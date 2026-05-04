package Servicios;

import transacciones.ItemVenta;
import transacciones.Venta;
import usuarios.Cliente;
import usuarios.Empleado;
import usuarios.Usuario;
import inventario.InventarioVenta;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ServicioVentas {

    private static final double IVA = 0.19;
    private static final double DESCUENTO_EMPLEADO = 0.20;
    private static final double DESCUENTO_CODIGO = 0.10;

    private InventarioVenta inventarioVenta;
    private List<Venta> historialVentas;

    // Estado temporal de ventas en progreso: idVenta -> items acumulados
    private Map<String, List<ItemVenta>> itemsEnProgreso;
    // Descuento pendiente por venta
    private Map<String, Double> descuentosEnProgreso;

    public ServicioVentas(InventarioVenta inventarioVenta) {
        this.inventarioVenta = inventarioVenta;
        this.historialVentas = new ArrayList<>();
        this.itemsEnProgreso = new HashMap<>();
        this.descuentosEnProgreso = new HashMap<>();
    }

    // Inicia una venta, reserva el id
    public Venta crearVenta(String idVenta, Usuario comprador, Venta.TipoVenta tipo) {
        itemsEnProgreso.put(idVenta, new ArrayList<>());
        descuentosEnProgreso.put(idVenta, 0.0);
        // Retorna null porque la Venta real se construye al finalizar
        // El id queda registrado en los mapas temporales
        return null;
    }

    public void agregarItem(String idVenta, ItemVenta item) {
        if (itemsEnProgreso.containsKey(idVenta)) {
            itemsEnProgreso.get(idVenta).add(item);
        }
    }

    public void aplicarDescuento(String idVenta, String codigoDescuento, Usuario comprador) {
        double descuento = resolverDescuento(comprador, codigoDescuento);
        descuentosEnProgreso.put(idVenta, descuento);
    }

    public Venta finalizarVenta(String idVenta, Usuario comprador, Venta.TipoVenta tipo) {
        List<ItemVenta> items = itemsEnProgreso.getOrDefault(idVenta, new ArrayList<>());
        double descuento = descuentosEnProgreso.getOrDefault(idVenta, 0.0);

        // Aplicar descuento a cada item
        List<ItemVenta> itemsConDescuento = new ArrayList<>();
        for (ItemVenta item : items) {
            if (item.esDeJuego()) {
                itemsConDescuento.add(new ItemVenta(
                    item.getJuego(), item.getCantidad(),
                    item.getPrecioUnitario(), descuento
                ));
                inventarioVenta.vender(item.getJuego(), item.getCantidad());
            } else {
                itemsConDescuento.add(item);
            }
        }

        double subtotal = calcularTotal(itemsConDescuento);
        double impuesto = subtotal * IVA;

        Venta venta = new Venta(idVenta, comprador, itemsConDescuento,
                new Date(), tipo, subtotal, impuesto, 0);

        historialVentas.add(venta);

        // Puntos de fidelidad
        if (comprador instanceof Cliente) {
        	Cliente cliente = (Cliente) comprador;
        	cliente.agregarPuntos(venta.calcularPuntosFidelidad());
            cliente.agregarCompra(venta);
        } else if (comprador instanceof Empleado) {
            ((Empleado) comprador).agregarPuntos(venta.calcularPuntosFidelidad());
        }

        // Limpiar estado temporal
        itemsEnProgreso.remove(idVenta);
        descuentosEnProgreso.remove(idVenta);

        return venta;
    }

    public double calcularTotal(List<ItemVenta> items) {
        double subtotal = 0;
        for (ItemVenta item : items) {
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }

    public double resolverDescuento(Usuario comprador, String codigoDescuento) {
        double mejorDescuento = 0;

        // Descuento automatico de empleado
        if (comprador instanceof Empleado) {
            mejorDescuento = Math.max(mejorDescuento, DESCUENTO_EMPLEADO);
        }

        // Código compartido
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

    public List<Venta> getHistorialVentas() {
        return historialVentas;
    }
}