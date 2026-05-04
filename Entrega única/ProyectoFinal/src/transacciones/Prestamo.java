package transacciones;

import java.util.Date;
import Cafeteria.Mesa;
import juegos.CopiaJuego;
import usuarios.Usuario;

public class Prestamo {

    private String idPrestamo;
    private CopiaJuego copia;
    private Usuario usuario;
    private Mesa mesa; // nullable
    private Date fechaPrestamo;
    private Date fechaDevolucion; // null mientras esté activo
    private boolean activo;

    public Prestamo(String idPrestamo, CopiaJuego copia, Usuario usuario, Mesa mesa) {
        this.idPrestamo = idPrestamo;
        this.copia = copia;
        this.usuario = usuario;
        this.mesa = mesa;
        this.fechaPrestamo = new Date();
        this.fechaDevolucion = null;
        this.activo = true;
        copia.marcarNoDisponible();
    }

    /** Constructor para carga desde persistencia. No modifica el estado de la copia. */
    public Prestamo(String idPrestamo, CopiaJuego copia, Usuario usuario, Mesa mesa,
                    Date fechaPrestamo, Date fechaDevolucion, boolean activo) {
        this.idPrestamo = idPrestamo;
        this.copia = copia;
        this.usuario = usuario;
        this.mesa = mesa;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.activo = activo;
    }

    public void devolver() {
        this.activo = false;
        this.fechaDevolucion = new Date();
        copia.marcarDisponible();
    }

    public boolean isActivo() {
        return activo;
    }

    public CopiaJuego getCopia() {
        return copia;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public Date getFechaPrestamo() {
        return fechaPrestamo;
    }

    public Date getFechaDevolucion() {
        return fechaDevolucion;
    }

    public String getIdPrestamo() {
        return idPrestamo;
    }
}