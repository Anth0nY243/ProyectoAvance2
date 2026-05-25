package principal;

import servicio.GestorArchivos;
import servicio.SistemaGestionEventos;
import vista.VentanaLogin;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SistemaGestionEventos sistema = GestorArchivos.cargarDatos();

            // --- TRUCO PARA LA EXPOSICIÓN: Inyectar voluntarios de prueba si la cola está vacía ---
            if (sistema.getSolicitudesPendientes().isEmpty()) {
                try {
                    modelo.Evento evDemo = new modelo.Evento(99, "Campaña de Reciclaje", 10, java.time.LocalDate.now(), "Parque Central");
                    sistema.crearEvento(evDemo);

                    modelo.Usuario vol1 = new modelo.Usuario(2, "Juan Pérez", "juan", "123", modelo.RolUsuario.VOLUNTARIO);
                    modelo.Usuario vol2 = new modelo.Usuario(3, "Ana Gómez", "ana", "123", modelo.RolUsuario.VOLUNTARIO);

                    sistema.solicitarInscripcion(vol1, evDemo); // Entra Juan a la Cola
                    sistema.solicitarInscripcion(vol2, evDemo); // Entra Ana a la Cola (detrás de Juan)
                } catch (Exception e) {}
            }
            // --------------------------------------------------------------------------------------

            VentanaLogin login = new VentanaLogin(sistema);
            login.setVisible(true);
        });
    }
}