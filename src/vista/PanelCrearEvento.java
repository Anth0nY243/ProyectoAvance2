package vista;

import servicio.SistemaGestionEventos;
import modelo.Evento;
import excepciones.VoluntariadoException;
import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class PanelCrearEvento {
    private JPanel panelEventos;
    private JTextField txtNombre;
    private JTextField txtLugar;
    private JTextField txtFecha;
    private JTextField txtHoraInicio;
    private JTextField txtHoraFin;
    private JSpinner spnCupos;
    private JTextField txtTipoRecurso;
    private JSpinner spnCantidadRecurso;
    private JButton btnGuardar;
    private JButton btnBuscar;
    private JButton btnModificar;
    private JButton btnEliminar;

    private SistemaGestionEventos sistema;
    private LocalDate fechaOriginalMemoria; // Necesario para modificar o eliminar en el TreeMap

    public PanelCrearEvento(SistemaGestionEventos sistema) {
        this.sistema = sistema;
        spnCupos.setModel(new SpinnerNumberModel(10, 1, 1000, 1));
        spnCantidadRecurso.setModel(new SpinnerNumberModel(1, 0, 1000, 1));

        btnGuardar.addActionListener(e -> {
            try {
                Evento ev = extraerDatosDePantalla();
                sistema.crearEvento(ev);
                JOptionPane.showMessageDialog(null, "Evento guardado. ID asignado: " + ev.getId());
            } catch (Exception ex) {
                mostrarErrorFormato(ex);
            }
        });

        btnBuscar.addActionListener(e -> {
            String buscar = JOptionPane.showInputDialog("Ingrese el nombre exacto del evento (Búsqueda Recursiva):");
            if(buscar != null && !buscar.isEmpty()){
                Evento enc = sistema.buscarEventoPorNombre(buscar);
                if(enc != null){
                    fechaOriginalMemoria = enc.getFecha(); // Guardamos la llave original

                    txtNombre.setText(enc.getNombre());
                    txtLugar.setText(enc.getLugar());
                    txtFecha.setText(enc.getFecha().toString());
                    txtHoraInicio.setText(enc.getHoraInicio().toString());
                    txtHoraFin.setText(enc.getHoraFin().toString());
                    spnCupos.setValue(enc.getCuposDisponibles());
                    txtTipoRecurso.setText(enc.getTipoRecurso());
                    spnCantidadRecurso.setValue(enc.getCantidadRecurso());

                    JOptionPane.showMessageDialog(null, "Evento encontrado.");
                } else {
                    JOptionPane.showMessageDialog(null, "No existe el evento.");
                }
            }
        });

        btnModificar.addActionListener(e -> {
            try {
                if (fechaOriginalMemoria == null) throw new VoluntariadoException("Primero busque un evento para modificar.");

                Evento modificado = extraerDatosDePantalla();
                // Elimina el viejo y guarda el nuevo para mantener orden en el TreeMap
                sistema.eliminarEvento(fechaOriginalMemoria);
                sistema.crearEvento(modificado);

                fechaOriginalMemoria = modificado.getFecha();
                JOptionPane.showMessageDialog(null, "Evento actualizado.");
            } catch (Exception ex) {
                mostrarErrorFormato(ex);
            }
        });

        btnEliminar.addActionListener(e -> {
            try {
                if (fechaOriginalMemoria == null) throw new VoluntariadoException("Primero busque un evento para eliminar.");
                sistema.eliminarEvento(fechaOriginalMemoria);
                JOptionPane.showMessageDialog(null, "Evento eliminado del sistema.");
                fechaOriginalMemoria = null;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private Evento extraerDatosDePantalla() {
        return new Evento(
                txtNombre.getText(),
                txtLugar.getText(),
                LocalDate.parse(txtFecha.getText()), // Formato AAAA-MM-DD
                LocalTime.parse(txtHoraInicio.getText()), // Formato HH:mm
                LocalTime.parse(txtHoraFin.getText()),
                (Integer) spnCupos.getValue(),
                txtTipoRecurso.getText(),
                (Integer) spnCantidadRecurso.getValue()
        );
    }

    private void mostrarErrorFormato(Exception ex) {
        if (ex instanceof VoluntariadoException) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Error de formato. Fechas: AAAA-MM-DD. Horas: HH:mm", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel getPanel() { return panelEventos; }
}