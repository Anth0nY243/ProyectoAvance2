package vista;

import modelo.Evento;
import servicio.SistemaGestionEventos;
import javax.swing.*;
import java.time.LocalDate;

public class PanelCrearEvento {
    private JPanel panelPrincipal;
    private JTextField txtNombre;
    private JTextField txtLugar;
    private JSpinner spinnerCupos; // Componente requerido por el avance
    private JButton btnCrear;

    private SistemaGestionEventos sistema;

    public PanelCrearEvento(SistemaGestionEventos sistema) {
        this.sistema = sistema;

        // Configuramos el Spinner para que solo acepte números de 1 a 1000
        spinnerCupos.setModel(new SpinnerNumberModel(20, 1, 1000, 1));

        btnCrear.addActionListener(e -> {
            int cupos = (int) spinnerCupos.getValue();
            Evento ev = new Evento((int)(Math.random()*100), txtNombre.getText(), cupos, LocalDate.now().plusDays(5), txtLugar.getText());
            sistema.crearEvento(ev);
            JOptionPane.showMessageDialog(null, "Evento guardado en el TreeMap cronológicamente.");
        });
    }

    public JPanel getPanelPrincipal() { return panelPrincipal; }
}