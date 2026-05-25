package vista;

import excepciones.VoluntariadoException;
import modelo.Evento;
import modelo.RolUsuario;
import modelo.Usuario;
import servicio.GestorArchivos;
import servicio.SistemaGestionEventos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelVerEventos extends JPanel {
    private SistemaGestionEventos sistema;
    private Usuario usuarioLogueado;
    private JTable tablaEventos;
    private DefaultTableModel modelo;

    public PanelVerEventos(SistemaGestionEventos sistema, Usuario usuarioLogueado) {
        this.sistema = sistema;
        this.usuarioLogueado = usuarioLogueado;

        setLayout(new BorderLayout());

        // Título
        JLabel lblTitulo = new JLabel("Cartelera de Eventos Disponibles", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Configuración de la Tabla
        // AHORA MOSTRAMOS HORA INICIO Y FIN
        String[] columnas = {"ID", "Nombre", "Fecha", "Horario", "Lugar", "Cupos", "Estado"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Que no se pueda editar directo en la tabla
            }
        };

        cargarDatosTabla();

        tablaEventos = new JTable(modelo);
        add(new JScrollPane(tablaEventos), BorderLayout.CENTER);

        // Botón de Inscripción (Solo para Voluntarios)
        if (usuarioLogueado.getRol() == RolUsuario.VOLUNTARIO) {
            JPanel panelBoton = new JPanel();
            JButton btnInscribirse = new JButton("Inscribirse al Evento Seleccionado");

            btnInscribirse.addActionListener(e -> inscribirseEvento());

            panelBoton.add(btnInscribirse);
            add(panelBoton, BorderLayout.SOUTH);
        }
    }

    private void cargarDatosTabla() {
        modelo.setRowCount(0); // Limpiar tabla
        for (Evento e : sistema.getEventosDisponibles()) {
            String estado = sistema.getEstadoInscripcionUsuario(usuarioLogueado, e);

            Object[] fila = {
                    e.getId(),
                    e.getNombre(),
                    e.getFecha(),
                    e.getHoraInicio() + " - " + e.getHoraFin(), // Mostramos rango de horas
                    e.getLugar(),
                    e.getCuposDisponibles(),
                    estado
            };
            modelo.addRow(fila);
        }
    }

    private void inscribirseEvento() {
        int filaSeleccionada = tablaEventos.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un evento de la lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idEvento = (int) modelo.getValueAt(filaSeleccionada, 0);
        Evento eventoSeleccionado = sistema.buscarEventoPorId(idEvento);

        if (eventoSeleccionado != null) {
            // 1. PEDIMOS LA FECHA AL USUARIO
            String fechaInscripcion = JOptionPane.showInputDialog(this,
                    "Ingrese la fecha de su inscripción (DD/MM/AAAA):",
                    "Confirmar Inscripción",
                    JOptionPane.QUESTION_MESSAGE);

            // Si el usuario cancela o lo deja vacío, no hacemos nada
            if (fechaInscripcion == null || fechaInscripcion.trim().isEmpty()) {
                return;
            }

            try {
                // 2. LLAMAMOS AL MÉTODO CON LOS 3 ARGUMENTOS (Usuario, Evento, Fecha)
                sistema.solicitarInscripcion(usuarioLogueado, eventoSeleccionado, fechaInscripcion);

                // Guardamos cambios
                GestorArchivos.guardarDatos(sistema);

                JOptionPane.showMessageDialog(this, "Solicitud enviada exitosamente con fecha: " + fechaInscripcion);
                cargarDatosTabla(); // Refrescar para ver el estado "Pendiente"

            } catch (VoluntariadoException ex) {
                // Manejo de errores (Fecha mal, cupos llenos, etc.)
                JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo inscribir", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}