package transacciones;

import java.util.ArrayList;
import java.util.List;
import Cafeteria.Alimento;
import Cafeteria.Mesa;

public class Pedido {

    private String idPedido;
    private Mesa mesa;
    private List<ItemVenta> items;
    private double propina; // porcentaje, 0.10 por defecto

    public Pedido(String idPedido, Mesa mesa) {
        this.idPedido = idPedido;
        this.mesa = mesa;
        this.items = new ArrayList<>();
        this.propina = 0.10;
    }

    public void agregarItem(Alimento alimento, int cantidad, double precioUnitario, double descuento) {
        items.add(new ItemVenta(alimento, cantidad, precioUnitario, descuento));
    }

    public void setPropina(double propina) {
        this.propina = propina;
    }

    public double getSubtotalSinImpuestos() {
        double total = 0;
        for (ItemVenta item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public double getPropinaMonto() {
        return getSubtotalSinImpuestos() * propina;
    }

    public double getImpuestoConsumo() {
        return getSubtotalSinImpuestos() * 0.08;
    }

    public double getTotalFinal() {
        return getSubtotalSinImpuestos() + getImpuestoConsumo() + getPropinaMonto();
    }

    public String getIdPedido() {
        return idPedido;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public List<ItemVenta> getItems() {
        return items;
    }

    public double getPropina() {
        return propina;
    }
}