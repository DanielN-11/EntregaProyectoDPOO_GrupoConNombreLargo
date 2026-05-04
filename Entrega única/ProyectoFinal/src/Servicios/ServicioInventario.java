package Servicios;

import inventario.InventarioPrestamo;
import inventario.InventarioVenta;
import juegos.CopiaJuego;
import juegos.JuegoDeMesa;
import java.util.List;

public class ServicioInventario {

    private InventarioPrestamo inventarioPrestamo;
    private InventarioVenta inventarioVenta;

    public ServicioInventario(InventarioPrestamo inventarioPrestamo, InventarioVenta inventarioVenta) {
        this.inventarioPrestamo = inventarioPrestamo;
        this.inventarioVenta = inventarioVenta;
    }

    // Agrega una copia nueva al inventario de préstamo
    public void agregarCopiaAPrestamo(CopiaJuego copia) {
        inventarioPrestamo.agregarCopia(copia);
    }

    // Mueve una copia del inventario de venta al de préstamo (reparar)
    public boolean repararCopia(CopiaJuego copiaAReemplazar, JuegoDeMesa juego) {
        if (!inventarioVenta.hayStock(juego)) {
            return false;
        }
        inventarioVenta.vender(juego, 1);
        copiaAReemplazar.actualizarEstado("Nuevo");
        copiaAReemplazar.marcarDisponible();
        return true;
    }

    // Marca una copia como desaparecida
    public void marcarComoRobada(CopiaJuego copia) {
        copia.actualizarEstado("Desaparecido");
        copia.marcarNoDisponible();
    }

    // Reabastecer inventario de venta
    public void reabastecerVenta(JuegoDeMesa juego, int cantidad, double precio) {
        inventarioVenta.reabastecer(juego, cantidad, precio);
    }

    // Reabastecer inventario de préstamo con copia nueva
    public void reabastecerPrestamo(CopiaJuego copia) {
        inventarioPrestamo.agregarCopia(copia);
    }

    // Mover juego de venta a préstamo (crea una copia nueva)
    public boolean moverDeVentaAPrestamo(JuegoDeMesa juego, String idNuevaCopia) {
        if (!inventarioVenta.hayStock(juego)) {
            return false;
        }
        inventarioVenta.vender(juego, 1);
        CopiaJuego nuevaCopia = new CopiaJuego(idNuevaCopia, juego, "Nuevo", true);
        inventarioPrestamo.agregarCopia(nuevaCopia);
        return true;
    }

    public void actualizarEstadoCopia(CopiaJuego copia, String nuevoEstado) {
        copia.actualizarEstado(nuevoEstado);
    }

    public List<CopiaJuego> getCopiasPorJuego(JuegoDeMesa juego) {
        return inventarioPrestamo.getCopiasPorJuego(juego);
    }

    public InventarioPrestamo getInventarioPrestamo() {
        return inventarioPrestamo;
    }

    public InventarioVenta getInventarioVenta() {
        return inventarioVenta;
    }
}