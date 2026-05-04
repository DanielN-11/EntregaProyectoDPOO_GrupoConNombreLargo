package usuarios;

import java.util.ArrayList;
import java.util.List;
import juegos.JuegoDeMesa;

public abstract class Usuario {

    private String idUsuario;
    private String login;
    private String password;
    private String nombre;
    private List<JuegoDeMesa> favoritos;

    public Usuario(String idUsuario, String login, String password, String nombre) {
        this.idUsuario = idUsuario;
        this.login = login;
        this.password = password;
        this.nombre = nombre;
        this.favoritos = new ArrayList<>();
    }

    public boolean autenticar(String loginIngresado, String passwordIngresado) {
        return this.login.equals(loginIngresado) && this.password.equals(passwordIngresado);
    }

    public void agregarFavorito(JuegoDeMesa juego) {
        if (!favoritos.contains(juego)) {
            favoritos.add(juego);
        }
    }

    public void quitarFavorito(JuegoDeMesa juego) {
        favoritos.remove(juego);
    }

    public List<JuegoDeMesa> getFavoritos() {
        return favoritos;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getLogin() {
        return login;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean verificarPassword(String password) {
        return this.password.equals(password);
    }

    public String getPassword() {
        return password;
    }
}