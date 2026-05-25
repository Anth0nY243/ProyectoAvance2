package modelo;
import java.io.Serializable;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String nombreCompleto;
    private String username;
    private String password;
    private RolUsuario rol;

    public Usuario(int id, String nombreCompleto, String username, String password, RolUsuario rol) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public int getId() { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public RolUsuario getRol() { return rol; }

    @Override
    public String toString() { return nombreCompleto + " (" + rol + ")"; }
}