package consola;

import Cafeteria.Alimento;
import Torneos.torneos;
import inventario.InventarioPrestamo;
import juegos.CopiaJuego;
import juegos.JuegoDeMesa;
import persistencia.PersistenciaCafe;
import sistema.SistemaCafe;
import transacciones.Venta;
import usuarios.Administrador;
import usuarios.Cliente;
import usuarios.Cocinero;
import usuarios.Empleado;
import usuarios.Mesero;
import usuarios.Usuario;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public abstract class ConsolaBase {

    private static final String RUTA_DATOS = "datos_prueba/";

    protected final Scanner          scanner;
    protected final SistemaCafe      sistema;
    private   final PersistenciaCafe persistencia;

    protected ConsolaBase() {
        this.scanner      = new Scanner(System.in);
        int capacidad     = PersistenciaCafe.leerCapacidadMaxima(RUTA_DATOS);
        this.sistema      = new SistemaCafe(capacidad);
        this.persistencia = new PersistenciaCafe(RUTA_DATOS);
        cargarDatos();
    }

    // ── Ciclo de vida ──────────────────────────────────────────────

    private void cargarDatos() {
        try {
            persistencia.cargar(sistema);
        } catch (IOException e) {
            System.out.println("Advertencia: error al cargar datos — " + e.getMessage());
        }
    }

    protected void guardarDatos() {
        try {
            persistencia.guardar(sistema);
        } catch (IOException e) {
            System.out.println("Error al guardar datos: " + e.getMessage());
        }
    }

    protected abstract void ejecutar();

    // ── Autenticación ──────────────────────────────────────────────

    protected Usuario autenticar(String tipoUsuario, Class<? extends Usuario> tipoEsperado) {
        System.out.println("\n=== Autenticación — " + tipoUsuario + " ===");
        while (true) {
            String login    = leerTexto("Login    : ");
            String password = leerTexto("Contraseña: ");
            Usuario usuario = sistema.login(login, password);
            if (usuario == null) {
                System.out.println("  Credenciales incorrectas. Intente de nuevo.\n");
                continue;
            }
            if (!tipoEsperado.isInstance(usuario)) {
                System.out.println("  Este acceso es solo para " + tipoUsuario + ". Intente de nuevo.\n");
                continue;
            }
            System.out.println("  Bienvenido/a, " + usuario.getNombre() + "!\n");
            return usuario;
        }
    }

    // ── Generadores de ID ─────────────────────────────────────────

    protected String generarIdUsuario() {
        int max = 0;
        for (Usuario u : sistema.getUsuarios()) {
            String id = u.getIdUsuario();
            if (id.matches("U\\d+")) {
                int n = Integer.parseInt(id.substring(1));
                if (n > max) max = n;
            }
        }
        return "U" + (max + 1);
    }

    protected String generarIdCliente() {
        int max = 0;
        for (Usuario u : sistema.getUsuarios()) {
            if (u instanceof Cliente) {
                String idCli = ((Cliente) u).getIdCliente();
                if (idCli != null && idCli.matches("CL\\d+")) {
                    int n = Integer.parseInt(idCli.substring(2));
                    if (n > max) max = n;
                }
            }
        }
        return "CL" + (max + 1);
    }

    // ── Validaciones de unicidad ──────────────────────────────────

    protected boolean loginExiste(String login) {
        for (Usuario u : sistema.getUsuarios()) {
            if (u.getLogin().equals(login)) return true;
        }
        return false;
    }

    protected boolean idJuegoExiste(String id) {
        for (JuegoDeMesa j : obtenerTodosLosJuegos()) {
            if (j.getIdJuego().equals(id)) return true;
        }
        return false;
    }

    protected boolean idCopiaExiste(String id) {
        for (CopiaJuego c : sistema.getServicioInventario().getInventarioPrestamo().getCopias()) {
            if (c.getIdCopia().equals(id)) return true;
        }
        return false;
    }

    protected boolean idTorneoExiste(String id) {
        for (torneos t : sistema.getTorneos()) {
            if (t.getIdTorneo().equals(id)) return true;
        }
        return false;
    }

    protected boolean idMenuExiste(String id) {
        for (Alimento a : sistema.getServicioCafeteria().getMenu()) {
            if (a.getIdProducto().equals(id)) return true;
        }
        return false;
    }

    // ── Inventario ────────────────────────────────────────────────

    protected List<JuegoDeMesa> obtenerTodosLosJuegos() {
        List<JuegoDeMesa> todos = new ArrayList<>();
        for (CopiaJuego c : sistema.getServicioInventario().getInventarioPrestamo().getCopias()) {
            if (!todos.contains(c.getJuego())) todos.add(c.getJuego());
        }
        for (JuegoDeMesa j : sistema.getServicioInventario().getInventarioVenta().getJuegos()) {
            if (!todos.contains(j)) todos.add(j);
        }
        return todos;
    }

    protected List<JuegoDeMesa> juegosPrestamosDisponibles(InventarioPrestamo inv) {
        List<JuegoDeMesa> lista = new ArrayList<>();
        for (CopiaJuego c : inv.getCopias()) {
            if (c.isDisponible() && !lista.contains(c.getJuego())) lista.add(c.getJuego());
        }
        return lista;
    }

    // ── Favoritos ────────────────────────────────────────────────

    protected void gestionarFavoritos(Usuario usuario) {
        String[] sub = { "Ver mis favoritos", "Agregar favorito", "Quitar favorito", "Volver" };
        int op;
        do {
            mostrarMenu("Juegos Favoritos", sub);
            op = leerEnteroEnRango("", 1, sub.length);
            switch (op) {
                case 1: verFavoritos(usuario);    break;
                case 2: agregarFavorito(usuario); break;
                case 3: quitarFavorito(usuario);  break;
            }
        } while (op != sub.length);
    }

    protected void verFavoritos(Usuario usuario) {
        List<JuegoDeMesa> favs = usuario.getFavoritos();
        if (favs.isEmpty()) {
            System.out.println("  No tiene juegos favoritos.");
        } else {
            System.out.println("\n  === Mis favoritos ===");
            for (int i = 0; i < favs.size(); i++) {
                System.out.printf("  %d. %s%n", i + 1, favs.get(i).getNombre());
            }
        }
        pausar();
    }

    protected void agregarFavorito(Usuario usuario) {
        List<JuegoDeMesa> todos = obtenerTodosLosJuegos();
        todos.removeAll(usuario.getFavoritos());
        if (todos.isEmpty()) {
            System.out.println("  No hay juegos nuevos para agregar.");
            pausar();
            return;
        }
        System.out.println("\n  === Catálogo ===");
        for (int i = 0; i < todos.size(); i++) {
            System.out.printf("  %d. %s%n", i + 1, todos.get(i).getNombre());
        }
        int idx = leerEnteroEnRango("  Seleccione juego: ", 1, todos.size()) - 1;
        usuario.agregarFavorito(todos.get(idx));
        System.out.println("  Favorito agregado: " + todos.get(idx).getNombre());
        pausar();
    }

    protected void quitarFavorito(Usuario usuario) {
        List<JuegoDeMesa> favs = usuario.getFavoritos();
        if (favs.isEmpty()) {
            System.out.println("  No tiene favoritos.");
            pausar();
            return;
        }
        System.out.println("\n  === Mis favoritos ===");
        for (int i = 0; i < favs.size(); i++) {
            System.out.printf("  %d. %s%n", i + 1, favs.get(i).getNombre());
        }
        int idx = leerEnteroEnRango("  Seleccione juego a quitar: ", 1, favs.size()) - 1;
        JuegoDeMesa juego = favs.get(idx);
        usuario.quitarFavorito(juego);
        System.out.println("  Favorito eliminado: " + juego.getNombre());
        pausar();
    }

    // ── Puntos ───────────────────────────────────────────────────

    protected void verPuntos(Usuario usuario) {
        double puntos = getPuntos(usuario);
        System.out.printf("  Puntos de fidelidad: %.0f  (equivalen a $%.0f de descuento)%n", puntos, puntos);
        if (tieneBono(usuario)) {
            System.out.printf("  Bono torneo amistoso pendiente: %.0f%%%n", getBono(usuario) * 100);
        }
        pausar();
    }

    protected double pedirUsoPuntos(Usuario usuario) {
        double puntos = getPuntos(usuario);
        if (puntos <= 0) return 0;
        System.out.printf("  Tiene %.0f puntos ($%.0f de descuento disponible).%n", puntos, puntos);
        int usar = leerEnteroEnRango("  ¿Cuántos puntos usar? (0 para ninguno): ", 0, (int) puntos);
        if (usar > 0) {
            if (usuario instanceof Cliente)       ((Cliente)  usuario).usarPuntos(usar);
            else if (usuario instanceof Empleado) ((Empleado) usuario).usarPuntos(usar);
        }
        return usar;
    }

    // ── Torneos ──────────────────────────────────────────────────

    protected void inscribirseATorneo(Usuario usuario) {
        List<torneos> disponibles = new ArrayList<>();
        for (torneos t : sistema.getTorneos()) {
            if (t.buscarInscripcion(usuario) == null && t.cuposDisponibles() > 0)
                disponibles.add(t);
        }
        if (disponibles.isEmpty()) {
            System.out.println("  No hay torneos disponibles para inscribirse.");
            pausar();
            return;
        }
        System.out.println("\n  === Torneos disponibles ===");
        for (int i = 0; i < disponibles.size(); i++) {
            torneos t = disponibles.get(i);
            String nota = (usuario instanceof Empleado && !t.esAmistoso())
                    ? " (gratuito para empleados, sin premio)" : "";
            System.out.printf("  %2d. [%-11s] %-20s | Día: %-10s | Cupos: %d%s%n",
                    i + 1, t.getTipo(), t.getNombre(), t.getDiaSemana(),
                    t.cuposDisponibles(), nota);
        }
        int idx           = leerEnteroEnRango("  Seleccione torneo: ", 1, disponibles.size()) - 1;
        int participantes = leerEnteroEnRango("  Número de participantes (1-3): ", 1, 3);
        boolean ok = sistema.inscribirseATorneo(disponibles.get(idx), usuario, participantes);
        if (!ok) System.out.println("  No se pudo completar la inscripción.");
        pausar();
    }

    protected void desinscribirseATorneo(Usuario usuario) {
        List<torneos> inscritos = new ArrayList<>();
        for (torneos t : sistema.getTorneos()) {
            if (t.buscarInscripcion(usuario) != null) inscritos.add(t);
        }
        if (inscritos.isEmpty()) {
            System.out.println("  No está inscrito en ningún torneo.");
            pausar();
            return;
        }
        System.out.println("\n  === Mis torneos ===");
        for (int i = 0; i < inscritos.size(); i++) {
            System.out.printf("  %d. %s (%s)%n",
                    i + 1, inscritos.get(i).getNombre(), inscritos.get(i).getTipo());
        }
        int idx = leerEnteroEnRango("  Seleccione torneo: ", 1, inscritos.size()) - 1;
        sistema.desinscribirseATorneo(inscritos.get(idx), usuario);
        pausar();
    }

    // ── Ventas ────────────────────────────────────────────────────

    protected void mostrarResumenVenta(Venta venta, double puntosUsados) {
        System.out.printf("  Subtotal: $%.2f | Impuesto: $%.2f",
                venta.getSubtotal(), venta.getImpuesto());
        if (venta.getPropina() > 0) {
            System.out.printf(" | Propina: $%.2f", venta.getPropina());
        }
        System.out.printf(" | Total: $%.2f%n", venta.getTotal());
        if (puntosUsados > 0) {
            System.out.printf("  Descuento por puntos: $%.0f | Neto a pagar: $%.2f%n",
                    puntosUsados, venta.getTotal() - puntosUsados);
        }
        System.out.printf("  Puntos ganados esta compra: %.0f%n", venta.calcularPuntosFidelidad());
    }

    // ── Presentación ──────────────────────────────────────────────

    protected void mostrarMenu(String titulo, String[] opciones) {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("  " + titulo);
        System.out.println("══════════════════════════════════════");
        for (int i = 0; i < opciones.length; i++) {
            System.out.printf("  %2d. %s%n", i + 1, opciones[i]);
        }
        System.out.println("══════════════════════════════════════");
        System.out.print("  Opción: ");
    }

    protected void mostrarListaJuegos(List<JuegoDeMesa> juegos) {
        System.out.println();
        for (int i = 0; i < juegos.size(); i++) {
            System.out.printf("  %2d. %s%n", i + 1, juegos.get(i).getNombre());
        }
    }

    protected String tipoDe(Usuario u) {
        if (u instanceof Administrador) return "Admin";
        if (u instanceof Mesero)        return "Mesero";
        if (u instanceof Cocinero)      return "Cocinero";
        if (u instanceof Cliente)       return "Cliente";
        if (u instanceof Empleado)      return "Empleado";
        return "Desconocido";
    }

    protected void pausar() {
        System.out.print("\n  Presione Enter para continuar...");
        scanner.nextLine();
    }

    // ── Lectura con validación ─────────────────────────────────────

    protected int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Ingrese un número entero válido.");
            }
        }
    }

    protected int leerEnteroEnRango(String mensaje, int min, int max) {
        while (true) {
            int valor = leerEntero(mensaje);
            if (valor >= min && valor <= max) return valor;
            System.out.printf("  El valor debe estar entre %d y %d.%n", min, max);
        }
    }

    protected String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            if (!texto.isEmpty()) return texto;
            System.out.println("  Este campo no puede estar vacío.");
        }
    }

    protected double leerDouble(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                double val = Double.parseDouble(scanner.nextLine().trim());
                if (val >= 0) return val;
                System.out.println("  El valor debe ser positivo.");
            } catch (NumberFormatException e) {
                System.out.println("  Ingrese un número decimal válido.");
            }
        }
    }

    protected boolean leerSiNo(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (s/n): ");
            String r = scanner.nextLine().trim().toLowerCase();
            if (r.equals("s") || r.equals("si") || r.equals("sí")) return true;
            if (r.equals("n") || r.equals("no"))                    return false;
            System.out.println("  Ingrese 's' o 'n'.");
        }
    }

    protected Date leerFecha(String mensaje) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        while (true) {
            System.out.print(mensaje);
            try {
                return sdf.parse(scanner.nextLine().trim());
            } catch (ParseException e) {
                System.out.println("  Formato inválido. Use dd/MM/yyyy (ej: 01/05/2025).");
            }
        }
    }

    // ── Helpers privados ─────────────────────────────────────────

    private double getPuntos(Usuario usuario) {
        if (usuario instanceof Cliente)  return ((Cliente)  usuario).getPuntosFidelidad();
        if (usuario instanceof Empleado) return ((Empleado) usuario).getPuntosFidelidad();
        return 0;
    }

    private boolean tieneBono(Usuario usuario) {
        if (usuario instanceof Cliente)  return ((Cliente)  usuario).tieneBonoTorneoAmistoso();
        if (usuario instanceof Empleado) return ((Empleado) usuario).tieneBonoTorneoAmistoso();
        return false;
    }

    private double getBono(Usuario usuario) {
        if (usuario instanceof Cliente)  return ((Cliente)  usuario).getBonoTorneoAmistoso();
        if (usuario instanceof Empleado) return ((Empleado) usuario).getBonoTorneoAmistoso();
        return 0;
    }
}
