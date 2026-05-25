package vista;

import excepciones.VoluntariadoException;
import modelo.RolUsuario;
import modelo.Usuario;
import servicio.SistemaGestionEventos;
import javax.swing.*;

public class VentanaRegistro extends JDialog {
    private JPanel panelPrincipal;
    private JTextField txtNombre;
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JComboBox<RolUsuario> comboRol; // Componente requerido por el avance
    private JButton btnRegistrar;

    public VentanaRegistro(JFrame parent, SistemaGestionEventos sistema) {
        super(parent, "Registro de Usuario", true);
        setContentPane(panelPrincipal);
        setSize(350, 350);
        setLocationRelativeTo(parent);

        // Poblar el JComboBox
        comboRol.setModel(new DefaultComboBoxModel<>(RolUsuario.values()));

        btnRegistrar.addActionListener(e -> {
            try {
                Usuario u = new Usuario((int)(Math.random()*100), txtNombre.getText(), txtUser.getText(),
                        new String(txtPass.getPassword()), (RolUsuario) comboRol.getSelectedItem());
                sistema.registrarUsuario(u);
                JOptionPane.showMessageDialog(this, "Usuario creado exitosamente.");
                dispose();
            } catch (VoluntariadoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
    }
}