package transacciones;

import java.util.Date;
import java.util.List;
import usuarios.Usuario;

public class Venta {

    public enum TipoVenta {
        JUEGO, CAFETERIA
    }

    private String idVenta;
    private Usuario comprador;
    private List<ItemVenta> items;
    private Date fecha;
    private TipoVenta tipo;
    private double subtotal;
    private double impuesto;
    private double propina;
    private double total;

    public Venta(String idVenta, Usuario comprador, List<ItemVenta> items,
                 Date fecha, TipoVenta tipo, double subtotal,
                 double impuesto, double propina) {
        this.idVenta = idVenta;
        this.comprador = comprador;
        this.items = items;
        this.fecha = fecha;
        this.tipo = tipo;
        this.subtotal = subtotal;
        this.impuesto = impuesto;
        this.propina = propina;
        this.total = subtotal + impuesto + propina;
    }

    public double calcularPuntosFidelidad() {
        return total * 0.01;
    }

    public String getIdVenta() {
        return idVenta;
    }

    public Usuario getComprador() {
        return comprador;
    }

    public List<ItemVenta> getItems() {
        return items;
    }

    public Date getFecha() {
        return fecha;
    }

    public TipoVenta getTipo() {
        return tipo;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getImpuesto() {
        return impuesto;
    }

    public double getPropina() {
        return propina;
    }

    public double getTotal() {
        return total;
    }
}