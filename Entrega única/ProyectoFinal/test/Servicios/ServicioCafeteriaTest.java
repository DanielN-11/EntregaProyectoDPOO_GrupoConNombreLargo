package Servicios;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Cafeteria.Bebida;
import Cafeteria.Mesa;
import Cafeteria.Pasteleria;
import transacciones.Pedido;
import transacciones.Venta;
import usuarios.Cliente;
import usuarios.Mesero;

import java.util.ArrayList;
import java.util.Arrays;

public class ServicioCafeteriaTest {

    private ServicioCafeteria servicioCafeteria;
    private Mesa mesa;
    private Cliente cliente;
    private Mesero mesero;
    private Bebida bebidaNormal;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        servicioCafeteria = new ServicioCafeteria(new ArrayList<>());
        mesa = new Mesa("M1", true, 3, 0, 0, new ArrayList<>(), null);
        cliente = new Cliente("U1", "MarioGei", "Luiguitmb", "Juan", "Nintendobot12");
        mesero = new Mesero("U2", "MeseCrack", "contraseña", "Messi Ronaldo", "Mundial2026");
        bebidaNormal = new Bebida("B1", "Jugo", 5000, false, false);
        pedido = servicioCafeteria.crearPedido("P1", mesa);
    }
    
    @Test
    void finalizarPedido_calculaImpuesto_propina_descuentoEmpleado() {
        servicioCafeteria.agregarAlPedido(pedido, bebidaNormal, 1);
        Venta venta = servicioCafeteria.finalizarPedido("V1", pedido, mesero, null);

        double subtotal = 5000 * 0.80;
        double impuesto = subtotal * 0.08;
        double propina = subtotal * 0.10;

        assertEquals(subtotal, venta.getSubtotal(), 0.01);
        assertEquals(impuesto, venta.getImpuesto(), 0.01);
        assertEquals(propina, venta.getPropina(), 0.01);
    }

    @Test
    void finalizarPedido_acumulaPuntosClientePorciento() {
        servicioCafeteria.agregarAlPedido(pedido, bebidaNormal, 1);
        Venta venta = servicioCafeteria.finalizarPedido("V1", pedido, cliente, null);
        double total = venta.getTotal();
        double puntosEsperados = total * 0.01;
        assertEquals(puntosEsperados, cliente.getPuntosFidelidad(), 0.01);
    }

    @Test
    void validarAlimento_rechazaBebidaAlcoholicaConMenores() {
        Mesa mesaConNinos = new Mesa("M2", true, 3, 1, 0, new ArrayList<>(), null);
        Bebida cerveza = new Bebida("B2", "Cerveza", 8000, true, false);

        assertFalse(servicioCafeteria.validarAlimento(cerveza, mesaConNinos));
    }
    
    @Test
    void validarAlimento_pasteleriaSiempreValida() {
        Pasteleria torta = new Pasteleria("P1", "Torta", 6000, Arrays.asList("gluten"));

        assertTrue(servicioCafeteria.validarAlimento(torta, mesa));
    }
}
