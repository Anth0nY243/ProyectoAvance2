package servicio;

import excepciones.VoluntariadoException;
import modelo.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class SistemaGestionEventos implements Serializable {
    private static final long serialVersionUID = 1L;

    // ESTRUCTURAS DE DATOS (Módulos de los 4 integrantes)
    private HashMap<String, Usuario> usuarios; // O(1) Login - Módulo Anthony
    private TreeMap<LocalDate, Evento> eventos; // O(log n) Ordenamiento - Módulo Damián
    private Queue<Inscripcion> solicitudesPendientes; // FIFO - Módulo Sebastián
    private Stack<Inscripcion> historialAcciones; // LIFO Deshacer - Módulo Marco
    private HashSet<Integer> asistenciasValidadas; // Evita duplicados

    public SistemaGestionEventos() {
        usuarios = new HashMap<>();
        eventos = new TreeMap<>();
        solicitudesPendientes = new LinkedList<>();
        historialAcciones = new Stack<>();
        asistenciasValidadas = new HashSet<>();

        // Admin por defecto
        usuarios.put("admin", new Usuario(1, "Super Admin", "admin", "admin", RolUsuario.ADMINISTRADOR));
    }

    public Usuario iniciarSesion(String username, String password) throws VoluntariadoException {
        Usuario u = usuarios.get(username);
        if (u != null && u.getPassword().equals(password)) {
            return u;
        }
        throw new VoluntariadoException("Credenciales incorrectas.");
    }

    public void registrarUsuario(Usuario nuevo) throws VoluntariadoException {
        if (usuarios.containsKey(nuevo.getUsername())) {
            throw new VoluntariadoException("El usuario ya existe.");
        }
        usuarios.put(nuevo.getUsername(), nuevo);
    }

    public void crearEvento(Evento evento) {
        eventos.put(evento.getFecha(), evento); // Automáticamente se ordena
    }

    public void solicitarInscripcion(Usuario vol, Evento ev) throws VoluntariadoException {
        if (ev.getCuposDisponibles() <= 0) throw new VoluntariadoException("Evento lleno.");
        Inscripcion nueva = new Inscripcion(solicitudesPendientes.size() + 1, vol, ev);
        solicitudesPendientes.add(nueva);
    }

    public Queue<Inscripcion> getSolicitudesPendientes() { return solicitudesPendientes; }

    public void procesarSiguienteInscripcion(boolean aprobar) throws VoluntariadoException {
        if (solicitudesPendientes.isEmpty()) throw new VoluntariadoException("No hay solicitudes en la cola.");

        Inscripcion procesada = solicitudesPendientes.poll(); // Extrae FIFO
        if (aprobar) {
            procesada.aprobar();
        } else {
            procesada.rechazar();
        }
        historialAcciones.push(procesada); // Guarda LIFO
    }

    public void deshacerUltimaAccion() throws VoluntariadoException {
        if (historialAcciones.isEmpty()) throw new VoluntariadoException("No hay acciones para deshacer.");

        Inscripcion ultima = historialAcciones.pop(); // Extrae LIFO
        if (ultima.getEstado() == EstadoInscripcion.APROBADA) {
            ultima.getEvento().restaurarCupo();
        }
        ultima.setEstado(EstadoInscripcion.PENDIENTE);

        ((LinkedList<Inscripcion>) solicitudesPendientes).addFirst(ultima); // Devuelve al inicio de la cola
    }
    // MÓDULO 4: Marco Herrera (Gestión de Asistencia y Reportes)
    public List<Object[]> generarReporteGeneral() {
        List<Object[]> reporte = new ArrayList<>();

        // Recorremos el TreeMap de eventos para sacar las estadísticas
        for (Evento ev : eventos.values()) {
            Object[] filaDato = new Object[4];
            filaDato[0] = ev.getNombre();
            filaDato[1] = ev.getFecha().toString();
            filaDato[2] = ev.getCuposDisponibles();
            filaDato[3] = ev.isAsistenciaTomada() ? "Cerrado" : "Pendiente";

            reporte.add(filaDato);
        }

        return reporte;
    }
    // -------------------------------------------------------------
    // MÉTODOS PARA COMPATIBILIDAD CON PANELES GRÁFICOS
    // -------------------------------------------------------------

    public List<Evento> getEventosDisponibles() {
        // Devuelve los eventos del TreeMap como una Lista
        return new ArrayList<>(eventos.values());
    }

    public Evento buscarEventoPorId(int id) {
        for (Evento e : eventos.values()) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null; // Si no lo encuentra
    }

    public String getEstadoInscripcionUsuario(Usuario u, Evento e) {
        // Busca si el usuario ya está en la cola o en el historial
        for (Inscripcion i : solicitudesPendientes) {
            if (i.getVoluntario().getId() == u.getId() && i.getEvento().getId() == e.getId()) {
                return i.getEstado().toString();
            }
        }
        for (Inscripcion i : historialAcciones) {
            if (i.getVoluntario().getId() == u.getId() && i.getEvento().getId() == e.getId()) {
                return i.getEstado().toString();
            }
        }
        return "NO INSCRITO";
    }

    // Sobrecarga del método para aceptar el String de fecha que manda tu panel
    public void solicitarInscripcion(Usuario vol, Evento ev, String fechaSol) throws VoluntariadoException {
        if (ev.getCuposDisponibles() <= 0) throw new VoluntariadoException("Evento lleno.");
        Inscripcion nueva = new Inscripcion(solicitudesPendientes.size() + historialAcciones.size() + 1, vol, ev);
        solicitudesPendientes.add(nueva);
    }

    public List<Inscripcion> getAprobadosPorEvento(int idEvento) {
        List<Inscripcion> aprobados = new ArrayList<>();
        for (Inscripcion i : historialAcciones) {
            if (i.getEvento().getId() == idEvento && i.getEstado() == EstadoInscripcion.APROBADA) {
                aprobados.add(i);
            }
        }
        return aprobados;
    }

    public void cerrarAsistenciaEvento(int idEvento, List<Integer> idsPresentes, String coordinador) throws VoluntariadoException {
        Evento ev = buscarEventoPorId(idEvento);
        if (ev == null) throw new VoluntariadoException("Evento no encontrado.");

        ev.marcarAsistenciaTomada(coordinador);

        // Cruza los datos de las inscripciones con los IDs que asistieron
        for (Inscripcion i : historialAcciones) {
            if (i.getEvento().getId() == idEvento && i.getEstado() == EstadoInscripcion.APROBADA) {
                if (idsPresentes.contains(i.getId())) {
                    i.marcarAsistio();
                    asistenciasValidadas.add(i.getVoluntario().getId()); // Usa el HashSet para evitar duplicados
                }
            }
        }
    }
}