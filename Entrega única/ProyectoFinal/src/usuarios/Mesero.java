package usuarios;

import java.util.ArrayList;
import java.util.List;
import juegos.JuegoDeMesa;

public class Mesero extends Empleado {

    private List<JuegoDeMesa> juegosQueConoce;

    public Mesero(String idUsuario, String login, String password, String nombre, String codigoDescuento) {
        super(idUsuario, login, password, nombre, codigoDescuento);
        this.juegosQueConoce = new ArrayList<>();
    }

    public void aprenderJuego(JuegoDeMesa juego) {
        if (!juegosQueConoce.contains(juego)) {
            juegosQueConoce.add(juego);
        }
    }

    public boolean conoceJuego(JuegoDeMesa juego) {
        return juegosQueConoce.contains(juego);
    }

    public List<JuegoDeMesa> getJuegosQueConoce() {
        return juegosQueConoce;
    }
}