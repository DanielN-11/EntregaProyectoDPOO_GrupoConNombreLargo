package Cafeteria;

import usuarios.Empleado;

public class SolicitudCambioTurno {

    public static final String CAMBIO_GENERAL = "CAMBIO_GENERAL";
    public static final String INTERCAMBIO = "INTERCAMBIO";

    public static final String PENDIENTE = "PENDIENTE";
    public static final String APROBADA = "APROBADA";
    public static final String RECHAZADA = "RECHAZADA";

    private String idSolicitud;
    private Empleado solicitante;
    private Turno turnoAfectado;
    private Empleado empleadoDestino;
    private String tipo;
    private String estado;

    public SolicitudCambioTurno(String idSolicitud, Empleado solicitante,
                                Turno turnoAfectado, Empleado empleadoDestino,
                                String tipo) {
        this.idSolicitud = idSolicitud;
        this.solicitante = solicitante;
        this.turnoAfectado = turnoAfectado;
        this.empleadoDestino = empleadoDestino;
        this.tipo = tipo;
        this.estado = PENDIENTE;
    }

    public void aprobar() {
        this.estado = APROBADA;
    }

    public void rechazar() {
        this.estado = RECHAZADA;
    }

    public String getEstado() {
        return estado;
    }

    public String getTipo() {
        return tipo;
    }

    public String getIdSolicitud() {
        return idSolicitud;
    }

    public Empleado getSolicitante() {
        return solicitante;
    }

    public Turno getTurnoAfectado() {
        return turnoAfectado;
    }

    public Empleado getEmpleadoDestino() {
        return empleadoDestino;
    }
}