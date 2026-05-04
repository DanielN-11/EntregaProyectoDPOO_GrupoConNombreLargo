package persistencia;

import juegos.JuegoDeMesa;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistencia de juegos de mesa.
 * Archivo: datos/juegos.txt
 * Formato por línea: idJuego|nombre|anio|empresa|minJ|maxJ|edadMin|categoria|dificil
 */
public class PersistenciaJuegos {

    private static final String SEP = "|";
    private static final String ARCHIVO = "juegos.txt";

    private String rutaDatos;

    public PersistenciaJuegos(String rutaDatos) {
        this.rutaDatos = rutaDatos;
    }

    public void guardar(List<JuegoDeMesa> juegos) throws IOException {
        File dir = new File(rutaDatos);
        if (!dir.exists()) dir.mkdirs();

        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDatos + ARCHIVO))) {
            for (JuegoDeMesa j : juegos) {
                pw.println(j.getIdJuego() + SEP
                        + j.getNombre() + SEP
                        + j.getAnioPublicacion() + SEP
                        + j.getEmpresaMatriz() + SEP
                        + j.getMinJugadores() + SEP
                        + j.getMaxJugadores() + SEP
                        + j.getEdadMinima() + SEP
                        + j.getCategoria() + SEP
                        + j.esDificil());
            }
        }
    }

    public List<JuegoDeMesa> cargar() throws IOException {
        List<JuegoDeMesa> juegos = new ArrayList<>();
        File archivo = new File(rutaDatos + ARCHIVO);
        if (!archivo.exists()) return juegos;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] p = linea.split("\\|", -1);
                if (p.length < 9) continue;

                String id        = p[0];
                String nombre    = p[1];
                int anio         = Integer.parseInt(p[2]);
                String empresa   = p[3];
                int minJ         = Integer.parseInt(p[4]);
                int maxJ         = Integer.parseInt(p[5]);
                int edadMin      = Integer.parseInt(p[6]);
                String cat       = p[7];
                boolean dificil  = Boolean.parseBoolean(p[8]);

                juegos.add(new JuegoDeMesa(id, nombre, anio, empresa, minJ, maxJ, edadMin, cat, dificil));
            }
        }
        return juegos;
    }
}
