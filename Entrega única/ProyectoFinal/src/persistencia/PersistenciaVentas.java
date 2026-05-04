package persistencia;

import Cafeteria.Alimento;
import juegos.JuegoDeMesa;
import transacciones.ItemVenta;
import transacciones.Venta;
import usuarios.Usuario;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Persistencia del historial de ventas (tienda y cafetería).
 *
 * Archivo: datos/ventas.txt
 * Formato por línea (todo en una línea):
 *   idVenta|idComprador|tipo|fecha|subtotal|impuesto|propina|ITEMS
 *   ITEMS: campo con items separados por ";" cada uno con formato:
 *     J,idJuego,cantidad,precio,descuento   (item de juego)
 *     A,idAlimento,cantidad,precio,descuento (item de alimento)
 */
public class PersistenciaVentas {

    private static final String SEP       = "|";
    private static final String SEP_ITEM  = ";";
    private static final String SEP_CAMPO = ",";
    private static final String FECHA_FMT = "yyyy-MM-dd HH:mm:ss";
    private static final String ARCHIVO   = "ventas.txt";

    private String rutaDatos;

    public PersistenciaVentas(String rutaDatos) {
        this.rutaDatos = rutaDatos;
    }

    public void guardar(List<Venta> ventas) throws IOException {
        File dir = new File(rutaDatos);
        if (!dir.exists()) dir.mkdirs();

        SimpleDateFormat sdf = new SimpleDateFormat(FECHA_FMT);
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDatos + ARCHIVO))) {
            for (Venta v : ventas) {
                String items = serializarItems(v.getItems());
                pw.println(v.getIdVenta() + SEP
                        + v.getComprador().getIdUsuario() + SEP
                        + v.getTipo().name() + SEP
                        + sdf.format(v.getFecha()) + SEP
                        + v.getSubtotal() + SEP
                        + v.getImpuesto() + SEP
                        + v.getPropina() + SEP
                        + items);
            }
        }
    }

    public List<Venta> cargar(List<Usuario> usuarios,
                               List<JuegoDeMesa> juegos,
                               List<Alimento> menu) throws IOException {
        List<Venta> ventas = new ArrayList<>();
        File archivo = new File(rutaDatos + ARCHIVO);
        if (!archivo.exists()) return ventas;

        SimpleDateFormat sdf = new SimpleDateFormat(FECHA_FMT);
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] p = linea.split("\\|", -1);
                if (p.length < 8) continue;

                String idVenta     = p[0];
                String idComprador = p[1];
                Venta.TipoVenta tipo = Venta.TipoVenta.valueOf(p[2]);
                Date fecha         = parseFecha(sdf, p[3]);
                double subtotal    = Double.parseDouble(p[4]);
                double impuesto    = Double.parseDouble(p[5]);
                double propina     = Double.parseDouble(p[6]);
                String itemsStr    = p[7];

                Usuario comprador = buscarUsuario(usuarios, idComprador);
                if (comprador == null) continue;

                List<ItemVenta> items = cargarItems(itemsStr, juegos, menu);

                Venta venta = new Venta(idVenta, comprador, items, fecha,
                        tipo, subtotal, impuesto, propina);
                ventas.add(venta);
            }
        }
        return ventas;
    }

    // ── Serialización de items ────────────────────────────────

    private String serializarItems(List<ItemVenta> items) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) sb.append(SEP_ITEM);
            ItemVenta it = items.get(i);
            if (it.esDeJuego()) {
                sb.append("J").append(SEP_CAMPO)
                  .append(it.getJuego().getIdJuego()).append(SEP_CAMPO)
                  .append(it.getCantidad()).append(SEP_CAMPO)
                  .append(it.getPrecioUnitario()).append(SEP_CAMPO)
                  .append(it.getDescuentoAplicado());
            } else {
                sb.append("A").append(SEP_CAMPO)
                  .append(it.getAlimento().getIdProducto()).append(SEP_CAMPO)
                  .append(it.getCantidad()).append(SEP_CAMPO)
                  .append(it.getPrecioUnitario()).append(SEP_CAMPO)
                  .append(it.getDescuentoAplicado());
            }
        }
        return sb.toString();
    }

    private List<ItemVenta> cargarItems(String itemsStr,
                                         List<JuegoDeMesa> juegos,
                                         List<Alimento> menu) {
        List<ItemVenta> items = new ArrayList<>();
        if (itemsStr == null || itemsStr.isEmpty()) return items;

        for (String itemStr : itemsStr.split(SEP_ITEM)) {
            String[] f = itemStr.split(SEP_CAMPO, -1);
            if (f.length < 5) continue;

            int cantidad    = Integer.parseInt(f[2]);
            double precio   = Double.parseDouble(f[3]);
            double descuento = Double.parseDouble(f[4]);

            if ("J".equals(f[0])) {
                JuegoDeMesa j = buscarJuego(juegos, f[1]);
                if (j != null) items.add(new ItemVenta(j, cantidad, precio, descuento));
            } else if ("A".equals(f[0])) {
                Alimento a = buscarAlimento(menu, f[1]);
                if (a != null) items.add(new ItemVenta(a, cantidad, precio, descuento));
            }
        }
        return items;
    }

    // ── Utilidades ────────────────────────────────────────────

    private Date parseFecha(SimpleDateFormat sdf, String s) {
        try { return sdf.parse(s); } catch (ParseException e) { return new Date(); }
    }

    private Usuario buscarUsuario(List<Usuario> usuarios, String id) {
        for (Usuario u : usuarios) {
            if (u.getIdUsuario().equals(id)) return u;
        }
        return null;
    }

    private JuegoDeMesa buscarJuego(List<JuegoDeMesa> juegos, String id) {
        for (JuegoDeMesa j : juegos) {
            if (j.getIdJuego().equals(id)) return j;
        }
        return null;
    }

    private Alimento buscarAlimento(List<Alimento> menu, String id) {
        for (Alimento a : menu) {
            if (a.getIdProducto().equals(id)) return a;
        }
        return null;
    }
}
