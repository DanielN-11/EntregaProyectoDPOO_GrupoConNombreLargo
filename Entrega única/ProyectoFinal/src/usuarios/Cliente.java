package usuarios;

import java.util.ArrayList;
import java.util.List;

import Cafeteria.Mesa;
import transacciones.Venta;

public class Cliente extends Usuario {

    private String idCliente;
    private double puntosFidelidad;
    private Mesa mesaActual;
    private List<Venta> historialCompras;
    // Para torneos
    private double bonoTorneoAmistoso;

    public Cliente(String idUsuario, String login, String password, String nombre, String idCliente) {
        super(idUsuario, login, password, nombre);
        this.idCliente = idCliente;
        this.puntosFidelidad = 0;
        this.mesaActual = null;
        this.historialCompras = new ArrayList<>();
        
        this.bonoTorneoAmistoso = 0;
    }

    public void agregarPuntos(double puntos) {
        this.puntosFidelidad += puntos;
    }

    public boolean usarPuntos(double puntos) {
        if (puntos > puntosFidelidad) {
            return false;
        }
        this.puntosFidelidad -= puntos;
        return true;
    }

    public double getPuntosFidelidad() {
        return puntosFidelidad;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public Mesa getMesaActual() {
        return mesaActual;
    }

    public void setMesaActual(Mesa mesa) {
        this.mesaActual = mesa;
    }

    public boolean tieneMesa() {
        return mesaActual != null;
    }

    public void agregarCompra(Venta venta) {
        historialCompras.add(venta);
    }

    public List<Venta> getHistorialCompras() {
        return historialCompras;
    }
    
    // Para torneos
    
    public double getBonoTorneoAmistoso() {
        return bonoTorneoAmistoso;
    }

    public void setBonoTorneoAmistoso(double bonoTorneoAmistoso) {
        this.bonoTorneoAmistoso = bonoTorneoAmistoso;
    }

    public boolean tieneBonoTorneoAmistoso() {
        return bonoTorneoAmistoso > 0;
    }

    public void limpiarBonoTorneoAmistoso() {
        this.bonoTorneoAmistoso = 0;
    }
}