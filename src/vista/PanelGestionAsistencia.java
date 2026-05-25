package vista;

import modelo.Evento;
import modelo.Inscripcion;
import modelo.Usuario;
import servicio.GestorArchivos;
import servicio.SistemaGestionEventos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelGestionAsistencia extends PanelBase {
    private JTable tablaEventos;
    private DefaultTableModel modeloEventos;
    private Usuario coordinadorActual;

    public PanelGestionAsistencia(SistemaGestionEventos sistema, Usuario coordinador) {
        super(sistema);
        this.coordinadorActual = coordinador;
        setLayout(new BorderLayout());

        // --- TABLA DE EVENTOS ---
        modeloEventos = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        modeloEventos.addColumn("ID");
        modeloEventos.addColumn("Evento");
        modeloEventos.addColumn("Estado Asistencia");
        modeloEventos.addColumn("Responsable");

        tablaEventos = new JTable(modeloEventos);
        add(new JScrollPane(tablaEventos), BorderLayout.CENTER);

        JPanel panelSur = new JPanel();
        JButton btnTomarLista = new JButton("Tomar / Ver Asistencia");
        btnTomarLista.addActionListener(e -> abrirDialogoAsistencia());

        panelSur.add(btnTomarLista);
        add(panelSur, BorderLayout.SOUTH);

        cargarTabla();
    }

    @Override
    public void cargarTabla() {
        modeloEventos.setRowCount(0);
        for (Evento ev : sistema.getEventosDisponibles()) {
            String estado = ev.isAsistenciaTomada() ? "ASISTENCIA TOMADA" : "Pendiente";
            String responsable = ev.isAsistenciaTomada() ? ev.getCoordinadorAsistencia() : "-";

            modeloEventos.addRow(new Object[]{ev.getId(), ev.getNombre(), estado, responsable});
        }
    }

    private void abrirDialogoAsistencia() {
        int fila = tablaEventos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un evento.");
            return;
        }

        int idEvento = (int) modeloEventos.getValueAt(fila, 0);
        Evento evento = sistema.buscarEventoPorId(idEvento);

        if (evento.isAsistenciaTomada()) {
            JOptionPane.showMessageDialog(this, "La asistencia ya fue registrada por: " + evento.getCoordinadorAsistencia());
            // Opcional: Podrías mostrar la lista solo lectura aquí si quisieras
            return;
        }

        // Obtener voluntarios aprobados
        List<Inscripcion> aprobados = sistema.getAprobadosPorEvento(idEvento);
        if (aprobados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay voluntarios aprobados para este evento aún.");
            return;
        }

        // --- DIÁLOGO PARA MARCAR CASILLAS ---
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Asistencia: " + evento.getNombre(), true);
        dialogo.setSize(400, 500);
        dialogo.setLayout(new BorderLayout());
        dialogo.setLocationRelativeTo(this);

        // Modelo de tabla con Checkbox
        DefaultTableModel modeloCheck = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 2 ? Boolean.class : String.class;
            }
        };
        modeloCheck.addColumn("ID Inscripcion");
        modeloCheck.addColumn("Voluntario");
        modeloCheck.addColumn("¿Asistió?"); // Columna booleana = Checkbox automático

        for (Inscripcion i : aprobados) {
            modeloCheck.addRow(new Object[]{i.getId(), i.getVoluntario().getNombreCompleto(), false});
        }

        JTable tablaCheck = new JTable(modeloCheck);
        dialogo.add(new JScrollPane(tablaCheck), BorderLayout.CENTER);

        JButton btnGuardar = new JButton("Guardar Asistencia Definitiva");
        btnGuardar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialogo,
                    "¿Está seguro? Esta acción cerrará la asistencia del evento.",
                    "Confirmar", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                List<Integer> presentes = new ArrayList<>();
                for (int i = 0; i < modeloCheck.getRowCount(); i++) {
                    boolean asistio = (boolean) modeloCheck.getValueAt(i, 2);
                    if (asistio) {
                        presentes.add((Integer) modeloCheck.getValueAt(i, 0));
                    }
                }

                try {
                    // Usamos el nombre real del coordinador logueado en lugar del texto quemado
                    sistema.cerrarAsistenciaEvento(idEvento, presentes, coordinadorActual.getNombreCompleto());

                    // Guardamos en el archivo .dat inmediatamente
                    GestorArchivos.guardarDatos(sistema);

                    // Mostramos un solo mensaje de éxito
                    JOptionPane.showMessageDialog(dialogo, "Asistencia guardada y cerrada correctamente.");
                    dialogo.dispose();
                    cargarTabla(); // Refrescar la tabla principal

                } catch (excepciones.VoluntariadoException ex) {
                    JOptionPane.showMessageDialog(dialogo, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        dialogo.add(btnGuardar, BorderLayout.SOUTH);
        dialogo.setVisible(true);
    }
}