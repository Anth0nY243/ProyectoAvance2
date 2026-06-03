package principal;

import servicio.SistemaGestionEventos;
import servicio.GestorArchivos;
import vista.VentanaLogin;

public class Main {
    public static void main(String[] args) {

        // Cargar los datos del archivo .dat si existe
        SistemaGestionEventos sistemaCargado = GestorArchivos.cargarDatos();

        // Si es la primera vez, el archivo no existe, usamos el constructor con datos quemados
        if (sistemaCargado == null) {
            sistemaCargado = new SistemaGestionEventos();
        }

        final SistemaGestionEventos sistemaFinal = sistemaCargado;

        // Esto guarda TODO automáticamente cuando cierras el programa
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            GestorArchivos.guardarDatos(sistemaFinal);
        }));

        // Lanzar la Interfaz Gráfica
        java.awt.EventQueue.invokeLater(() -> {
            VentanaLogin login = new VentanaLogin(sistemaFinal);
            login.setVisible(true);
        });

    }
}