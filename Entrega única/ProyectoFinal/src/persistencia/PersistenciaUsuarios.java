package persistencia;

import juegos.JuegoDeMesa;
import usuarios.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistencia de usuarios (Cliente, Mesero, Cocinero, Administrador).
 *
 * Archivo: datos/usuarios.txt
 * Formatos:
 *   CLIENTE   |id|login|pwd|nombre|idCliente|puntos|idFav1,idFav2
 *   MESERO    |id|login|pwd|nombre|codDesc|enTurno|puntos|idFavs|idJuegosConoce
 *   COCINERO  |id|login|pwd|nombre|codDesc|enTurno|puntos|idFavs
 *   ADMIN     |id|login|pwd|nombre
 */
public class PersistenciaUsuarios {

    private static final String SEP       = "|";
    private static final String SEP_LISTA = ",";
    private static final String ARCHIVO   = "usuarios.txt";

    private String rutaDatos;

    public PersistenciaUsuarios(String rutaDatos) {
        this.rutaDatos = rutaDatos;
    }

    public void guardar(List<Usuario> usuarios) throws IOException {
        File dir = new File(rutaDatos);
        if (!dir.exists()) dir.mkdirs();

        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDatos + ARCHIVO))) {
            for (Usuario u : usuarios) {
                if (u instanceof Cliente) {
                    pw.println(serializarCliente((Cliente) u));
                } else if (u instanceof Mesero) {
                    pw.println(serializarMesero((Mesero) u));
                } else if (u instanceof Cocinero) {
                    pw.println(serializarCocinero((Cocinero) u));
                } else if (u instanceof Administrador) {
                    pw.println(serializarAdmin((Administrador) u));
                }
            }
        }
    }

    public List<Usuario> cargar(List<JuegoDeMesa> juegos) throws IOException {
        List<Usuario> usuarios = new ArrayList<>();
        File archivo = new File(rutaDatos + ARCHIVO);
        if (!archivo.exists()) return usuarios;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] p = linea.split("\\|", -1);
                if (p.length < 1) continue;

                switch (p[0]) {
                    case "CLIENTE":   usuarios.add(cargarCliente(p, juegos));   break;
                    case "MESERO":    usuarios.add(cargarMesero(p, juegos));    break;
                    case "COCINERO":  usuarios.add(cargarCocinero(p, juegos)); break;
                    case "ADMIN":     usuarios.add(cargarAdmin(p, juegos));    break;
                    default: break;
                }
            }
        }
        return usuarios;
    }

    // ── Serialización ─────────────────────────────────────────

    private String serializarCliente(Cliente c) {
        return "CLIENTE" + SEP + c.getIdUsuario() + SEP + c.getLogin() + SEP
                + c.getPassword() + SEP + c.getNombre() + SEP
                + c.getIdCliente() + SEP + c.getPuntosFidelidad() + SEP
                + serializarFavoritos(c.getFavoritos());
    }

    private String serializarMesero(Mesero m) {
        StringBuilder juegosConoce = new StringBuilder();
        List<JuegoDeMesa> conoce = m.getJuegosQueConoce();
        for (int i = 0; i < conoce.size(); i++) {
            if (i > 0) juegosConoce.append(SEP_LISTA);
            juegosConoce.append(conoce.get(i).getIdJuego());
        }
        return "MESERO" + SEP + m.getIdUsuario() + SEP + m.getLogin() + SEP
                + m.getPassword() + SEP + m.getNombre() + SEP
                + m.getCodigoDescuento() + SEP + m.estaEnTurno() + SEP
                + m.getPuntosFidelidad() + SEP
                + serializarFavoritos(m.getFavoritos()) + SEP
                + juegosConoce.toString();
    }

    private String serializarCocinero(Cocinero c) {
        return "COCINERO" + SEP + c.getIdUsuario() + SEP + c.getLogin() + SEP
                + c.getPassword() + SEP + c.getNombre() + SEP
                + c.getCodigoDescuento() + SEP + c.estaEnTurno() + SEP
                + c.getPuntosFidelidad() + SEP
                + serializarFavoritos(c.getFavoritos());
    }

    private String serializarAdmin(Administrador a) {
        return "ADMIN" + SEP + a.getIdUsuario() + SEP + a.getLogin() + SEP
                + a.getPassword() + SEP + a.getNombre() + SEP
                + serializarFavoritos(a.getFavoritos());
    }

    private String serializarFavoritos(List<JuegoDeMesa> favs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < favs.size(); i++) {
            if (i > 0) sb.append(SEP_LISTA);
            sb.append(favs.get(i).getIdJuego());
        }
        return sb.toString();
    }

    // ── Carga ─────────────────────────────────────────────────

    private Cliente cargarCliente(String[] p, List<JuegoDeMesa> juegos) {
        // CLIENTE|id|login|pwd|nombre|idCliente|puntos|favs
        Cliente c = new Cliente(p[1], p[2], p[3], p[4], p[5]);
        c.agregarPuntos(Double.parseDouble(p[6]));
        cargarFavoritos(c, p.length > 7 ? p[7] : "", juegos);
        return c;
    }

    private Mesero cargarMesero(String[] p, List<JuegoDeMesa> juegos) {
        // MESERO|id|login|pwd|nombre|codDesc|enTurno|puntos|favs|juegosConoce
        Mesero m = new Mesero(p[1], p[2], p[3], p[4], p[5]);
        m.setEnTurno(Boolean.parseBoolean(p[6]));
        m.agregarPuntos(Double.parseDouble(p[7]));
        cargarFavoritos(m, p.length > 8 ? p[8] : "", juegos);
        if (p.length > 9 && !p[9].isEmpty()) {
            for (String idJ : p[9].split(SEP_LISTA)) {
                JuegoDeMesa j = buscarJuego(juegos, idJ.trim());
                if (j != null) m.aprenderJuego(j);
            }
        }
        return m;
    }

    private Cocinero cargarCocinero(String[] p, List<JuegoDeMesa> juegos) {
        // COCINERO|id|login|pwd|nombre|codDesc|enTurno|puntos|favs
        Cocinero c = new Cocinero(p[1], p[2], p[3], p[4], p[5]);
        c.setEnTurno(Boolean.parseBoolean(p[6]));
        c.agregarPuntos(Double.parseDouble(p[7]));
        cargarFavoritos(c, p.length > 8 ? p[8] : "", juegos);
        return c;
    }

    private Administrador cargarAdmin(String[] p, List<JuegoDeMesa> juegos) {
        // ADMIN|id|login|pwd|nombre|favs
        Administrador a = new Administrador(p[1], p[2], p[3], p[4]);
        cargarFavoritos(a, p.length > 5 ? p[5] : "", juegos);
        return a;
    }

    private void cargarFavoritos(Usuario u, String campo, List<JuegoDeMesa> juegos) {
        if (campo == null || campo.isEmpty()) return;
        for (String idJ : campo.split(SEP_LISTA)) {
            JuegoDeMesa j = buscarJuego(juegos, idJ.trim());
            if (j != null) u.agregarFavorito(j);
        }
    }

    private JuegoDeMesa buscarJuego(List<JuegoDeMesa> juegos, String id) {
        for (JuegoDeMesa j : juegos) {
            if (j.getIdJuego().equals(id)) return j;
        }
        return null;
    }
}
