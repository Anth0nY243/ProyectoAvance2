package modelo;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Evento implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int generadorId = 1;

    public static void setGeneradorId(int id) { generadorId = id; }

    private int id;
    private String nombre;
    private String lugar;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private int cuposTotales;
    private int cuposDisponibles;
    private String tipoRecurso;
    private int cantidadRecurso;
    private boolean asistenciaTomada = false;
    private String coordinadorAsistencia = null;

    public Evento(String nombre, String lugar, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, int cuposTotales, String tipoRecurso, int cantidadRecurso) {
        this.id = generadorId++;
        this.nombre = nombre;
        this.lugar = lugar;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.cuposTotales = cuposTotales;
        this.cuposDisponibles = cuposTotales;
        this.tipoRecurso = tipoRecurso;
        this.cantidadRecurso = cantidadRecurso;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public int getCuposDisponibles() { return cuposDisponibles; }
    public void modificarCupos(int variacion) { this.cuposDisponibles += variacion; }
    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public String getTipoRecurso() { return tipoRecurso; }
    public int getCantidadRecurso() { return cantidadRecurso; }
    public boolean isAsistenciaTomada() { return asistenciaTomada; }
    public void setAsistenciaTomada(boolean asistenciaTomada) { this.asistenciaTomada = asistenciaTomada; }
    public String getCoordinadorAsistencia() { return coordinadorAsistencia; }
    public void setCoordinadorAsistencia(String coordinadorAsistencia) { this.coordinadorAsistencia = coordinadorAsistencia; }

    @Override
    public String toString() { return id + ". " + nombre + " (" + cuposDisponibles + " cupos)"; }
}