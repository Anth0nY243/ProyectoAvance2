package vista;

import excepciones.VoluntariadoException;
import modelo.Usuario;
import servicio.SistemaGestionEventos;
import javax.swing.*;

public class VentanaLogin extends JFrame {
    private JPanel panelPrincipal;
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;
    private JButton btnRegistro;

    private SistemaGestionEventos sistema;

    public VentanaLogin(SistemaGestionEventos sistema) {
        this.sistema = sistema;
        setContentPane(panelPrincipal);
        setTitle("Conexión Voluntaria - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        btnLogin.addActionListener(e -> hacerLogin());

        btnRegistro.addActionListener(e -> {
            VentanaRegistro vr = new VentanaRegistro(this, sistema);
            vr.setVisible(true);
        });
    }

    private void hacerLogin() {
        try {
            Usuario logueado = sistema.iniciarSesion(txtUser.getText(), new String(txtPass.getPassword()));
            VentanaPrincipal vp = new VentanaPrincipal(sistema, logueado);
            vp.setVisible(true);
            this.dispose(); // Cierra el login
        } catch (VoluntariadoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}