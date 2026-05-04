package Servicios;

import usuarios.Empleado;

public class Sugerencia {

    public enum EstadoSugerencia {
        PENDIENTE, APROBADA, RECHAZADA
    }

    private String idSugerencia;
    private Empleado empleado;
    private String nombrePropuesto;
    private String categoria;
    private EstadoSugerencia estado;

    public Sugerencia(String idSugerencia, Empleado empleado, String nombrePropuesto, String categoria) {
        this.idSugerencia = idSugerencia;
        this.empleado = empleado;
        this.nombrePropuesto = nombrePropuesto;
        this.categoria = categoria;
        this.estado = EstadoSugerencia.PENDIENTE;
    }

    public void aprobar() {
        this.estado = EstadoSugerencia.APROBADA;
    }

    public void rechazar() {
        this.estado = EstadoSugerencia.RECHAZADA;
    }

    public EstadoSugerencia getEstado() {
        return estado;
    }

    public String getNombrePropuesto() {
        return nombrePropuesto;
    }

    public String getCategoria() {
        return categoria;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public String getIdSugerencia() {
        return idSugerencia;
    }
}