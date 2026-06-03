package vista;

import servicio.SistemaGestionEventos;
import modelo.Inscripcion;
import excepciones.VoluntariadoException;
import javax.swing.*;
import java.util.Queue;

public class PanelGestionInscripciones {
    private JPanel panelInscripciones;
    private JList<Inscripcion> listFilaEspera;
    private JButton btnAprobar;
    private JButton btnRechazar;
    private JButton btnDeshacer;
    private JButton btnRefrescar;
    private SistemaGestionEventos sistema;
    private DefaultListModel<Inscripcion> listModel;

    public PanelGestionInscripciones(SistemaGestionEventos sistema) {
        this.sistema = sistema;
        listModel = new DefaultListModel<>();
        listFilaEspera.setModel(listModel);

        btnRefrescar.addActionListener(e -> actualizarLista());

        btnAprobar.addActionListener(e -> procesar(true));
        btnRechazar.addActionListener(e -> procesar(false));

        btnDeshacer.addActionListener(e -> {
            try {
                sistema.deshacerUltimaAccion(); // Devuelve el cupo al evento y pone al usuario en la Cola
                actualizarLista();
                JOptionPane.showMessageDialog(null, "Acción revertida (LIFO). El cupo fue devuelto al evento.");
            } catch (VoluntariadoException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void procesar(boolean aprobar) {
        try {
            sistema.procesarSiguienteInscripcion(aprobar); // Resta el cupo automáticamente si es true
            actualizarLista();
            String msj = aprobar ? "Aprobado (Se restó 1 cupo disponible)" : "Rechazado";
            JOptionPane.showMessageDialog(null, "Procesado correctamente: " + msj);
        } catch (VoluntariadoException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarLista() {
        listModel.clear();
        Queue<Inscripcion> fila = sistema.getFilaEspera();
        for (Inscripcion ins : fila) {
            listModel.addElement(ins);
        }
    }

    public JPanel getPanel() { return panelInscripciones; }
}