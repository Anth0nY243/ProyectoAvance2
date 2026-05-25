package modelo;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Evento implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String nombre;
    private int cuposDisponibles;
    private LocalDate fecha;
    private String horaInicio;
    private String horaFin;
    private String lugar;
    private List<Requisito> requisitos;
    private boolean asistenciaTomada;
    private String coordinadorAsistencia;

    public Evento(int id, String nombre, int cuposDisponibles, LocalDate fecha, String lugar) {
        this.id = id;
        this.nombre = nombre;
        this.cuposDisponibles = cuposDisponibles;
        this.fecha = fecha;
        this.horaInicio = "08:00"; // Por defecto
        this.horaFin = "12:00";    // Por defecto
        this.lugar = lugar;
        this.requisitos = new ArrayList<>();
        this.asistenciaTomada = false;
        this.coordinadorAsistencia = "N/A";
    }

    public void reducirCupo() { if (cuposDisponibles > 0) cuposDisponibles--; }
    public void restaurarCupo() { cuposDisponibles++; }
    public void marcarAsistenciaTomada(String coordinador) {
        this.asistenciaTomada = true;
        this.coordinadorAsistencia = coordinador;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public LocalDate getFecha() { return fecha; }
    public int getCuposDisponibles() { return cuposDisponibles; }
    public String getHoraInicio() { return horaInicio; }
    public String getHoraFin() { return horaFin; }
    public String getLugar() { return lugar; }
    public boolean isAsistenciaTomada() { return asistenciaTomada; }
    public String getCoordinadorAsistencia() { return coordinadorAsistencia; }
}