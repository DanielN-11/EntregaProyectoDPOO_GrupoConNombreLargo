package sistema;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Cafeteria.Mesa;
import usuarios.Administrador;
import usuarios.Cliente;
import Torneos.Inscripcion;
import Torneos.torneoCompetitivo;
import Torneos.torneos;
import juegos.CopiaJuego;
import juegos.JuegoDeMesa;
import usuarios.Mesero;

import java.util.ArrayList;
import java.util.List;

public class SistemaCafeTest {

    private SistemaCafe sistema;
    private Cliente cliente;
    private Administrador admin;

    @BeforeEach
    void setUp() {
        sistema = new SistemaCafe(10);
        cliente = new Cliente("U1", "MarioGei", "Luiguitmb", "Juan", "Nintendobot12");
        admin = new Administrador("U2", "LolsitoGod", "GwenMiElfa", "Santi");

        sistema.agregarUsuario(cliente);
        sistema.agregarUsuario(admin);

        sistema.agregarMesa(new Mesa("M1", false, 0, 0, 0, null, null));
        sistema.agregarMesa(new Mesa("M2", false, 0, 0, 0, null, null));
    }

    @Test
    void registrarMesa_exitosa_YRechazadaPorCapacidad() {
        Mesa mesa = sistema.registrarMesa(4, 0, 0, cliente);
        assertNotNull(mesa);
        assertTrue(mesa.isOcupada());

        Cliente cliente2 = new Cliente("U3", "CarryPotter", "0imaginacion", "Pedro", "TengoSueño");
        Mesa mesaRechazada = sistema.registrarMesa(8, 0, 0, cliente2);
        assertNull(mesaRechazada);
    }

    @Test
    void registrarMesa_rechazada_noHayMesasLibres() {
        Cliente cliente2 = new Cliente("U3", "CarryPotter", "0imaginacion", "Pedro", "TengoSueño");
        sistema.registrarMesa(2, 0, 0, cliente);
        sistema.registrarMesa(2, 0, 0, cliente2);

        Cliente cliente3 = new Cliente("U4", "CancerBero", "LuchoDior", "Rosa", "TengoSueño2");
        Mesa mesaRechazada = sistema.registrarMesa(2, 0, 0, cliente3);
        assertNull(mesaRechazada);
    }

    @Test
    void login_exitoso_YFallidoConCredencialesInvalidas() {
        assertNotNull(sistema.login("MarioGei", "Luiguitmb"));
        assertNull(sistema.login("MarioGei", "semeolvidoxd"));
        assertNull(sistema.login("Tontolio", "Luiguitmb"));
    }

    @Test
    void realizarVenta_rechazada_administradorNoPuedeComprarNiOrdenar() {
        assertNull(sistema.realizarVentaTienda(admin, new ArrayList<>()));

        Mesa mesa = sistema.registrarMesa(2, 0, 0, cliente);
        assertNull(sistema.realizarVentaCafeteria(admin, mesa, new ArrayList<>()));
    }
    
    @Test
    void flujoTorneoAmistoso_desdeSistemaCafe_guardaBonoGanador() {
        JuegoDeMesa catan = new JuegoDeMesa("J1", "Catan", 1995, "Catan Studio", 3, 4, 10, "Tablero", true);
        sistema.getServicioInventario().getInventarioPrestamo()
                .agregarCopia(new CopiaJuego("C1", catan, "Nuevo", true));

        cliente.agregarFavorito(catan);

        torneos torneo = sistema.crearTorneoAmistoso(
                "T1", "Torneo Amistoso Catan", catan, "Lunes", 4, 0.15
        );

        assertNotNull(torneo);
        assertTrue(sistema.inscribirseATorneo(torneo, cliente, 1));

        Inscripcion ganador = sistema.registrarGanadorTorneo(torneo, cliente);

        assertNotNull(ganador);
        assertEquals(0.15, cliente.getBonoTorneoAmistoso(), 0.01);
        assertTrue(cliente.tieneBonoTorneoAmistoso());
    }
    
    @Test
    void flujoTorneoCompetitivo_desdeSistemaCafe_clientePagaEmpleadoGratis() {
        JuegoDeMesa catan = new JuegoDeMesa("J1", "Catan", 1995, "Catan Studio", 3, 4, 10, "Tablero", true);
        sistema.getServicioInventario().getInventarioPrestamo()
                .agregarCopia(new CopiaJuego("C1", catan, "Nuevo", true));

        Mesero mesero = new Mesero("U3", "mesero", "123", "Mesero", "DESC1");
        mesero.setEnTurno(false);
        sistema.agregarUsuario(mesero);

        torneos torneo = sistema.crearTorneoCompetitivo(
                "T2", "Torneo Competitivo Catan", catan, "Martes", 4, 10000
        );

        assertNotNull(torneo);
        assertTrue(sistema.inscribirseATorneo(torneo, cliente, 2));
        assertTrue(sistema.inscribirseATorneo(torneo, mesero, 1));

        torneoCompetitivo competitivo = (torneoCompetitivo) torneo;

        assertEquals(20000, competitivo.calcularPremio(), 0.01);
        assertTrue(torneo.buscarInscripcion(cliente).puedeRecibirPremioMetalico());
        assertFalse(torneo.buscarInscripcion(mesero).puedeRecibirPremioMetalico());
    }
}