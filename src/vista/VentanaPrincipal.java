package vista;

import modelo.Usuario;
import servicio.GestorArchivos;
import servicio.SistemaGestionEventos;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VentanaPrincipal extends JFrame {
    private JPanel panelPadre;
    private JTabbedPane tabbedPaneGeneral;

    private PanelCrearEvento panelCrearEvento;
    private PanelGestionInscripciones panelInscripciones;

    public VentanaPrincipal(SistemaGestionEventos sistema, Usuario usuarioLogueado) {
        setContentPane(panelPadre);
        setTitle("Sistema - " + usuarioLogueado.getNombreCompleto());
        setSize(800, 600);
        setLocationRelativeTo(null);

        panelCrearEvento = new PanelCrearEvento(sistema);
        panelInscripciones = new PanelGestionInscripciones(sistema);

        tabbedPaneGeneral.addTab("Crear Eventos", panelCrearEvento.getPanelPrincipal());
        tabbedPaneGeneral.addTab("Cola de Inscripciones", panelInscripciones.getPanelPrincipal());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                GestorArchivos.guardarDatos(sistema);
                System.exit(0);
            }
        });
    }
}