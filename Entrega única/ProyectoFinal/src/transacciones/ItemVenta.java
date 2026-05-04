package transacciones;

import Cafeteria.Alimento;
import juegos.JuegoDeMesa;

public class ItemVenta {

    // Solo uno de los dos será no-null según el tipo de venta
    private Alimento alimento;
    private JuegoDeMesa juego;
    private int cantidad;
    private double precioUnitario;
    private double descuentoAplicado;

    // Constructor para venta de cafetería
    public ItemVenta(Alimento alimento, int cantidad, double precioUnitario, double descuentoAplicado) {
        this.alimento = alimento;
        this.juego = null;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.descuentoAplicado = descuentoAplicado;
    }

    // Constructor para venta de juego
    public ItemVenta(JuegoDeMesa juego, int cantidad, double precioUnitario, double descuentoAplicado) {
        this.juego = juego;
        this.alimento = null;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.descuentoAplicado = descuentoAplicado;
    }

    public double getSubtotal() {
        return precioUnitario * cantidad * (1 - descuentoAplicado);
    }

    public Alimento getAlimento() {
        return alimento;
    }

    public JuegoDeMesa getJuego() {
        return juego;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public double getDescuentoAplicado() {
        return descuentoAplicado;
    }

    public boolean esDeJuego() {
        return juego != null;
    }
}