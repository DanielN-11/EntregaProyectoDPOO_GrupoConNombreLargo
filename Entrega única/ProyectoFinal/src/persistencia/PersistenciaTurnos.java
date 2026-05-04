package persistencia;

import Cafeteria.SolicitudCambioTurno;
import Cafeteria.Turno;
import usuarios.Empleado;
import usuarios.Usuario;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistencia de turnos y solicitudes de cambio.
 *
 * Archivo: datos/turnos.txt
 * Formato: idTurno|dia|idEmpleado
 *
 * Archivo: datos/solicitudes.txt
 * Formato: idSolicitud|idSolicitante|idTurno|idDestino|tipo|estado
 *          (idDestino y idTurno pueden ser "null")
 */
public class PersistenciaTurnos {

    private static final String SEP     = "|";
    private static final String NULO    = "null";
    private static final String ARCH_TURNOS      = "turnos.txt";
    private static final String ARCH_SOLICITUDES = "solicitudes.txt";

    private String rutaDatos;

    public PersistenciaTurnos(String rutaDatos) {
        this.rutaDatos = rutaDatos;
    }

    // ── Turnos ────────────────────────────────────────────────

    public void guardarTurnos(List<Turno> turnos) throws IOException {
        File dir = new File(rutaDatos);
        if (!dir.exists()) dir.mkdirs();

        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDatos + ARCH_TURNOS))) {
            for (Turno t : turnos) {
                String idEmpleado = t.getEmpleado() != null ? t.getEmpleado().getIdUsuario() : NULO;
                pw.println(t.getIdTurno() + SEP + t.getDiaSemana() + SEP + idEmpleado);
            }
        }
    }

    public List<Turno> cargarTurnos(List<Usuario> usuarios) throws IOException {
        List<Turno> turnos = new ArrayList<>();
        File archivo = new File(rutaDatos + ARCH_TURNOS);
        if (!archivo.exists()) return turnos;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] p = linea.split("\\|", -1);
                if (p.length < 3) continue;

                String idTurno    = p[0];
                String dia        = p[1];
                String idEmpleado = p[2];

                Empleado empleado = null;
                if (!NULO.equals(idEmpleado)) {
                    empleado = (Empleado) buscarUsuario(usuarios, idEmpleado);
                }

                Turno turno = new Turno(idTurno, dia, empleado);
                if (empleado != null) {
                    empleado.setTurno(turno);
                }
                turnos.add(turno);
            }
        }
        return turnos;
    }

    // ── Solicitudes de cambio ─────────────────────────────────

    public void guardarSolicitudes(List<SolicitudCambioTurno> solicitudes) throws IOException {
        File dir = new File(rutaDatos);
        if (!dir.exists()) dir.mkdirs();

        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDatos + ARCH_SOLICITUDES))) {
            for (SolicitudCambioTurno s : solicitudes) {
                String idSolicitante = s.getSolicitante() != null ? s.getSolicitante().getIdUsuario() : NULO;
                String idTurno       = s.getTurnoAfectado() != null ? s.getTurnoAfectado().getIdTurno() : NULO;
                String idDestino     = s.getEmpleadoDestino() != null ? s.getEmpleadoDestino().getIdUsuario() : NULO;

                pw.println(s.getIdSolicitud() + SEP + idSolicitante + SEP
                        + idTurno + SEP + idDestino + SEP
                        + s.getTipo() + SEP + s.getEstado());
            }
        }
    }

    public List<SolicitudCambioTurno> cargarSolicitudes(List<Usuario> usuarios, List<Turno> turnos) throws IOException {
        List<SolicitudCambioTurno> solicitudes = new ArrayList<>();
        File archivo = new File(rutaDatos + ARCH_SOLICITUDES);
        if (!archivo.exists()) return solicitudes;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] p = linea.split("\\|", -1);
                if (p.length < 6) continue;

                String idSolicitud   = p[0];
                String idSolicitante = p[1];
                String idTurno       = p[2];
                String idDestino     = p[3];
                String tipo          = p[4];
                String estado        = p[5];

                Empleado solicitante = (Empleado) buscarUsuario(usuarios, idSolicitante);
                Turno turno          = buscarTurno(turnos, idTurno);
                Empleado destino     = NULO.equals(idDestino) ? null
                        : (Empleado) buscarUsuario(usuarios, idDestino);

                SolicitudCambioTurno s = new SolicitudCambioTurno(idSolicitud, solicitante, turno, destino, tipo);
                // Restaurar estado
                if (SolicitudCambioTurno.APROBADA.equals(estado))  s.aprobar();
                if (SolicitudCambioTurno.RECHAZADA.equals(estado))  s.rechazar();

                solicitudes.add(s);
            }
        }
        return solicitudes;
    }

    // ── Utilidades ────────────────────────────────────────────

    private Usuario buscarUsuario(List<Usuario> usuarios, String id) {
        for (Usuario u : usuarios) {
            if (u.getIdUsuario().equals(id)) return u;
        }
        return null;
    }

    private Turno buscarTurno(List<Turno> turnos, String idTurno) {
        if (NULO.equals(idTurno)) return null;
        for (Turno t : turnos) {
            if (t.getIdTurno().equals(idTurno)) return t;
        }
        return null;
    }
}
