package servicio;
import java.io.*;

public class GestorArchivos {
    private static final String RUTA_ARCHIVO = "datos_voluntariado.dat";

    public static void guardarDatos(SistemaGestionEventos sistema) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RUTA_ARCHIVO))) {
            oos.writeObject(sistema);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SistemaGestionEventos cargarDatos() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RUTA_ARCHIVO))) {
            return (SistemaGestionEventos) ois.readObject();
        } catch (Exception e) {
            return new SistemaGestionEventos();
        }
    }
}