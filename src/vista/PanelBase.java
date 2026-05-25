package vista;

import javax.swing.JPanel;
import servicio.SistemaGestionEventos;

public abstract class PanelBase extends JPanel {
    protected SistemaGestionEventos sistema;
    public PanelBase(SistemaGestionEventos sistema) {
        this.sistema = sistema;
    }
    public abstract void cargarTabla();
}