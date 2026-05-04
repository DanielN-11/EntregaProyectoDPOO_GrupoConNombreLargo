package Servicios;

import java.util.ArrayList;
import java.util.List;

import Torneos.Inscripcion;
import Torneos.torneoAmistoso;
import Torneos.torneoCompetitivo;
import Torneos.torneos;
import inventario.InventarioPrestamo;
import juegos.JuegoDeMesa;
import usuarios.Cliente;
import usuarios.Empleado;
import usuarios.Usuario;

public class ServicioTorneo {
	
	// logica del torneo 

    // Máximo de participantes que un usuario puede inscribir
    private static final int MAX_PARTICIPANTES_POR_INSCRIPCION = 3;

    private List<torneos> torneos;
    private InventarioPrestamo inventarioPrestamo;

    public ServicioTorneo(InventarioPrestamo inventarioPrestamo) {
        this.torneos = new ArrayList<>();
        this.inventarioPrestamo = inventarioPrestamo;
    }

    // Crear torneos

    public torneos crearTorneoAmistoso(String idTorneo, String nombre,
                                       JuegoDeMesa juego, String diaSemana,
                                       int cupoMaximo, double bonoPremio) {

        if (!validarCupoConCopias(juego, cupoMaximo)) {
            System.out.println("RECHAZADO: No hay suficientes copias para "
                    + cupoMaximo + " participantes.");
            return null;
        }

        torneoAmistoso torneo = new torneoAmistoso(
                idTorneo, nombre, juego, diaSemana, cupoMaximo, bonoPremio
        );

        torneos.add(torneo);
        return torneo;
    }

    public torneos crearTorneoCompetitivo(String idTorneo, String nombre,
                                          JuegoDeMesa juego, String diaSemana,
                                          int cupoMaximo, double tarifaEntrada) {

        if (!validarCupoConCopias(juego, cupoMaximo)) {
            System.out.println("RECHAZADO: No hay suficientes copias para "
                    + cupoMaximo + " participantes.");
            return null;
        }

        torneoCompetitivo torneo = new torneoCompetitivo(
                idTorneo, nombre, juego, diaSemana, cupoMaximo, tarifaEntrada
        );

        torneos.add(torneo);
        return torneo;
    }

    // Inscripción

    public boolean inscribirse(torneos torneo, Usuario usuario, int numParticipantes) {

        if (torneo == null || usuario == null) {
            System.out.println("RECHAZADO: Torneo o usuario inválido.");
            return false;
        }

        // Regla: máximo 3 participantes por inscripción
        if (numParticipantes < 1 || numParticipantes > MAX_PARTICIPANTES_POR_INSCRIPCION) {
            System.out.println("RECHAZADO: Puede inscribir entre 1 y 3 participantes.");
            return false;
        }

        // Regla: ya inscrito
        if (torneo.buscarInscripcion(usuario) != null) {
            System.out.println("RECHAZADO: El usuario ya está inscrito en este torneo.");
            return false;
        }

        // Regla: cupos disponibles totales
        if (torneo.cuposDisponibles() < numParticipantes) {
            System.out.println("RECHAZADO: No hay suficientes cupos. Disponibles: "
                    + torneo.cuposDisponibles());
            return false;
        }

        // Regla: empleado solo puede si NO está cubriendo turno ese dia
        if (usuario instanceof Empleado) {
            Empleado emp = (Empleado) usuario;

            if (empleadoCubreTurnoEseDia(emp, torneo.getDiaSemana())) {
                System.out.println("RECHAZADO: El empleado tiene turno el día del torneo.");
                return false;
            }
        }

        // Regla: cupos fanaticos
        int cuposFanaticosUsados = 0;
        int cuposRegularesUsados = 0;

        boolean esFanatico = torneo.usuarioEsFanatico(usuario);

        if (esFanatico) {
            int fanDisponibles = torneo.cuposFanaticosDisponibles();

            // El fanatico toma primero cupos reservados
            cuposFanaticosUsados = Math.min(numParticipantes, fanDisponibles);

            // Si se acabaron los cupos fanaticos, toma cupos regulares
            cuposRegularesUsados = numParticipantes - cuposFanaticosUsados;

            if (torneo.cuposRegularesDisponibles() < cuposRegularesUsados) {
                System.out.println("RECHAZADO: No hay cupos regulares suficientes.");
                return false;
            }
        } else {
            // Si no es fanatico, solo puede tomar cupos regulares
            cuposRegularesUsados = numParticipantes;

            if (torneo.cuposRegularesDisponibles() < cuposRegularesUsados) {
                System.out.println("RECHAZADO: No hay cupos regulares disponibles.");
                return false;
            }
        }

        // Regla: torneo competitivo
        boolean puedeRecibirPremioMetalico = true;
        double montoPagado = 0;

        if (torneo instanceof torneoCompetitivo) {
            torneoCompetitivo competitivo = (torneoCompetitivo) torneo;

            if (usuario instanceof Empleado) {
                // Empleados compiten gratis, pero no reciben premio en metálico
                montoPagado = 0;
                puedeRecibirPremioMetalico = false;
            } else if (usuario instanceof Cliente) {
                montoPagado = competitivo.getTarifaEntrada() * numParticipantes;
                puedeRecibirPremioMetalico = true;
            }
        }

        Inscripcion inscripcion = new Inscripcion(
                usuario,
                numParticipantes,
                cuposFanaticosUsados,
                cuposRegularesUsados,
                puedeRecibirPremioMetalico,
                montoPagado
        );

        torneo.agregarInscripcion(inscripcion);

        System.out.println("Inscripción exitosa: " + numParticipantes
                + " participante(s) en torneo '" + torneo.getNombre() + "'.");

        return true;
    }

    public boolean desinscribirse(torneos torneo, Usuario usuario) {
        if (torneo == null || usuario == null) {
            return false;
        }

        boolean eliminado = torneo.eliminarInscripcion(usuario);

        if (eliminado) {
            System.out.println("Desinscripción exitosa del torneo '"
                    + torneo.getNombre() + "'.");
        } else {
            System.out.println("El usuario no estaba inscrito en este torneo.");
        }

        return eliminado;
    }

    // Ganador

    public Inscripcion registrarGanador(torneos torneo, Usuario ganador) {

        if (torneo == null || ganador == null) {
            System.out.println("RECHAZADO: Torneo o ganador inválido.");
            return null;
        }

        Inscripcion inscripcion = torneo.buscarInscripcion(ganador);

        if (inscripcion == null) {
            System.out.println("RECHAZADO: El ganador no está inscrito en el torneo.");
            return null;
        }

        if (torneo instanceof torneoCompetitivo) {
            if (!inscripcion.puedeRecibirPremioMetalico()) {
                System.out.println("INFO: El ganador es empleado. No recibe premio en metálico.");
            } else {
                torneoCompetitivo competitivo = (torneoCompetitivo) torneo;
                System.out.println("Premio metálico para el ganador: "
                        + competitivo.calcularPremio());
            }
        }

        if (torneo instanceof torneoAmistoso) {
            torneoAmistoso amistoso = (torneoAmistoso) torneo;
            guardarBonoPremioAmistoso(ganador, amistoso.getBonoPremio());

            System.out.println("Bono de torneo amistoso guardado para el ganador: "
                    + amistoso.getBonoPremio());
        }

        return inscripcion;
    }

    private void guardarBonoPremioAmistoso(Usuario ganador, double porcentajeBono) {
        if (ganador instanceof Cliente) {
            ((Cliente) ganador).setBonoTorneoAmistoso(porcentajeBono);
        } else if (ganador instanceof Empleado) {
            ((Empleado) ganador).setBonoTorneoAmistoso(porcentajeBono);
        }
    }

    // Validaciones auxiliares

    public boolean empleadoCubreTurnoEseDia(Empleado empleado, String diaTorneo) {
        if (empleado == null || diaTorneo == null) {
            return false;
        }

        if (!empleado.estaEnTurno()) {
            return false;
        }

        if (empleado.getTurno() == null) {
            return false;
        }

        return empleado.getTurno().getDiaSemana().equalsIgnoreCase(diaTorneo);
    }

    private boolean validarCupoConCopias(JuegoDeMesa juego, int cupoMaximo) {
        int copiasDisponibles = inventarioPrestamo.getCopiasPorJuego(juego).size();
        int maximoPosible = copiasDisponibles * juego.getMaxJugadores();
        return cupoMaximo <= maximoPosible;
    }

    // Getters y busqueda de torneos

    public List<torneos> getTorneos() {
        return torneos;
    }

    public torneos buscarTorneo(String idTorneo) {
        for (torneos t : torneos) {
            if (t.getIdTorneo().equals(idTorneo)) {
                return t;
            }
        }
        return null;
    }

    public InventarioPrestamo getInventarioPrestamo() {
        return inventarioPrestamo;
    }
}