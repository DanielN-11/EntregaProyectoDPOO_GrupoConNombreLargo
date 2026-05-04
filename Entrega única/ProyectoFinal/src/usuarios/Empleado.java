package usuarios;
import Cafeteria.Turno;

public abstract class Empleado extends Usuario {

    private String codigoDescuento;
    private boolean enTurno;
    private Turno turno;
    private double puntosFidelidad;
    // Para torneos
    private double bonoTorneoAmistoso;

    public Empleado(String idUsuario, String login, String password, String nombre, String codigoDescuento) {
        super(idUsuario, login, password, nombre);
        this.codigoDescuento = codigoDescuento;
        this.enTurno = false;
        this.turno = null;
        this.puntosFidelidad = 0;
        this.bonoTorneoAmistoso = 0;
    }

    public boolean estaEnTurno() {
        return enTurno;
    }

    public void setEnTurno(boolean enTurno) {
        this.enTurno = enTurno;
    }

    public String getCodigoDescuento() {
        return codigoDescuento;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    // Un empleado puede pedir prestado solo si NO está en turno
    // o si no hay clientes que atender pero eso va en ServicioPrestamo
    public boolean puedePedirPrestado() {
        return !enTurno;
    }
    
    public void agregarPuntos(double puntos) {
        this.puntosFidelidad += puntos;
    }
    
    public boolean usarPuntos(double puntos) {
        if (puntos > puntosFidelidad) return false;
        this.puntosFidelidad -= puntos;
        return true;
    }

    public double getPuntosFidelidad() {
        return puntosFidelidad;
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