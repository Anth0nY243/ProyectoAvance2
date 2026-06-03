package servicio;

import modelo.*;
import excepciones.VoluntariadoException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class SistemaGestionEventos implements Serializable {
    private static final long serialVersionUID = 1L;

    private HashMap<String, Usuario> usuarios;
    private TreeMap<LocalDate, Evento> eventos;
    private Queue<Inscripcion> filaEspera;
    private Stack<Inscripcion> historialAcciones;
    private List<Inscripcion> historialCompleto;

    public SistemaGestionEventos() {
        usuarios = new HashMap<>();
        eventos = new TreeMap<>();
        filaEspera = new LinkedList<>();
        historialAcciones = new Stack<>();
        historialCompleto = new ArrayList<>();

        Usuario admin = new Usuario("Admin Principal", "0000000000", "admin", "admin123", RolUsuario.ADMINISTRADOR);
        usuarios.put(admin.getUsername(), admin);

        Usuario coord = new Usuario("Carlos Coordinador", "1111111111", "coord1", "1234", RolUsuario.COORDINADOR);
        usuarios.put(coord.getUsername(), coord);

        Usuario vol1 = new Usuario("Ana Voluntaria", "2222222222", "vol1", "1234", RolUsuario.VOLUNTARIO);
        Usuario vol2 = new Usuario("Luis Voluntario", "3333333333", "vol2", "1234", RolUsuario.VOLUNTARIO);
        usuarios.put(vol1.getUsername(), vol1);
        usuarios.put(vol2.getUsername(), vol2);

        Evento ev1 = new Evento("Campaña de Reforestación", "Parque Metropolitano", LocalDate.now().plusDays(5), LocalTime.of(8, 0), LocalTime.of(13, 0), 20, "Palas", 15);
        eventos.put(ev1.getFecha(), ev1);

        Inscripcion insPrueba = new Inscripcion(vol1, ev1, "Quiero ayudar a plantar árboles");
        filaEspera.offer(insPrueba);
        historialCompleto.add(insPrueba);
    }

    private void validarDatosUsuario(String nombre, String cedula) {
        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) throw new VoluntariadoException("El nombre no puede contener números.");
        if (!cedula.matches("^[0-9]{10}$")) throw new VoluntariadoException("La cédula debe contener exactamente 10 números.");
    }

    public Usuario iniciarSesion(String username, String password) {
        if (!usuarios.containsKey(username) || !usuarios.get(username).getPassword().equals(password)) {
            throw new VoluntariadoException("Credenciales incorrectas.");
        }
        return usuarios.get(username);
    }

    public void registrarUsuario(Usuario adminActual, Usuario nuevoUser) {
        if (nuevoUser.getRol() == RolUsuario.COORDINADOR) {
            if (adminActual == null || adminActual.getRol() != RolUsuario.ADMINISTRADOR) {
                throw new VoluntariadoException("Solo el Administrador puede registrar coordinadores.");
            }
        }
        validarDatosUsuario(nuevoUser.getNombreCompleto(), nuevoUser.getCedula());
        for (Usuario u : usuarios.values()) {
            if (u.getUsername().equals(nuevoUser.getUsername())) throw new VoluntariadoException("Usuario en uso.");
            if (u.getCedula().equals(nuevoUser.getCedula())) throw new VoluntariadoException("Cédula registrada.");
        }
        usuarios.put(nuevoUser.getUsername(), nuevoUser);
    }

    public void crearEvento(Evento evento) {
        for (Evento e : eventos.values()) {
            if (e.getNombre().equalsIgnoreCase(evento.getNombre())) throw new VoluntariadoException("El evento ya existe.");
        }
        eventos.put(evento.getFecha(), evento);
    }

    public void eliminarEvento(LocalDate fecha) {
        if (!eventos.containsKey(fecha)) throw new VoluntariadoException("El evento no existe.");
        eventos.remove(fecha);
    }

    public Evento buscarEventoPorNombre(String nombre) {
        List<Evento> listaEventos = new ArrayList<>(eventos.values());
        return busquedaRecursiva(listaEventos, nombre, 0);
    }

    private Evento busquedaRecursiva(List<Evento> lista, String nombre, int indice) {
        if (indice >= lista.size()) return null;
        if (lista.get(indice).getNombre().equalsIgnoreCase(nombre)) return lista.get(indice);
        return busquedaRecursiva(lista, nombre, indice + 1);
    }

    public void solicitarInscripcion(Usuario voluntario, Evento evento, String motivacion) {
        for (Inscripcion ins : historialCompleto) {
            if (ins.getVoluntario().getUsername().equals(voluntario.getUsername()) && ins.getEvento().getId() == evento.getId()) {
                if (ins.getEstado() == EstadoInscripcion.PENDIENTE) throw new VoluntariadoException("Ya tienes una solicitud pendiente.");
                else if (ins.getEstado() == EstadoInscripcion.RECHAZADA) throw new VoluntariadoException("Tu solicitud fue rechazada.");
            }
        }
        if (evento.getCuposDisponibles() <= 0) throw new VoluntariadoException("No hay cupos disponibles.");

        Inscripcion nueva = new Inscripcion(voluntario, evento, motivacion);
        filaEspera.offer(nueva);
        historialCompleto.add(nueva);
    }

    public void procesarSiguienteInscripcion(boolean aprobar) {
        if (filaEspera.isEmpty()) throw new VoluntariadoException("La fila de espera está vacía.");
        Inscripcion procesada = filaEspera.peek();
        if (aprobar) {
            if (procesada.getEvento().getCuposDisponibles() <= 0) throw new VoluntariadoException("El evento no tiene cupos.");
            procesada.getEvento().modificarCupos(-1);
        }
        procesada = filaEspera.poll();
        procesada.setEstado(aprobar ? EstadoInscripcion.APROBADA : EstadoInscripcion.RECHAZADA);
        historialAcciones.push(procesada);
    }

    public void deshacerUltimaAccion() {
        if (historialAcciones.isEmpty()) throw new VoluntariadoException("No hay acciones para deshacer.");
        Inscripcion revertida = historialAcciones.pop();
        if (revertida.getEstado() == EstadoInscripcion.APROBADA) {
            revertida.getEvento().modificarCupos(1);
        }
        revertida.setEstado(EstadoInscripcion.PENDIENTE);
        LinkedList<Inscripcion> temporal = new LinkedList<>(filaEspera);
        temporal.addFirst(revertida);
        filaEspera = temporal;
    }

    public Queue<Inscripcion> getFilaEspera() { return filaEspera; }
    public List<Evento> getEventosDisponibles() { return new ArrayList<>(eventos.values()); }
    public List<Inscripcion> getHistorialCompleto() { return historialCompleto; }

    public Evento buscarEventoPorId(int id) {
        for (Evento e : eventos.values()) {
            if (e.getId() == id) return e;
        }
        return null;
    }

    public String getEstadoInscripcionUsuario(Usuario u, Evento e) {
        for (Inscripcion ins : historialCompleto) {
            if (ins.getVoluntario().getUsername().equals(u.getUsername()) && ins.getEvento().getId() == e.getId()) {
                return ins.getEstado().toString();
            }
        }
        return "NO INSCRITO";
    }

    public List<Inscripcion> getAprobadosPorEvento(int idEvento) {
        List<Inscripcion> aprobados = new ArrayList<>();
        for (Inscripcion ins : historialCompleto) {
            if (ins.getEvento().getId() == idEvento && ins.getEstado() == EstadoInscripcion.APROBADA) aprobados.add(ins);
        }
        return aprobados;
    }

    public void cerrarAsistenciaEvento(int idEvento, List<Integer> idsPresentes, String nombreCoordinador) {
        Evento ev = buscarEventoPorId(idEvento);
        if (ev == null) throw new VoluntariadoException("Evento no encontrado.");
        ev.setAsistenciaTomada(true);
        ev.setCoordinadorAsistencia(nombreCoordinador);
        for (Inscripcion ins : historialCompleto) {
            if (ins.getEvento().getId() == idEvento && ins.getEstado() == EstadoInscripcion.APROBADA) {
                if (idsPresentes.contains(ins.getId())) ins.setEstado(EstadoInscripcion.ASISTIO);
            }
        }
    }

    // ==========================================
    // MÓDULO DE REPORTES (PARA EL ADMIN)
    // ==========================================
    public List<Object[]> generarReporteGeneral() {
        List<Object[]> filasReporte = new ArrayList<>();

        for (Evento ev : eventos.values()) {
            int aprobados = 0;
            int asistieron = 0;

            // Calculamos estadísticas cruzando datos con el historial
            for (Inscripcion ins : historialCompleto) {
                if (ins.getEvento().getId() == ev.getId()) {
                    if (ins.getEstado() == EstadoInscripcion.APROBADA) aprobados++;
                    if (ins.getEstado() == EstadoInscripcion.ASISTIO) asistieron++;
                }
            }

            // Creamos la fila lista para ser mostrada en el JTable del Admin
            Object[] fila = new Object[]{
                    ev.getId(),
                    ev.getNombre(),
                    ev.getFecha().toString(),
                    ev.isAsistenciaTomada() ? "Cerrada" : "Abierta",
                    aprobados,
                    asistieron
            };
            filasReporte.add(fila);
        }
        return filasReporte;
    }
}