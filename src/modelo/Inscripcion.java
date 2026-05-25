package modelo;
import java.io.Serializable;
import java.time.LocalDate;

public class Inscripcion implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private Usuario voluntario;
    private Evento evento;
    private EstadoInscripcion estado;
    private LocalDate fechaInscripcion;

    public Inscripcion(int id, Usuario voluntario, Evento evento) {
        this.id = id;
        this.voluntario = voluntario;
        this.evento = evento;
        this.estado = EstadoInscripcion.PENDIENTE;
        this.fechaInscripcion = LocalDate.now();
    }

    public void aprobar() {
        this.estado = EstadoInscripcion.APROBADA;
        this.evento.reducirCupo();
    }

    public void rechazar() { this.estado = EstadoInscripcion.RECHAZADA; }
    public void marcarAsistio() { this.estado = EstadoInscripcion.ASISTIO; }

    public int getId() { return id; }
    public Usuario getVoluntario() { return voluntario; }
    public Evento getEvento() { return evento; }
    public EstadoInscripcion getEstado() { return estado; }
    public void setEstado(EstadoInscripcion estado) { this.estado = estado; }
}