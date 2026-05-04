package Servicios;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Cafeteria.SolicitudCambioTurno;
import Cafeteria.Turno;
import usuarios.Cocinero;
import usuarios.Empleado;
import usuarios.Mesero;

import java.util.ArrayList;
import java.util.List;

public class ServicioTurnosTest {

    private ServicioTurnos servicioTurnos;
    private Mesero mesero1;
    private Mesero mesero2;
    private Mesero mesero3;
    private Cocinero cocinero;

    @BeforeEach
    void setUp() {
        mesero1 = new Mesero("E1", "mes1", "pass", "Ana", "C1");
        mesero2 = new Mesero("E2", "mes2", "pass", "Luis", "C2");
        mesero3 = new Mesero("E3", "mes3", "pass", "Pedro", "C3");
        cocinero = new Cocinero("E4", "coc1", "pass", "María", "C4");

        List<Empleado> empleados = new ArrayList<>();
        empleados.add(mesero1);
        empleados.add(mesero2);
        empleados.add(mesero3);
        empleados.add(cocinero);

        servicioTurnos = new ServicioTurnos(empleados);

        mesero1.setEnTurno(true);
        mesero2.setEnTurno(true);
        mesero3.setEnTurno(true);
        cocinero.setEnTurno(true);

        servicioTurnos.crearTurno("T1", mesero1, "Lunes");
        servicioTurnos.crearTurno("T2", mesero2, "Lunes");
        servicioTurnos.crearTurno("T3", cocinero, "Lunes");
        servicioTurnos.crearTurno("T4", mesero3, "Lunes");
    }

    @Test
    void solicitarCambio_rechazado_siRompeMinimosPersonal() {
        // Si mesero1 sale solo quedan mesero2 y cocinero -> mínimos OK
        // Pero si mesero2 también sale solo queda 1 mesero -> falla
        mesero2.setEnTurno(false);
        SolicitudCambioTurno solicitud = new SolicitudCambioTurno("S1", mesero1, mesero1.getTurno(), null, SolicitudCambioTurno.CAMBIO_GENERAL);

        boolean resultado = servicioTurnos.solicitarCambio(solicitud);

        assertFalse(resultado);
        assertTrue(servicioTurnos.getSolicitudesPendientes().isEmpty());
    }

    @Test
    void solicitarCambio_aprobado_hayPersonalSuficiente() {
        SolicitudCambioTurno solicitud = new SolicitudCambioTurno("S1", mesero1, mesero1.getTurno(), null, SolicitudCambioTurno.CAMBIO_GENERAL);

        boolean resultado = servicioTurnos.solicitarCambio(solicitud);

        assertTrue(resultado);
        assertEquals(1, servicioTurnos.getSolicitudesPendientes().size());
    }

    @Test
    void aprobarSolicitud_intercambio_TurnosCorrectamente() {
        Turno turnoMesero1 = mesero1.getTurno();
        Turno turnoMesero3 = mesero3.getTurno();

        SolicitudCambioTurno solicitud = new SolicitudCambioTurno("S1", mesero1, turnoMesero1, mesero3, SolicitudCambioTurno.INTERCAMBIO);

        servicioTurnos.aprobarSolicitud(solicitud);

        assertEquals(turnoMesero3, mesero1.getTurno());
        assertEquals(turnoMesero1, mesero3.getTurno());
        assertEquals(SolicitudCambioTurno.APROBADA, solicitud.getEstado());
    }

    @Test
    void validarMinimosPersonal_verdadero_falso() {
        assertTrue(servicioTurnos.validarMinimosPersonal("Lunes"));

        servicioTurnos.eliminarTurno(mesero2.getTurno());
        servicioTurnos.eliminarTurno(mesero3.getTurno());

        assertFalse(servicioTurnos.validarMinimosPersonal("Lunes"));
    }
}