package modelo;

import java.io.Serializable;

public class RegistroAsistencia implements Serializable {
    private int id; // Identificador opcional
    private Inscripcion inscripcion;
    private String reporte; // Ej: "Presente", "Llegó tarde", etc.

    public RegistroAsistencia(Inscripcion inscripcion, String reporte) {
        this.inscripcion = inscripcion;
        this.reporte = reporte;
    }

    public Inscripcion getInscripcion() {
        return inscripcion;
    }

    public String getReporte() {
        return reporte;
    }
}