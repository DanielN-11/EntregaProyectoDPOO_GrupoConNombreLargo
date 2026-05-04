package juegos;

public class CopiaJuego {

    private String idCopia;
    private JuegoDeMesa juego;
    private String estadoCopia;
    private boolean disponible;

    public CopiaJuego(String idCopia, JuegoDeMesa juego, String estadoCopia, boolean disponible) {
        this.idCopia = idCopia;
        this.juego = juego;
        this.estadoCopia = estadoCopia;
        this.disponible = disponible;
    }

    public void marcarDisponible() {
        this.disponible = true;
    }

    public void marcarNoDisponible() {
        this.disponible = false;
    }

    public void actualizarEstado(String nuevoEstado) {
        this.estadoCopia = nuevoEstado;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public JuegoDeMesa getJuego() {
        return juego;
    }

    public String getIdCopia() {
        return idCopia;
    }

    public String getEstadoCopia() {
        return estadoCopia;
    }
}