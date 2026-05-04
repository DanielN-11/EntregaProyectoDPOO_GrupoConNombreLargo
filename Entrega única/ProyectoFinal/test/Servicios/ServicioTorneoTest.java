package Servicios;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Cafeteria.Turno;
import Torneos.Inscripcion;
import Torneos.torneoCompetitivo;
import Torneos.torneos;
import inventario.InventarioPrestamo;
import juegos.CopiaJuego;
import juegos.JuegoDeMesa;
import usuarios.Cliente;
import usuarios.Mesero;

public class ServicioTorneoTest {

    private ServicioTorneo servicioTorneo;
    private InventarioPrestamo inventarioPrestamo;

    private JuegoDeMesa catan;
    private JuegoDeMesa uno;

    private Cliente clienteFanatico;
    private Cliente clienteNormal;
    private Mesero mesero;

    @BeforeEach
    void setUp() {
        // Armamos un inventario simple para que los torneos tengan copias disponibles
        inventarioPrestamo = new InventarioPrestamo();

        catan = new JuegoDeMesa("J1", "Catan", 1995, "Catan Studio", 3, 4, 10, "Tablero", true);
        uno = new JuegoDeMesa("J2", "Uno", 1992, "Mattel", 2, 10, 5, "Cartas", false);

        // Catan tiene 2 copias y cada copia aguanta 4 jugadores, entonces maximo 8 cupos
        inventarioPrestamo.agregarCopia(new CopiaJuego("C1", catan, "Nuevo", true));
        inventarioPrestamo.agregarCopia(new CopiaJuego("C2", catan, "Nuevo", true));

        // Uno tiene una copia, pero aguanta hasta 10 jugadores
        inventarioPrestamo.agregarCopia(new CopiaJuego("C3", uno, "Nuevo", true));

        servicioTorneo = new ServicioTorneo(inventarioPrestamo);

        clienteFanatico = new Cliente("U1", "fan", "123", "Cliente Fanatico", "CL1");
        clienteNormal = new Cliente("U2", "normal", "123", "Cliente Normal", "CL2");
        mesero = new Mesero("U3", "mesero", "123", "Mesero", "DESC1");

        // Este cliente sí queda como fanatico de Catan
        clienteFanatico.agregarFavorito(catan);
    }

    @Test
    void crearTorneo_exitoso_yFallaSiNoHayCopiasSuficientes() {
        // Con 2 copias de Catan y 4 jugadores por copia, 8 cupos sí se puede
        torneos torneoValido = servicioTorneo.crearTorneoAmistoso(
                "T1", "Torneo Catan", catan, "Lunes", 8, 0.15
        );

        assertNotNull(torneoValido);
        assertEquals(1, servicioTorneo.getTorneos().size());

        // 9 cupos ya se pasa del maximo posible, entonces debe fallar
        torneos torneoInvalido = servicioTorneo.crearTorneoAmistoso(
                "T2", "Torneo Catan Grande", catan, "Martes", 9, 0.15
        );

        assertNull(torneoInvalido);
        assertEquals(1, servicioTorneo.getTorneos().size());
    }

    @Test
    void inscribirse_fanaticoUsaCupoReservado_yNormalUsaRegular() {
        // Cupo 4 => 20% redondeado hacia arriba = 1 cupo fanático
        torneos torneo = servicioTorneo.crearTorneoAmistoso(
                "T1", "Torneo Catan", catan, "Lunes", 4, 0.15
        );

        assertEquals(1, torneo.getCuposReservadosFanaticos());
        assertEquals(3, torneo.getCuposRegularesTotales());

        // Como es fanático, debe tomar primero el cupo reservado
        assertTrue(servicioTorneo.inscribirse(torneo, clienteFanatico, 1));

        assertEquals(0, torneo.cuposFanaticosDisponibles());
        assertEquals(3, torneo.cuposRegularesDisponibles());

        // Este no es fanático, entonces entra por cupos normales
        assertTrue(servicioTorneo.inscribirse(torneo, clienteNormal, 3));

        assertEquals(0, torneo.cuposDisponibles());
        assertEquals(0, torneo.cuposRegularesDisponibles());
    }

    @Test
    void inscribirse_fallaDuplicado_yMasDeTresParticipantes() {
        torneos torneo = servicioTorneo.crearTorneoAmistoso(
                "T1", "Torneo Catan", catan, "Lunes", 4, 0.15
        );

        // Primera inscripción bien
        assertTrue(servicioTorneo.inscribirse(torneo, clienteFanatico, 1));

        // Segunda inscripción del mismo usuario no se puede
        assertFalse(servicioTorneo.inscribirse(torneo, clienteFanatico, 1));

        // Nadie puede inscribir más de 3 participantes
        assertFalse(servicioTorneo.inscribirse(torneo, clienteNormal, 4));
    }

    @Test
    void desinscribirse_eliminaTodosLosCuposDelUsuario() {
        torneos torneo = servicioTorneo.crearTorneoAmistoso(
                "T1", "Torneo Catan", catan, "Lunes", 4, 0.15
        );

        servicioTorneo.inscribirse(torneo, clienteFanatico, 1);
        servicioTorneo.inscribirse(torneo, clienteNormal, 3);

        assertEquals(0, torneo.cuposDisponibles());

        // Pedro/cliente normal había tomado 3 cupos, entonces al salir libera los 3
        assertTrue(servicioTorneo.desinscribirse(torneo, clienteNormal));

        assertEquals(3, torneo.cuposDisponibles());
        assertEquals(3, torneo.cuposRegularesDisponibles());
        assertNull(torneo.buscarInscripcion(clienteNormal));
    }

    @Test
    void empleadoEnTurnoMismoDiaNoPuedeInscribirse_peroSinTurnoActivoSiPuede() {
        torneos torneo = servicioTorneo.crearTorneoAmistoso(
                "T1", "Torneo Catan", catan, "Lunes", 4, 0.15
        );

        // El mesero está cubriendo turno el mismo día del torneo
        Turno turno = new Turno("TR1", "Lunes", mesero);
        mesero.setTurno(turno);
        mesero.setEnTurno(true);

        assertFalse(servicioTorneo.inscribirse(torneo, mesero, 1));

        // Si ya no está en turno, sí lo dejamos jugar
        mesero.setEnTurno(false);

        assertTrue(servicioTorneo.inscribirse(torneo, mesero, 1));
    }

    @Test
    void empleadoNoUsaCupoFanaticoAunqueTengaJuegoFavorito() {
        torneos torneo = servicioTorneo.crearTorneoAmistoso(
                "T1", "Torneo Catan", catan, "Lunes", 4, 0.15
        );

        // Aunque el empleado tenga favorito el juego, la reserva es solo para clientes fanáticos
        mesero.agregarFavorito(catan);
        mesero.setEnTurno(false);

        assertTrue(servicioTorneo.inscribirse(torneo, mesero, 1));

        // El cupo fanático sigue intacto porque el mesero entró por cupos regulares
        assertEquals(1, torneo.cuposFanaticosDisponibles());
        assertEquals(2, torneo.cuposRegularesDisponibles());
    }

    @Test
    void competitivo_clientePaga_empleadoGratisYNoRecibePremio() {
        torneos torneo = servicioTorneo.crearTorneoCompetitivo(
                "T1", "Torneo Competitivo Catan", catan, "Miercoles", 4, 10000
        );

        mesero.setEnTurno(false);

        // El cliente paga 2 cupos
        assertTrue(servicioTorneo.inscribirse(torneo, clienteNormal, 2));

        // El empleado entra gratis
        assertTrue(servicioTorneo.inscribirse(torneo, mesero, 1));

        torneoCompetitivo competitivo = (torneoCompetitivo) torneo;

        // Solo cuentan los 2 cupos pagados por cliente
        assertEquals(20000, competitivo.calcularPremio(), 0.01);

        Inscripcion insCliente = torneo.buscarInscripcion(clienteNormal);
        Inscripcion insEmpleado = torneo.buscarInscripcion(mesero);

        assertEquals(20000, insCliente.getMontoPagado(), 0.01);
        assertTrue(insCliente.puedeRecibirPremioMetalico());

        assertEquals(0, insEmpleado.getMontoPagado(), 0.01);
        assertFalse(insEmpleado.puedeRecibirPremioMetalico());
    }

    @Test
    void registrarGanadorAmistoso_guardaBonoEnCliente() {
        torneos torneo = servicioTorneo.crearTorneoAmistoso(
                "T1", "Torneo Catan", catan, "Lunes", 4, 0.15
        );

        servicioTorneo.inscribirse(torneo, clienteFanatico, 1);

        // Si gana un amistoso, se le guarda el bono para una compra futura
        Inscripcion ganador = servicioTorneo.registrarGanador(torneo, clienteFanatico);

        assertNotNull(ganador);
        assertEquals(0.15, clienteFanatico.getBonoTorneoAmistoso(), 0.01);
        assertTrue(clienteFanatico.tieneBonoTorneoAmistoso());
    }

    @Test
    void registrarGanador_fallaSiUsuarioNoEstaInscrito() {
        torneos torneo = servicioTorneo.crearTorneoAmistoso(
                "T1", "Torneo Catan", catan, "Lunes", 4, 0.15
        );

        // No se puede poner ganador a alguien que ni siquiera está inscrito
        Inscripcion ganador = servicioTorneo.registrarGanador(torneo, clienteNormal);

        assertNull(ganador);
        assertEquals(0, clienteNormal.getBonoTorneoAmistoso(), 0.01);
    }
}