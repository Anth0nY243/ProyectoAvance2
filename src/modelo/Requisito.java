package modelo;
import java.io.Serializable;

public class Requisito implements Serializable {
    private static final long serialVersionUID = 1L;
    private String descripcion;
    private int cantidad;
    private TipoRequisito tipo;
    private EstadoRequisito estado;
    private String proveedor;

    public Requisito(String descripcion, int cantidad, TipoRequisito tipo) {
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.tipo = tipo;
        this.estado = EstadoRequisito.PENDIENTE;
        this.proveedor = "N/A";
    }

    public void aprobar(String proveedor) {
        this.estado = EstadoRequisito.APROBADO;
        this.proveedor = proveedor;
    }
}