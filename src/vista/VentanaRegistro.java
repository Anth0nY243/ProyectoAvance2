package vista;

import servicio.SistemaGestionEventos;
import modelo.Usuario;
import modelo.RolUsuario;
import excepciones.VoluntariadoException;
import javax.swing.*;

public class VentanaRegistro extends JFrame {
    private JPanel panelRegistro;
    private JTextField txtNombreCompleto;
    private JTextField txtCedula;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<RolUsuario> cbxRol;
    private JButton btnGuardar;
    private SistemaGestionEventos sistema;
    private Usuario usuarioActual;

    public VentanaRegistro(SistemaGestionEventos sistema, Usuario usuarioActual) {
        this.sistema = sistema;
        this.usuarioActual = usuarioActual;

        setContentPane(panelRegistro);
        setTitle("Registro de Usuarios");
        setSize(450, 400);
        setLocationRelativeTo(null);

        cbxRol.setModel(new DefaultComboBoxModel<>(new RolUsuario[]{RolUsuario.VOLUNTARIO}));

        btnGuardar.addActionListener(e -> {
            try {
                RolUsuario rolSel = (RolUsuario) cbxRol.getSelectedItem();
                Usuario nuevo = new Usuario(
                        txtNombreCompleto.getText(),
                        txtCedula.getText(),
                        txtUsername.getText(),
                        new String(txtPassword.getPassword()),
                        rolSel
                );

                sistema.registrarUsuario(usuarioActual, nuevo);
                JOptionPane.showMessageDialog(null, "Registro exitoso. Cédula y datos validados.");
                dispose();
            } catch (VoluntariadoException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}