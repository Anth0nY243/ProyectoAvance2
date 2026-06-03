package vista;

import servicio.SistemaGestionEventos;
import modelo.Usuario;
import modelo.RolUsuario;
import javax.swing.*;

public class VentanaPrincipal extends JFrame {
    private JPanel panelContenedor;
    private JTabbedPane tabbedPanePrincipal;

    private SistemaGestionEventos sistema;
    private Usuario usuarioActual;

    public VentanaPrincipal(SistemaGestionEventos sistema, Usuario usuarioActual) {
        this.sistema = sistema;
        this.usuarioActual = usuarioActual;

        setContentPane(panelContenedor);
        setTitle("Conexión Voluntaria - " + usuarioActual.getNombreCompleto() + " (" + usuarioActual.getRol() + ")");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        if (usuarioActual.getRol() == RolUsuario.ADMINISTRADOR) {
            PanelRegistroCoordinador panelAdmin = new PanelRegistroCoordinador(sistema, usuarioActual);
            tabbedPanePrincipal.addTab("Registrar Coordinadores", panelAdmin.getPanel());

            PanelCrearEvento panelEventos = new PanelCrearEvento(sistema);
            tabbedPanePrincipal.addTab("Logística de Eventos", panelEventos.getPanel());

            PanelGestionInscripciones panelInscripciones = new PanelGestionInscripciones(sistema);
            tabbedPanePrincipal.addTab("Cola de Inscripciones", panelInscripciones.getPanel());

        } else if (usuarioActual.getRol() == RolUsuario.COORDINADOR) {
            PanelCrearEvento panelEventos = new PanelCrearEvento(sistema);
            tabbedPanePrincipal.addTab("Logística de Eventos", panelEventos.getPanel());

            PanelGestionInscripciones panelInscripciones = new PanelGestionInscripciones(sistema);
            tabbedPanePrincipal.addTab("Cola de Inscripciones", panelInscripciones.getPanel());

            PanelGestionAsistencia panelAsistencia = new PanelGestionAsistencia(sistema, usuarioActual);
            tabbedPanePrincipal.addTab("Tomar Asistencia", panelAsistencia.getPanel());

        } else if (usuarioActual.getRol() == RolUsuario.VOLUNTARIO) {
            PanelInscripcionVoluntario panelVoluntario = new PanelInscripcionVoluntario(sistema, usuarioActual);
            tabbedPanePrincipal.addTab("Eventos Disponibles", panelVoluntario.getPanel());
        }
    }
}