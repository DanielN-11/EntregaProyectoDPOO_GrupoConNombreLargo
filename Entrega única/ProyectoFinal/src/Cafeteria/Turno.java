package Cafeteria;

import usuarios.Empleado;

public class Turno {

    private String idTurno;
    private String diaSemana;
    private Empleado empleado;

    public Turno(String idTurno, String diaSemana, Empleado empleado) {
        this.idTurno = idTurno;
        this.diaSemana = diaSemana;
        this.empleado = empleado;
    }

    public String getIdTurno() {
        return idTurno;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }
}