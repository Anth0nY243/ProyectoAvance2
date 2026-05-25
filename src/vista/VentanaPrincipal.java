package vista;

import modelo.Usuario;
import servicio.GestorArchivos;
import servicio.SistemaGestionEventos;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VentanaPrincipal extends JFrame {
    private JPanel panelPadre;
    private JTabbedPane tabbedPaneGeneral; // Componente requerido por el avance

    // Estos paneles debes enlazarlos en el diseñador de IntelliJ (anidarlos dentro del JTabbedPane)
    private PanelCrearEvento panelCrearEvento;
    private PanelGestionInscripciones panelInscripciones;

    public VentanaPrincipal(SistemaGestionEventos sistema, Usuario usuarioLogueado) {
        setContentPane(panelPadre);
        setTitle("Sistema - " + usuarioLogueado.getNombreCompleto());
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Inicializamos los paneles internos pasándoles el sistema
        panelCrearEvento = new PanelCrearEvento(sistema);
        panelInscripciones = new PanelGestionInscripciones(sistema);

        // Agregamos los paneles al JTabbedPane por código (Si no lo hiciste en el diseñador visual)
        tabbedPaneGeneral.addTab("Crear Eventos", panelCrearEvento.getPanelPrincipal());
        tabbedPaneGeneral.addTab("Cola de Inscripciones", panelInscripciones.getPanelPrincipal());

        // Evento crítico para guardar los datos al cerrar
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                GestorArchivos.guardarDatos(sistema);
                System.exit(0);
            }
        });
    }
}