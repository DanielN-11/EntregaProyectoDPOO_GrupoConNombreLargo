package persistencia;

import Cafeteria.Alimento;
import Cafeteria.Bebida;
import Cafeteria.Pasteleria;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Persistencia del menú del café (Bebidas y Pastelería).
 *
 * Archivo: datos/menu.txt
 * Formatos:
 *   BEBIDA    |id|nombre|precio|alcoholica|caliente
 *   PASTELERIA|id|nombre|precio|alergeno1,alergeno2
 */
public class PersistenciaMenu {

    private static final String SEP       = "|";
    private static final String SEP_LISTA = ",";
    private static final String ARCHIVO   = "menu.txt";

    private String rutaDatos;

    public PersistenciaMenu(String rutaDatos) {
        this.rutaDatos = rutaDatos;
    }

    public void guardar(List<Alimento> menu) throws IOException {
        File dir = new File(rutaDatos);
        if (!dir.exists()) dir.mkdirs();

        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDatos + ARCHIVO))) {
            for (Alimento a : menu) {
                if (a instanceof Bebida) {
                    Bebida b = (Bebida) a;
                    pw.println("BEBIDA" + SEP + b.getIdProducto() + SEP
                            + b.getNombre() + SEP + b.getPrecio() + SEP
                            + b.isAlcoholica() + SEP + b.isCaliente());
                } else if (a instanceof Pasteleria) {
                    Pasteleria ps = (Pasteleria) a;
                    String alergenos = String.join(SEP_LISTA, ps.getAlergenos());
                    pw.println("PASTELERIA" + SEP + ps.getIdProducto() + SEP
                            + ps.getNombre() + SEP + ps.getPrecio() + SEP
                            + alergenos);
                }
            }
        }
    }

    public List<Alimento> cargar() throws IOException {
        List<Alimento> menu = new ArrayList<>();
        File archivo = new File(rutaDatos + ARCHIVO);
        if (!archivo.exists()) return menu;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] p = linea.split("\\|", -1);
                if (p.length < 4) continue;

                switch (p[0]) {
                    case "BEBIDA":
                        if (p.length >= 6) {
                            menu.add(new Bebida(p[1], p[2],
                                    Double.parseDouble(p[3]),
                                    Boolean.parseBoolean(p[4]),
                                    Boolean.parseBoolean(p[5])));
                        }
                        break;
                    case "PASTELERIA":
                        List<String> alergenos = new ArrayList<>();
                        if (p.length >= 5 && !p[4].isEmpty()) {
                            alergenos = new ArrayList<>(Arrays.asList(p[4].split(SEP_LISTA)));
                        }
                        menu.add(new Pasteleria(p[1], p[2],
                                Double.parseDouble(p[3]), alergenos));
                        break;
                    default: break;
                }
            }
        }
        return menu;
    }
}
