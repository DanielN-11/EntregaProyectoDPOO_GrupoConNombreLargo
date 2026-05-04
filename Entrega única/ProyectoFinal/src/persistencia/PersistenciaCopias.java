package persistencia;

import inventario.InventarioPrestamo;
import inventario.InventarioVenta;
import juegos.CopiaJuego;
import juegos.JuegoDeMesa;

import java.io.*;
import java.util.List;

/**
 * Persistencia de copias de juegos para préstamo e inventario de venta.
 *
 * Archivo: datos/copias_prestamo.txt
 * Formato: idCopia|idJuego|estado|disponible
 *
 * Archivo: datos/inventario_venta.txt
 * Formato: idJuego|cantidad|precio
 */
public class PersistenciaCopias {

    private static final String SEP = "|";
    private static final String ARCHIVO_PRESTAMO = "copias_prestamo.txt";
    private static final String ARCHIVO_VENTA    = "inventario_venta.txt";

    private String rutaDatos;

    public PersistenciaCopias(String rutaDatos) {
        this.rutaDatos = rutaDatos;
    }

    // ── Inventario de préstamo ────────────────────────────────

    public void guardarCopiasPrestamo(InventarioPrestamo inv) throws IOException {
        File dir = new File(rutaDatos);
        if (!dir.exists()) dir.mkdirs();

        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDatos + ARCHIVO_PRESTAMO))) {
            for (CopiaJuego c : inv.getCopias()) {
                pw.println(c.getIdCopia() + SEP
                        + c.getJuego().getIdJuego() + SEP
                        + c.getEstadoCopia() + SEP
                        + c.isDisponible());
            }
        }
    }

    public void cargarCopiasPrestamo(InventarioPrestamo inv, List<JuegoDeMesa> juegos) throws IOException {
        File archivo = new File(rutaDatos + ARCHIVO_PRESTAMO);
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] p = linea.split("\\|", -1);
                if (p.length < 4) continue;

                String idCopia  = p[0];
                String idJuego  = p[1];
                String estado   = p[2];
                boolean dispon  = Boolean.parseBoolean(p[3]);

                JuegoDeMesa juego = buscarJuego(juegos, idJuego);
                if (juego == null) continue;

                CopiaJuego copia = new CopiaJuego(idCopia, juego, estado, dispon);
                inv.agregarCopia(copia);
            }
        }
    }

    // ── Inventario de venta ───────────────────────────────────

    public void guardarInventarioVenta(InventarioVenta inv) throws IOException {
        File dir = new File(rutaDatos);
        if (!dir.exists()) dir.mkdirs();

        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDatos + ARCHIVO_VENTA))) {
            for (JuegoDeMesa j : inv.getJuegos()) {
                pw.println(j.getIdJuego() + SEP
                        + inv.getCantidad(j) + SEP
                        + inv.getPrecio(j));
            }
        }
    }

    public void cargarInventarioVenta(InventarioVenta inv, List<JuegoDeMesa> juegos) throws IOException {
        File archivo = new File(rutaDatos + ARCHIVO_VENTA);
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] p = linea.split("\\|", -1);
                if (p.length < 3) continue;

                String idJuego = p[0];
                int cantidad   = Integer.parseInt(p[1]);
                double precio  = Double.parseDouble(p[2]);

                JuegoDeMesa juego = buscarJuego(juegos, idJuego);
                if (juego == null) continue;

                inv.reabastecer(juego, cantidad, precio);
            }
        }
    }

    // ── Utilidad ──────────────────────────────────────────────

    private JuegoDeMesa buscarJuego(List<JuegoDeMesa> juegos, String idJuego) {
        for (JuegoDeMesa j : juegos) {
            if (j.getIdJuego().equals(idJuego)) return j;
        }
        return null;
    }
}
