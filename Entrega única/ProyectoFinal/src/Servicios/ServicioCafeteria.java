package Servicios;

import Cafeteria.Alimento;
import Cafeteria.Mesa;
import Cafeteria.Pasteleria;
import transacciones.ItemVenta;
import transacciones.Pedido;
import transacciones.Venta;
import usuarios.Cliente;
import usuarios.Empleado;
import usuarios.Usuario;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServicioCafeteria {

    private static final double DESCUENTO_EMPLEADO = 0.20;
    private static final double DESCUENTO_CODIGO = 0.10;

    private List<Alimento> menu;
    private List<Venta> historialVentas;

    public ServicioCafeteria(List<Alimento> menu) {
        this.menu = menu;
        this.historialVentas = new ArrayList<>();
    }

    public boolean validarAlimento(Alimento alimento, Mesa mesa) {
        if (!alimento.puedeServirseEnMesa(mesa)) {
            return false;
        }
        return true;
    }

    public Pedido crearPedido(String idPedido, Mesa mesa) {
        Pedido pedido = new Pedido(idPedido, mesa);
        mesa.setPedidoActual(pedido);
        return pedido;
    }

    public void agregarAlPedido(Pedido pedido, Alimento alimento, int cantidad) {
        Mesa mesa = pedido.getMesa();
        if (!validarAlimento(alimento, mesa)) {
            System.out.println("RECHAZADO: El alimento no puede servirse en esta mesa.");
            return;
        }
        // Descuento se resuelve al cerrar con finalizarPedido
        pedido.agregarItem(alimento, cantidad, alimento.getPrecio(), 0);
    }

    public Venta finalizarPedido(String idVenta, Pedido pedido, Usuario comprador, String codigoDescuento) {
        double descuento = resolverDescuento(comprador, codigoDescuento);
        // Recrear items con descuento aplicado
        List<ItemVenta> itemsConDescuento = new ArrayList<>();
        for (ItemVenta item : pedido.getItems()) {
            itemsConDescuento.add(new ItemVenta(
                item.getAlimento(), item.getCantidad(),
                item.getPrecioUnitario(), descuento
            ));
        }
        double subtotal = 0;
        for (ItemVenta item : itemsConDescuento) subtotal += item.getSubtotal();
        double impuesto = subtotal * 0.08;
        double propina = subtotal * pedido.getPropina();

        Venta venta = new Venta(idVenta, comprador, itemsConDescuento,
                new Date(), Venta.TipoVenta.CAFETERIA, subtotal, impuesto, propina);

        historialVentas.add(venta);

        if (comprador instanceof Cliente) {
            Cliente cliente = (Cliente) comprador;
            cliente.agregarPuntos(venta.calcularPuntosFidelidad());
            cliente.agregarCompra(venta);
        } else if (comprador instanceof Empleado) {
            ((Empleado) comprador).agregarPuntos(venta.calcularPuntosFidelidad());
        }
        return venta;
    }

    private double resolverDescuento(Usuario comprador, String codigoDescuento) {
        double mejorDescuento = 0;

        // Descuento automatico de empleado
        if (comprador instanceof Empleado) {
            mejorDescuento = Math.max(mejorDescuento, DESCUENTO_EMPLEADO);
        }

        // Codigo compartido
        if (codigoDescuento != null && !codigoDescuento.isEmpty()) {
            mejorDescuento = Math.max(mejorDescuento, DESCUENTO_CODIGO);
        }

        // Bono ganado en torneo amistoso
        if (comprador instanceof Cliente) {
            Cliente cliente = (Cliente) comprador;

            if (cliente.tieneBonoTorneoAmistoso()) {
                mejorDescuento = Math.max(mejorDescuento, cliente.getBonoTorneoAmistoso());
                cliente.limpiarBonoTorneoAmistoso();
            }

        } else if (comprador instanceof Empleado) {
            Empleado empleado = (Empleado) comprador;

            if (empleado.tieneBonoTorneoAmistoso()) {
                mejorDescuento = Math.max(mejorDescuento, empleado.getBonoTorneoAmistoso());
                empleado.limpiarBonoTorneoAmistoso();
            }
        }

        return mejorDescuento;
    }

    public void agregarAlMenu(Alimento alimento) {
        menu.add(alimento);
    }

    public List<Alimento> getMenu() {
        return menu;
    }

    public List<Venta> getHistorialVentas() {
        return historialVentas;
    }
}