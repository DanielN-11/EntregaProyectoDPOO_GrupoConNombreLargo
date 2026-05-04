package Servicios;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inventario.InventarioPrestamo;
import inventario.InventarioVenta;
import juegos.CopiaJuego;
import juegos.JuegoDeMesa;

public class ServicioInventarioTest {

    private ServicioInventario servicioInventario;
    private InventarioPrestamo inventarioPrestamo;
    private InventarioVenta inventarioVenta;
    private JuegoDeMesa juego;
    private CopiaJuego copia;

    @BeforeEach
    void setUp() {
        inventarioPrestamo = new InventarioPrestamo();
        inventarioVenta = new InventarioVenta();
        servicioInventario = new ServicioInventario(inventarioPrestamo, inventarioVenta);

        juego = new JuegoDeMesa("J1", "Uno", 1992, "Mattel", 2, 4, 5, "Cartas", false);
        copia = new CopiaJuego("C1", juego, "Falta una carta amarilla", false);
        inventarioPrestamo.agregarCopia(copia);
    }

    @Test
    void repararCopia_exitoso_YFallidoSinStock() {
        assertFalse(servicioInventario.repararCopia(copia, juego));

        inventarioVenta.reabastecer(juego, 1, 50000);
        assertTrue(servicioInventario.repararCopia(copia, juego));
        assertEquals("Nuevo", copia.getEstadoCopia());
        assertTrue(copia.isDisponible());
        assertEquals(0, inventarioVenta.getCantidad(juego));
    }

    @Test
    void moverDeVentaAPrestamo_exitoso_YFallidoSinStock() {
        assertFalse(servicioInventario.moverDeVentaAPrestamo(juego, "C2"));

        inventarioVenta.reabastecer(juego, 2, 50000);
        assertTrue(servicioInventario.moverDeVentaAPrestamo(juego, "C2"));
        assertEquals(1, inventarioVenta.getCantidad(juego));
        assertEquals(2, inventarioPrestamo.getCopiasPorJuego(juego).size());
    }

    @Test
    void marcarComoRobada_marcaEstadoDesaparecido() {
        copia.marcarDisponible();
        servicioInventario.marcarComoRobada(copia);

        assertEquals("Desaparecido", copia.getEstadoCopia());
        assertFalse(copia.isDisponible());
    }
}