package Torneos;

import juegos.JuegoDeMesa;
import usuarios.Cliente;

public class torneoCompetitivo extends torneos {

    // Hay tarifa de entrada
    // El premio es el dinero recaudado
	// Para empleados es gratuito peeero sin premio

    private double tarifaEntrada; // precio por participante

    public torneoCompetitivo(String idTorneo, String nombre, JuegoDeMesa juego,
                              String diaSemana, int cupoMaximo, double tarifaEntrada) {
        super(idTorneo, nombre, juego, diaSemana, cupoMaximo);
        this.tarifaEntrada = tarifaEntrada;
    }

    public double getTarifaEntrada() {
        return tarifaEntrada;
    }

    // Premio total en metálico:
    // solo cuenta lo pagado por clientes
    // Los empleados no pagan y no aumentan el premio
    public double calcularPremio() {
        double total = 0;

        for (Inscripcion i : getInscripciones()) {
            if (i.getUsuario() instanceof Cliente) {
                total += i.getMontoPagado();
            }
        }

        return total;
    }

    public boolean ganadorPuedeRecibirPremioMetalico(Inscripcion inscripcionGanadora) {
        return inscripcionGanadora != null && inscripcionGanadora.puedeRecibirPremioMetalico();
    }

    @Override
    public boolean esAmistoso() {
        return false;
    }

    @Override
    public String getTipo() {
        return "COMPETITIVO";
    }
}