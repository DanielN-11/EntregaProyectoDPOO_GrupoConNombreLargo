package Servicios;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Cafeteria.Alimento;
import Cafeteria.Bebida;
import Cafeteria.Mesa;
import Cafeteria.Turno;
import inventario.InventarioPrestamo;
import juegos.CopiaJuego;
import juegos.JuegoDeMesa;
import transacciones.Prestamo;
import transacciones.Pedido;
import usuarios.Cliente;
import usuarios.Mesero;
import usuarios.Administrador;

import java.util.ArrayList;
import java.util.List;

class ServicioPrestamoTest {

    private ServicioPrestamo servicioPrestamo;
    private InventarioPrestamo inventarioPrestamo;
    private JuegoDeMesa juego;
    private CopiaJuego copia;
    private Mesa mesa;
    private Cliente cliente;
    private Administrador administrador;
    private List<Mesa> todasLasMesas;
    private List<Mesero> meseros;

    @BeforeEach
    void setUp() {
        // Juego de 2-4 jugadores, edad mínima 5, categoría Cartas, no difícil
        juego = new JuegoDeMesa("J1", "Uno", 1992, "Mattel", 2, 4, 5, "Cartas", false);
        copia = new CopiaJuego("C1", juego, "Nuevo", true);

        inventarioPrestamo = new InventarioPrestamo();
        inventarioPrestamo.agregarCopia(copia);

        meseros = new ArrayList<>();
        servicioPrestamo = new ServicioPrestamo(inventarioPrestamo, meseros);

        // Mesa ocupada con 3 personas, sin niños ni jóvenes, sin pedido
        mesa = new Mesa("M1", true, 3, 0, 0, new ArrayList<>(), null);

        cliente = new Cliente("U1", "MarioGei", "Luiguitmb", "Juan", "Nintendobot12");

        todasLasMesas = new ArrayList<>();
        todasLasMesas.add(mesa);
    }
    
    @Test
    void prestar_exitoso_clienteConMesaOcupada() {
    	Prestamo prestamo = servicioPrestamo.prestarJuego("P1", cliente, juego, mesa, todasLasMesas);
    	
    	assertNotNull(prestamo);
    	assertTrue(prestamo.isActivo());
    	assertEquals(cliente, prestamo.getUsuario());
    	assertEquals(copia, prestamo.getCopia());
    	assertFalse(copia.isDisponible());
    	assertEquals(1, mesa.cantidadJuegosPrestados());
    }
    
    @Test
    void prestar_falla_administradorNoPuedePrestar() {
    	administrador = new Administrador("A1", "LolsitoGod", "GwenMiElfa", "Santi");
    	Prestamo prestamo = servicioPrestamo.prestarJuego("P1", administrador, juego, mesa, todasLasMesas);
    	assertNull(prestamo);
    	assertTrue(copia.isDisponible());
    	assertEquals(0, mesa.cantidadJuegosPrestados());	
    }
    
    @Test
    void prestar_falla_mesaConDosJuegosYaPrestados() {
    	JuegoDeMesa juego2 = new JuegoDeMesa("J2", "Parques", 1070, "Coltoys", 2,4,5, "Tablero", false);
    	CopiaJuego copia2 = new CopiaJuego("C2", juego2, "Nuevo", true);
    	CopiaJuego copia3 = new CopiaJuego("C3", juego, "Nuevo", true);
    	
    	inventarioPrestamo.agregarCopia(copia2);
    	inventarioPrestamo.agregarCopia(copia3);
    	
    	servicioPrestamo.prestarJuego("P1", cliente, juego, mesa, todasLasMesas);
    	servicioPrestamo.prestarJuego("P2", cliente, juego2, mesa, todasLasMesas);
    	
    	Prestamo tercerPrestamo = servicioPrestamo.prestarJuego("P3", cliente, juego, mesa, todasLasMesas);
    	
    	assertNull(tercerPrestamo);
    	assertEquals(2, mesa.cantidadJuegosPrestados());
    }
    
    @Test
    void prestar_falla_juegoNoAptoParaPersonasEdad() {
    	juego = new JuegoDeMesa("J2", "Parques", 1070, "Coltoys", 2,4,6, "Tablero", false);
    	mesa = new Mesa("M2", true, 3, 1, 0, new ArrayList<>(), null);
    	Prestamo prestamo = servicioPrestamo.prestarJuego("P1", cliente, juego, mesa, todasLasMesas);
    	assertNull(prestamo);
    	assertEquals(0, mesa.cantidadJuegosPrestados());
    }
    
    @Test
    void prestar_falla_juegoAccionConBebidaCaliente() {
    	juego = new JuegoDeMesa("J2", "Parques", 1070, "Coltoys", 2,4,6, "Accion", false);
    	Alimento cafecito = new Bebida("B1", "cafecito", 10, false, true);
    	List<Alimento> menu = new ArrayList<>();
    	ServicioCafeteria servicioCafeteria = new ServicioCafeteria((menu));
    	Pedido pedido = servicioCafeteria.crearPedido("P2", mesa);
    	servicioCafeteria.agregarAlPedido(pedido, cafecito, 1);
    	Prestamo prestamo = servicioPrestamo.prestarJuego("P1", cliente, juego, mesa, todasLasMesas);
    	assertNull(prestamo);	
    }
    
    @Test
    void prestar_falla_sinCopiaDisponible() {
    	JuegoDeMesa juego2 = new JuegoDeMesa("J3", "Poker", 1930, "Cassino", 2,4,5, "Cartas", false);
    	Prestamo prestamo = servicioPrestamo.prestarJuego("P1", cliente, juego2, mesa, todasLasMesas);
    	assertNull(prestamo);
    }
    
    @Test
    void prestar_empleadoEnTurnoConClientes_rechazado() {
    	Mesero mesero = new Mesero("Me1", "MeseCrack", "contraseña", "Messi Ronaldo", "Mundial2026");
    	meseros.add(mesero);
    	mesero.setEnTurno(true);
    	Prestamo prestamo = servicioPrestamo.prestarJuego("P1", mesero, juego, mesa, todasLasMesas);
    	assertNull(prestamo);
    }
    
    @Test
    void devolverJuego_marcaInactivo_CopiaDisponible() {
    	Prestamo prestamo = servicioPrestamo.prestarJuego("P1", cliente, juego, mesa, todasLasMesas);
    	servicioPrestamo.devolverJuego(prestamo);
    	assertEquals(0, mesa.cantidadJuegosPrestados());
    }
    
    
    
    

}
