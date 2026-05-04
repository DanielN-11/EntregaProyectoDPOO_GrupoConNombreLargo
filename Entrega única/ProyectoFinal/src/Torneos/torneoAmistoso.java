package Torneos;

import juegos.JuegoDeMesa;

public class torneoAmistoso extends torneos {

    // Porcentaje de descuento para el ganador
    private double bonoPremio;

    public torneoAmistoso(String idTorneo, String nombre, JuegoDeMesa juego,
                          String diaSemana, int cupoMaximo, double bonoPremio) {
        super(idTorneo, nombre, juego, diaSemana, cupoMaximo);
        this.bonoPremio = bonoPremio;
    }

    public double getBonoPremio() {
        return bonoPremio;
    }

    @Override
    public boolean esAmistoso() {
        return true;
    }

    @Override
    public String getTipo() {
        return "AMISTOSO";
    }
}