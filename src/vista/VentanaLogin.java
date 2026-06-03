package vista;

import servicio.SistemaGestionEventos;
import modelo.Usuario;
import excepciones.VoluntariadoException;
import javax.swing.*;

public class VentanaLogin extends JFrame {
    private JPanel panelPrincipal;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private JButton btnRegistrarse;
    private SistemaGestionEventos sistema;

    public VentanaLogin(SistemaGestionEventos sistema) {
        this.sistema = sistema;
        setContentPane(panelPrincipal);
        setTitle("Conexión Voluntaria - Iniciar Sesión");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        btnIngresar.addActionListener(e -> {
            try {
                String user = txtUsername.getText();
                String pass = new String(txtPassword.getPassword());
                Usuario logueado = sistema.iniciarSesion(user, pass);
                JOptionPane.showMessageDialog(null, "Bienvenido " + logueado.getNombreCompleto());
                VentanaPrincipal vp = new VentanaPrincipal(sistema, logueado);
                vp.setVisible(true);
                dispose();
            } catch (VoluntariadoException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRegistrarse.addActionListener(e -> {
            VentanaRegistro vr = new VentanaRegistro(sistema, null);
            vr.setVisible(true);
        });
    }
}