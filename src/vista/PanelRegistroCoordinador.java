package vista;

import servicio.SistemaGestionEventos;
import modelo.Usuario;
import modelo.RolUsuario;
import excepciones.VoluntariadoException;
import javax.swing.*;

public class PanelRegistroCoordinador {
    private JPanel panelRegCoord;
    private JTextField txtNombre;
    private JTextField txtCedula;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnGuardarCoord;

    private SistemaGestionEventos sistema;
    private Usuario adminActual;

    public PanelRegistroCoordinador(SistemaGestionEventos sistema, Usuario adminActual) {
        this.sistema = sistema;
        this.adminActual = adminActual;

        btnGuardarCoord.addActionListener(e -> {
            try {
                Usuario nuevoCoord = new Usuario(
                        txtNombre.getText(),
                        txtCedula.getText(),
                        txtUsername.getText(),
                        new String(txtPassword.getPassword()),
                        RolUsuario.COORDINADOR
                );

                // Pasa el adminActual para validar que tiene permisos
                sistema.registrarUsuario(adminActual, nuevoCoord);
                JOptionPane.showMessageDialog(null, "Coordinador registrado exitosamente.");

                // Limpiar campos
                txtNombre.setText("");
                txtCedula.setText("");
                txtUsername.setText("");
                txtPassword.setText("");
            } catch (VoluntariadoException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public JPanel getPanel() { return panelRegCoord; }
}