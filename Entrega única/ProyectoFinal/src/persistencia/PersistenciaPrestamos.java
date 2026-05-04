package persistencia;

import Cafeteria.Mesa;
import juegos.CopiaJuego;
import transacciones.Prestamo;
import usuarios.Usuario;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Persistencia del historial de préstamos.
 *
 * Archivo: datos/prestamos.txt
 * Formato: idPrestamo|idCopia|idUsuario|idMesa|fechaPrestamo|fechaDevolucion|activo
 *          (idMesa y fechaDevolucion pueden ser "null")
 */
public class PersistenciaPrestamos {

    private static final String SEP    = "|";
    private static final String NULO   = "null";
    private static final String FECHA_FMT = "yyyy-MM-dd HH:mm:ss";
    private static final String ARCHIVO   = "prestamos.txt";

    private String rutaDatos;

    public PersistenciaPrestamos(String rutaDatos) {
        this.rutaDatos = rutaDatos;
    }

    public void guardar(List<Prestamo> prestamos) throws IOException {
        File dir = new File(rutaDatos);
        if (!dir.exists()) dir.mkdirs();

        SimpleDateFormat sdf = new SimpleDateFormat(FECHA_FMT);
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDatos + ARCHIVO))) {
            for (Prestamo pr : prestamos) {
                String idMesa     = pr.getMesa() != null ? pr.getMesa().getIdMesa() : NULO;
                String fechaDev   = pr.getFechaDevolucion() != null
                        ? sdf.format(pr.getFechaDevolucion()) : NULO;

                pw.println(pr.getIdPrestamo() + SEP
                        + pr.getCopia().getIdCopia() + SEP
                        + pr.getUsuario().getIdUsuario() + SEP
                        + idMesa + SEP
                        + sdf.format(pr.getFechaPrestamo()) + SEP
                        + fechaDev + SEP
                        + pr.isActivo());
            }
        }
    }

    /**
     * Carga préstamos y los vincula con copias, usuarios y mesas.
     * Los préstamos activos marcarán automáticamente su copia como no disponible.
     */
    public List<Prestamo> cargar(List<CopiaJuego> copias,
                                  List<Usuario> usuarios,
                                  List<Mesa> mesas) throws IOException {
        List<Prestamo> prestamos = new ArrayList<>();
        File archivo = new File(rutaDatos + ARCHIVO);
        if (!archivo.exists()) return prestamos;

        SimpleDateFormat sdf = new SimpleDateFormat(FECHA_FMT);
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] p = linea.split("\\|", -1);
                if (p.length < 7) continue;

                String idPrestamo = p[0];
                String idCopia    = p[1];
                String idUsuario  = p[2];
                String idMesa     = p[3];
                Date fechaPrest   = parseFecha(sdf, p[4]);
                Date fechaDev     = NULO.equals(p[5]) ? null : parseFecha(sdf, p[5]);
                boolean activo    = Boolean.parseBoolean(p[6]);

                CopiaJuego copia  = buscarCopia(copias, idCopia);
                Usuario usuario   = buscarUsuario(usuarios, idUsuario);
                Mesa mesa         = NULO.equals(idMesa) ? null : buscarMesa(mesas, idMesa);

                if (copia == null || usuario == null) continue;

                // Usar el constructor de carga (no llama marcarNoDisponible)
                Prestamo pr = new Prestamo(idPrestamo, copia, usuario, mesa,
                        fechaPrest, fechaDev, activo);

                // Si sigue activo, marcar copia como no disponible
                if (activo) {
                    copia.marcarNoDisponible();
                    // Agregar a la mesa si corresponde
                    if (mesa != null) {
                        mesa.getPrestamosActivos().add(pr);
                    }
                }

                prestamos.add(pr);
            }
        }
        return prestamos;
    }

    // ── Utilidades ────────────────────────────────────────────

    private Date parseFecha(SimpleDateFormat sdf, String s) {
        if (s == null || NULO.equals(s)) return new Date();
        try { return sdf.parse(s); } catch (ParseException e) { return new Date(); }
    }

    private CopiaJuego buscarCopia(List<CopiaJuego> copias, String id) {
        for (CopiaJuego c : copias) {
            if (c.getIdCopia().equals(id)) return c;
        }
        return null;
    }

    private Usuario buscarUsuario(List<Usuario> usuarios, String id) {
        for (Usuario u : usuarios) {
            if (u.getIdUsuario().equals(id)) return u;
        }
        return null;
    }

    private Mesa buscarMesa(List<Mesa> mesas, String id) {
        for (Mesa m : mesas) {
            if (m.getIdMesa().equals(id)) return m;
        }
        return null;
    }
}
