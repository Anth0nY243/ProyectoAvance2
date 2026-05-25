package vista;

import excepciones.VoluntariadoException;
import modelo.Inscripcion;
import servicio.SistemaGestionEventos;
import javax.swing.*;

public class PanelGestionInscripciones {
    private JPanel panelPrincipal;
    private JList<String> listCola; // Componente requerido (Muestra la Queue)
    private JButton btnAprobar;
    private JButton btnRechazar;
    private JButton btnDeshacer; // Ejecuta el Stack

    private SistemaGestionEventos sistema;
    private DefaultListModel<String> modeloLista;

    public PanelGestionInscripciones(SistemaGestionEventos sistema) {
        this.sistema = sistema;
        modeloLista = new DefaultListModel<>();
        listCola.setModel(modeloLista);

        actualizarVistaCola();

        btnAprobar.addActionListener(e -> procesar(true));
        btnRechazar.addActionListener(e -> procesar(false));

        btnDeshacer.addActionListener(e -> {
            try {
                sistema.deshacerUltimaAccion(); // Saca de la Pila (LIFO)
                actualizarVistaCola();
                JOptionPane.showMessageDialog(null, "Acción deshecha con éxito (Stack LIFO).");
            } catch (VoluntariadoException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });
    }

    private void procesar(boolean aprobar) {
        try {
            sistema.procesarSiguienteInscripcion(aprobar); // Saca de la Cola (FIFO)
            actualizarVistaCola();
        } catch (VoluntariadoException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void actualizarVistaCola() {
        modeloLista.clear();
        int turno = 1;
        for (Inscripcion i : sistema.getSolicitudesPendientes()) {
            modeloLista.addElement("Turno " + turno + " - " + i.getVoluntario().getNombreCompleto() + " (Evento: " + i.getEvento().getNombre() + ")");
            turno++;
        }
    }

    public JPanel getPanelPrincipal() { return panelPrincipal; }
}