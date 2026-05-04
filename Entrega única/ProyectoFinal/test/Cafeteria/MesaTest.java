package Cafeteria;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import juegos.CopiaJuego;
import juegos.JuegoDeMesa;
import transacciones.Pedido;
import transacciones.Prestamo;
import usuarios.Cliente;

import java.util.ArrayList;

public class MesaTest {

    private Mesa mesa;
    private JuegoDeMesa juego;
    private CopiaJuego copia;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        mesa = new Mesa("M1", true, 3, 0, 0, new ArrayList<>(), null);
        juego = new JuegoDeMesa("J1", "Uno", 1992, "Mattel", 2, 4, 5, "Cartas", false);
        copia = new CopiaJuego("C1", juego, "Nuevo", true);
        cliente = new Cliente("U1", "MarioGei", "Luiguitmb", "Juan", "Nintendobot12");
    }

    @Test
    void agregarPrestamo_exitoso_YFallaConDos() {
        JuegoDeMesa juego2 = new JuegoDeMesa("J2", "Parques", 1970, "Coltoys", 2, 4, 5, "Tablero", false);
        CopiaJuego copia2 = new CopiaJuego("C2", juego2, "Nuevo", true);

        Prestamo p1 = new Prestamo("P1", copia, cliente, mesa);
        Prestamo p2 = new Prestamo("P2", copia2, cliente, mesa);

        assertTrue(mesa.agregarPrestamo(p1));
        assertTrue(mesa.agregarPrestamo(p2));

        CopiaJuego copia3 = new CopiaJuego("C3", juego, "Nuevo", true);
        Prestamo p3 = new Prestamo("P3", copia3, cliente, mesa);
        assertFalse(mesa.agregarPrestamo(p3));
        assertEquals(2, mesa.cantidadJuegosPrestados());
    }

    @Test
    void liberar_limpiaEstadoYDevuelvePrestamos() {
        Prestamo p1 = new Prestamo("P1", copia, cliente, mesa);
        mesa.agregarPrestamo(p1);

        mesa.liberar();

        assertFalse(mesa.isOcupada());
        assertEquals(0, mesa.cantidadJuegosPrestados());
        assertFalse(p1.isActivo());
        assertTrue(copia.isDisponible());
    }

    @Test
    void tieneBebidaCaliente_verdadero_falso() {
        assertFalse(mesa.tieneBebidaCaliente());

        Pedido pedido = new Pedido("PED1", mesa);
        Bebida cafesito = new Bebida("B1", "cafesito", 3000, false, true);
        pedido.agregarItem(cafesito, 1, cafesito.getPrecio(), 0);
        mesa.setPedidoActual(pedido);

        assertTrue(mesa.tieneBebidaCaliente());
    }
}