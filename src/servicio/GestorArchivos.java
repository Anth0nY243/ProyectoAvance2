package servicio;

import modelo.Evento;
import modelo.Inscripcion;
import java.io.*;

public class GestorArchivos {
    private static final String RUTA_ARCHIVO = "datos_voluntariado.dat";

    public static void guardarDatos(SistemaGestionEventos sistema) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RUTA_ARCHIVO))) {
            oos.writeObject(sistema);
            System.out.println("Datos guardados en " + RUTA_ARCHIVO);
        } catch (IOException e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    public static SistemaGestionEventos cargarDatos() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RUTA_ARCHIVO))) {
            SistemaGestionEventos sistema = (SistemaGestionEventos) ois.readObject();

            int maxIdEvento = 0;
            for (Evento e : sistema.getEventosDisponibles()) {
                if (e.getId() > maxIdEvento) maxIdEvento = e.getId();
            }
            Evento.setGeneradorId(maxIdEvento + 1);

            int maxIdInsc = 0;
            for (Inscripcion i : sistema.getHistorialCompleto()) {
                if (i.getId() > maxIdInsc) maxIdInsc = i.getId();
            }
            Inscripcion.setGeneradorIdInscripcion(maxIdInsc + 1);

            return sistema;
        } catch (Exception e) {
            return null;
        }
    }
}