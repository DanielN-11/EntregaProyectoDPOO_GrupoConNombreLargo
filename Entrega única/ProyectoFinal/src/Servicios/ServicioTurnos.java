package Servicios;

import Cafeteria.SolicitudCambioTurno;
import Cafeteria.Turno;
import usuarios.Cocinero;
import usuarios.Empleado;
import usuarios.Mesero;
import java.util.ArrayList;
import java.util.List;

public class ServicioTurnos {

    private static final int MIN_COCINEROS = 1;
    private static final int MIN_MESEROS = 2;

    private List<Empleado> empleados;
    private List<Turno> turnos;
    private List<SolicitudCambioTurno> solicitudesPendientes;

    public ServicioTurnos(List<Empleado> empleados) {
        this.empleados = empleados;
        this.turnos = new ArrayList<>();
        this.solicitudesPendientes = new ArrayList<>();
    }

    public Turno crearTurno(String idTurno, Empleado empleado, String dia) {
        Turno turno = new Turno(idTurno, dia, empleado);
        empleado.setTurno(turno);
        turnos.add(turno);
        return turno;
    }

    public void eliminarTurno(Turno turno) {
        turno.getEmpleado().setTurno(null);
        turnos.remove(turno);
    }

    public boolean solicitarCambio(SolicitudCambioTurno solicitud) {
        if (!validarMinimosPersonalSinEmpleado(solicitud.getSolicitante())) {
            System.out.println("RECHAZADO: No se cumplen los mínimos de personal.");
            return false;
        }
        solicitudesPendientes.add(solicitud);
        return true;
    }

    public boolean aprobarSolicitud(SolicitudCambioTurno solicitud) {
        if (!validarMinimosPersonalSinEmpleado(solicitud.getSolicitante())) {
            System.out.println("RECHAZADO: No se cumplen los mínimos de personal.");
            solicitud.rechazar();
            return false;
        }
        if (solicitud.getTipo().equals(SolicitudCambioTurno.INTERCAMBIO)
                && solicitud.getEmpleadoDestino() != null) {
            Turno turnoSolicitante = solicitud.getSolicitante().getTurno();
            Turno turnoDestino = solicitud.getEmpleadoDestino().getTurno();
            solicitud.getSolicitante().setTurno(turnoDestino);
            solicitud.getEmpleadoDestino().setTurno(turnoSolicitante);
            if (turnoDestino != null) turnoDestino.setEmpleado(solicitud.getSolicitante());
            if (turnoSolicitante != null) turnoSolicitante.setEmpleado(solicitud.getEmpleadoDestino());
        }
        solicitud.aprobar();
        solicitudesPendientes.remove(solicitud);
        return true;
    }

    public void rechazarSolicitud(SolicitudCambioTurno solicitud) {
        solicitud.rechazar();
        solicitudesPendientes.remove(solicitud);
    }

    public boolean validarMinimosPersonal(String dia) {
        int cocineros = 0;
        int meseros = 0;
        for (Turno t : turnos) {
            if (t.getDiaSemana().equals(dia)) {
                if (t.getEmpleado() instanceof Cocinero) cocineros++;
                if (t.getEmpleado() instanceof Mesero) meseros++;
            }
        }
        return cocineros >= MIN_COCINEROS && meseros >= MIN_MESEROS;
    }

    private boolean validarMinimosPersonalSinEmpleado(Empleado empleadoSaliente) {
        int cocineros = 0;
        int meseros = 0;
        for (Empleado e : empleados) {
            if (e.estaEnTurno() && !e.equals(empleadoSaliente)) {
                if (e instanceof Cocinero) cocineros++;
                if (e instanceof Mesero) meseros++;
            }
        }
        return cocineros >= MIN_COCINEROS && meseros >= MIN_MESEROS;
    }

    public List<SolicitudCambioTurno> getSolicitudesPendientes() {
        return solicitudesPendientes;
    }

    public List<Turno> getTurnos() {
        return turnos;
    }

    public List<Empleado> getEmpleados() {
        return empleados;
    }
}