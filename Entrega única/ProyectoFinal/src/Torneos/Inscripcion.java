package Torneos;

import usuarios.Usuario;

public class Inscripcion {

    // La inscripción de un usuario al torneo
    // Un usuario puede inscribir hasta 3 personas
    // Si se desinscribe, se eliminan todos sus cupos

    private Usuario usuario;
    private int numParticipantes; // entre 1 y 3

    // Cantidad de cupos que salieron de la reserva de fanaticos
    private int cuposFanaticosUsados;

    // Cantidad de cupos que salieron de cupos regulares
    private int cuposRegularesUsados;

    // En torneos competitivos, los empleados no pagan entrada,
    // pero tampoco pueden recibir premio en metálico
    private boolean puedeRecibirPremioMetalico;

    // Monto pagado por esta inscripción
    // Para empleados en competitivo debe ser 0
    private double montoPagado;

    public Inscripcion(Usuario usuario, int numParticipantes) {
        this(usuario, numParticipantes, 0, numParticipantes, true, 0);
    }

    public Inscripcion(
            Usuario usuario,
            int numParticipantes,
            int cuposFanaticosUsados,
            int cuposRegularesUsados,
            boolean puedeRecibirPremioMetalico,
            double montoPagado
    ) {
        this.usuario = usuario;
        this.numParticipantes = numParticipantes;
        this.cuposFanaticosUsados = cuposFanaticosUsados;
        this.cuposRegularesUsados = cuposRegularesUsados;
        this.puedeRecibirPremioMetalico = puedeRecibirPremioMetalico;
        this.montoPagado = montoPagado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public int getNumParticipantes() {
        return numParticipantes;
    }

    public int getCuposFanaticosUsados() {
        return cuposFanaticosUsados;
    }

    public int getCuposRegularesUsados() {
        return cuposRegularesUsados;
    }

    public boolean puedeRecibirPremioMetalico() {
        return puedeRecibirPremioMetalico;
    }

    public double getMontoPagado() {
        return montoPagado;
    }
}