package Servicios;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inventario.InventarioVenta;
import juegos.JuegoDeMesa;
import transacciones.ItemVenta;
import transacciones.Venta;
import usuarios.Cliente;
import usuarios.Mesero;

public class ServicioVentasTest {

    private ServicioVentas servicioVentas;
    private InventarioVenta inventarioVenta;
    private JuegoDeMesa juego;
    private Cliente cliente;
    private Mesero mesero;

    @BeforeEach
    void setUp() {
        inventarioVenta = new InventarioVenta();
        servicioVentas = new ServicioVentas(inventarioVenta);

        juego = new JuegoDeMesa("J1", "Uno", 1992, "Mattel", 2, 4, 5, "Cartas", false);
        inventarioVenta.reabastecer(juego, 5, 50000);

        cliente = new Cliente("U1", "MarioGei", "Luiguitmb", "Juan", "Nintendobot12");
        mesero = new Mesero("U2", "MeseCrack", "contraseña", "Messi Ronaldo", "Mundial2026");
    }

    @Test
    void finalizarVenta_calculaSubtotalIVAyPuntos() {
        servicioVentas.crearVenta("V1", cliente, Venta.TipoVenta.JUEGO);
        servicioVentas.agregarItem("V1", new ItemVenta(juego, 1, 50000, 0));
        servicioVentas.aplicarDescuento("V1", null, cliente);
        Venta venta = servicioVentas.finalizarVenta("V1", cliente, Venta.TipoVenta.JUEGO);

        double subtotal = 50000;
        double impuesto = subtotal * 0.19;
        double total = subtotal + impuesto;
        double puntosEsperados = total * 0.01;

        assertEquals(subtotal, venta.getSubtotal(), 0.01);
        assertEquals(impuesto, venta.getImpuesto(), 0.01);
        assertEquals(puntosEsperados, cliente.getPuntosFidelidad(), 0.01);
    }

    @Test
    void resolverDescuento_empleado_codigo_sinDescuento0() {
        assertEquals(0.20, servicioVentas.resolverDescuento(mesero, null), 0.01);
        assertEquals(0.10, servicioVentas.resolverDescuento(cliente, "Nintendobot12"), 0.01);
        assertEquals(0.0,  servicioVentas.resolverDescuento(cliente, null), 0.01);
    }

    @Test
    void finalizarVenta_reducesStockInventario() {
        servicioVentas.crearVenta("V1", cliente, Venta.TipoVenta.JUEGO);
        servicioVentas.agregarItem("V1", new ItemVenta(juego, 2, 50000, 0));
        servicioVentas.aplicarDescuento("V1", null, cliente);
        servicioVentas.finalizarVenta("V1", cliente, Venta.TipoVenta.JUEGO);

        assertEquals(3, inventarioVenta.getCantidad(juego));
    }
}