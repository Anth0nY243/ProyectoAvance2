package modelo;
import java.io.Serializable;

public class Inscripcion implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int generadorIdInscripcion = 1;

    public static void setGeneradorIdInscripcion(int id) { generadorIdInscripcion = id; }

    private int id;
    private Usuario voluntario;
    private Evento evento;
    private EstadoInscripcion estado;
    private String motivacion;

    public Inscripcion(Usuario voluntario, Evento evento, String motivacion) {
        this.id = generadorIdInscripcion++;
        this.voluntario = voluntario;
        this.evento = evento;
        this.motivacion = motivacion;
        this.estado = EstadoInscripcion.PENDIENTE;
    }

    public int getId() { return id; }
    public Usuario getVoluntario() { return voluntario; }
    public Evento getEvento() { return evento; }
    public String getMotivacion() { return motivacion; }
    public EstadoInscripcion getEstado() { return estado; }
    public void setEstado(EstadoInscripcion estado) { this.estado = estado; }

    @Override
    public String toString() { return id + ". " + voluntario.getNombreCompleto() + " -> " + evento.getNombre(); }
}