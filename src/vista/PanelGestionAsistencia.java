package vista;

import servicio.SistemaGestionEventos;
import modelo.Evento;
import modelo.Inscripcion;
import modelo.Usuario;
import excepciones.VoluntariadoException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class PanelGestionAsistencia {
    private JPanel panelAsistencia;
    private JComboBox<Evento> cbxEventos;
    private JButton btnCargar;
    private JTable tablaAsistencia;
    private JButton btnGuardarAsistencia;

    private SistemaGestionEventos sistema;
    private Usuario coordinadorActual;
    private DefaultTableModel modeloTabla;
    private Evento eventoSeleccionado;

    public PanelGestionAsistencia(SistemaGestionEventos sistema, Usuario coordinadorActual) {
        this.sistema = sistema;
        this.coordinadorActual = coordinadorActual;

        // 1. Configurar la tabla para que acepte Checkboxes
        String[] columnas = {"ID Inscripción", "Nombre Voluntario", "Cédula", "Asistió"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) {
                    return Boolean.class; // Esto hace que Swing dibuje un Checkbox
                }
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Solo permitimos editar la columna del Checkbox
            }
        };
        tablaAsistencia.setModel(modeloTabla);

        // 2. Cargar los eventos en el JComboBox al abrir el panel
        cargarComboBoxEventos();

        // 3. Acción del botón Cargar
        btnCargar.addActionListener(e -> {
            eventoSeleccionado = (Evento) cbxEventos.getSelectedItem();
            if (eventoSeleccionado == null) {
                JOptionPane.showMessageDialog(null, "Seleccione un evento.");
                return;
            }

            if (eventoSeleccionado.isAsistenciaTomada()) {
                JOptionPane.showMessageDialog(null, "La asistencia de este evento ya fue cerrada por: " + eventoSeleccionado.getCoordinadorAsistencia());
                btnGuardarAsistencia.setEnabled(false); // Bloqueamos el botón
            } else {
                btnGuardarAsistencia.setEnabled(true);
            }

            cargarVoluntariosAprobados();
        });

        // 4. Acción del botón Guardar Asistencia
        btnGuardarAsistencia.addActionListener(e -> {
            if (eventoSeleccionado == null) return;

            int confirmacion = JOptionPane.showConfirmDialog(null,
                    "¿Está seguro de cerrar la asistencia? Esta acción no se puede deshacer.",
                    "Confirmar Cierre", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    List<Integer> idsPresentes = new ArrayList<>();

                    // Recorremos la tabla para ver quién tiene el Checkbox marcado
                    for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                        boolean asistio = (Boolean) modeloTabla.getValueAt(i, 3);
                        if (asistio) {
                            int idInscripcion = (Integer) modeloTabla.getValueAt(i, 0);
                            idsPresentes.add(idInscripcion);
                        }
                    }

                    // Enviamos los IDs al sistema para que consolide (Uso interno de HashSet/Listas)
                    sistema.cerrarAsistenciaEvento(eventoSeleccionado.getId(), idsPresentes, coordinadorActual.getNombreCompleto());

                    JOptionPane.showMessageDialog(null, "Asistencia cerrada exitosamente. Guardado en el registro histórico.");
                    btnGuardarAsistencia.setEnabled(false);

                } catch (VoluntariadoException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void cargarComboBoxEventos() {
        cbxEventos.removeAllItems();
        List<Evento> eventosDisponibles = sistema.getEventosDisponibles();
        for (Evento ev : eventosDisponibles) {
            cbxEventos.addItem(ev);
        }
    }

    private void cargarVoluntariosAprobados() {
        modeloTabla.setRowCount(0); // Limpiar tabla

        List<Inscripcion> aprobados = sistema.getAprobadosPorEvento(eventoSeleccionado.getId());

        if (aprobados.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay voluntarios aprobados para este evento todavía.");
            return;
        }

        // Llenamos la tabla. El "false" inicializa el Checkbox desmarcado.
        for (Inscripcion ins : aprobados) {
            Object[] fila = {
                    ins.getId(),
                    ins.getVoluntario().getNombreCompleto(),
                    ins.getVoluntario().getCedula(),
                    false // Por defecto nadie ha llegado hasta que el coordinador lo marque
            };
            modeloTabla.addRow(fila);
        }
    }

    public JPanel getPanel() { return panelAsistencia; }
}