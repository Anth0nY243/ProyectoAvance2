package vista;

import servicio.SistemaGestionEventos;
import modelo.Usuario;
import modelo.Evento;
import excepciones.VoluntariadoException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class PanelInscripcionVoluntario {
    private JPanel panelInscripcionVol;
    private JTable tablaEventos;
    private JTextField txtMotivacion;
    private JButton btnInscribirse;
    private JButton btnActualizar;

    private SistemaGestionEventos sistema;
    private Usuario voluntarioActual;
    private DefaultTableModel modeloTabla;

    public PanelInscripcionVoluntario(SistemaGestionEventos sistema, Usuario voluntarioActual) {
        this.sistema = sistema;
        this.voluntarioActual = voluntarioActual;

        // Configurar la tabla
        String[] columnas = {"ID", "Nombre", "Lugar", "Fecha", "Cupos Disponibles", "Tu Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Tabla de solo lectura
        };
        tablaEventos.setModel(modeloTabla);

        cargarEventos();

        btnActualizar.addActionListener(e -> cargarEventos());

        btnInscribirse.addActionListener(e -> {
            int filaSeleccionada = tablaEventos.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(null, "Por favor, seleccione un evento de la tabla.");
                return;
            }

            try {
                int idEvento = (int) tablaEventos.getValueAt(filaSeleccionada, 0);
                Evento evento = sistema.buscarEventoPorId(idEvento);
                String motiv = txtMotivacion.getText();

                sistema.solicitarInscripcion(voluntarioActual, evento, motiv);
                JOptionPane.showMessageDialog(null, "Inscripción enviada. Estado: PENDIENTE.");
                txtMotivacion.setText("");
                cargarEventos(); // Refresca la tabla para ver el nuevo estado
            } catch (VoluntariadoException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void cargarEventos() {
        modeloTabla.setRowCount(0); // Limpiar tabla
        List<Evento> lista = sistema.getEventosDisponibles();

        for (Evento ev : lista) {
            String estadoUsuario = sistema.getEstadoInscripcionUsuario(voluntarioActual, ev);
            Object[] fila = {
                    ev.getId(),
                    ev.getNombre(),
                    ev.getLugar(),
                    ev.getFecha().toString(),
                    ev.getCuposDisponibles(),
                    estadoUsuario
            };
            modeloTabla.addRow(fila);
        }
    }

    public JPanel getPanel() { return panelInscripcionVol; }
}