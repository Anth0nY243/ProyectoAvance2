package vista;

import servicio.SistemaGestionEventos;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelReporteAdmin extends PanelBase {
    private JTable tablaReporte;
    private DefaultTableModel modelo;

    public PanelReporteAdmin(SistemaGestionEventos sistema) {
        super(sistema);
        setLayout(new BorderLayout());

        JLabel lblTitulo = new JLabel("Reporte General de Eventos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);

        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Evento");
        modelo.addColumn("Total Solicitudes");
        modelo.addColumn("Aprobados");
        modelo.addColumn("Asistieron Realmente");
        modelo.addColumn("Estado Asistencia");

        tablaReporte = new JTable(modelo);
        add(new JScrollPane(tablaReporte), BorderLayout.CENTER);

        JPanel panelSur = new JPanel();
        JButton btnRefrescar = new JButton("Actualizar Datos");
        btnRefrescar.addActionListener(e -> cargarTabla());
        panelSur.add(btnRefrescar);
        add(panelSur, BorderLayout.SOUTH);

        cargarTabla();
    }

    @Override
    public void cargarTabla() {
        modelo.setRowCount(0);
        List<Object[]> datos = sistema.generarReporteGeneral();

        for (Object[] fila : datos) {
            modelo.addRow(fila);
        }
    }
}