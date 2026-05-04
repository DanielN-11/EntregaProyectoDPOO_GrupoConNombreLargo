package inventario;

import java.util.HashMap;
import java.util.Map;

import juegos.JuegoDeMesa;

public class InventarioVenta {

    private Map<JuegoDeMesa, Integer> stock;
    private Map<JuegoDeMesa, Double> precios;

    public InventarioVenta() {
        this.stock = new HashMap<>();
        this.precios = new HashMap<>();
    }

    public void reabastecer(JuegoDeMesa juego, int cantidad, double precio) {
        if (stock.containsKey(juego)) {
            stock.put(juego, stock.get(juego) + cantidad);
        } else {
            stock.put(juego, cantidad);
            precios.put(juego, precio);
        }
    }

    public boolean hayStock(JuegoDeMesa juego) {
        if (stock.containsKey(juego)) {
            return stock.get(juego) > 0;
        }
        return false;
    }

    public void vender(JuegoDeMesa juego, int cantidad) {
    	if (!hayStock(juego)) {
            System.out.println("No hay stock disponible para: " + juego.getNombre());
            return;
        }
        stock.put(juego, stock.get(juego) - cantidad);
    }

    public double getPrecio(JuegoDeMesa juego) {
        return precios.get(juego);
    }

    public int getCantidad(JuegoDeMesa juego) {
        return stock.get(juego);
    }

    public java.util.Set<JuegoDeMesa> getJuegos() {
        return stock.keySet();
    }
}