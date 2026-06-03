package modelo;
import java.io.Serializable;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L; // Identificador de versión


    private String nombreCompleto;
    private String cedula;
    private String username;
    private String password;
    private RolUsuario rol;

    public Usuario(String nombreCompleto, String cedula, String username, String password, RolUsuario rol) {
        this.nombreCompleto = nombreCompleto;
        this.cedula = cedula;
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public String getNombreCompleto() { return nombreCompleto; }
    public String getCedula() { return cedula; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public RolUsuario getRol() { return rol; }

    @Override
    public String toString() { return nombreCompleto + " - CI: " + cedula; }
}