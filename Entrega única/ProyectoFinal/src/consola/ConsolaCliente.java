package consola;

import Cafeteria.Alimento;
import Torneos.torneos;
import inventario.InventarioPrestamo;
import inventario.InventarioVenta;
import juegos.CopiaJuego;
import juegos.JuegoDeMesa;
import transacciones.ItemVenta;
import transacciones.Prestamo;
import transacciones.Venta;
import usuarios.Cliente;

import java.util.ArrayList;
import java.util.List;

public class ConsolaCliente extends ConsolaBase {

    private static final String[] OPCIONES = {
        "Registrar mesa",
        "Liberar mesa",
        "Ver catálogo de juegos",
        "Pedir préstamo de juego",
        "Devolver juego prestado",
        "Realizar pedido en cafetería",
        "Comprar juego en tienda",
        "Ver mis puntos de fidelidad",
        "Ver / agregar juegos favoritos",
        "Ver torneos disponibles",
        "Inscribirse a torneo",
        "Desinscribirse de torneo",
        "Salir"
    };

    public static void main(String[] args) {
        ConsolaCliente consola = new ConsolaCliente();
        consola.ejecutar();
        consola.guardarDatos();
    }

    @Override
    protected void ejecutar() {
        Cliente cliente = menuInicial();
        int opcion;
        do {
            mostrarMenu("Menú Cliente — " + cliente.getNombre(), OPCIONES);
            opcion = leerEnteroEnRango("", 1, OPCIONES.length);
            procesarOpcion(opcion, cliente);
        } while (opcion != OPCIONES.length);
    }

    private Cliente menuInicial() {
        while (true) {
            System.out.println("\n══════════════════════════════════════");
            System.out.println("  Café de Juegos — Área de Clientes");
            System.out.println("══════════════════════════════════════");
            System.out.println("   1. Iniciar sesión");
            System.out.println("   2. Registrarse");
            System.out.println("══════════════════════════════════════");
            int op = leerEnteroEnRango("  Opción: ", 1, 2);
            if (op == 1) {
                return (Cliente) autenticar("Cliente", Cliente.class);
            } else {
                Cliente nuevo = registrarNuevoCliente();
                if (nuevo != null) return nuevo;
            }
        }
    }

    private Cliente registrarNuevoCliente() {
        System.out.println("\n  === Registro de nuevo cliente ===");
        String nombre = leerTexto("  Nombre completo: ");
        String login;
        while (true) {
            login = leerTexto("  Login (usuario): ");
            if (!loginExiste(login)) break;
            System.out.println("  Ese login ya está en uso. Elija otro.");
        }
        String password = leerTexto("  Contraseña: ");
        String id = generarIdUsuario();
        String idCliente = generarIdCliente();
        Cliente cliente = new Cliente(id, login, password, nombre, idCliente);
        sistema.agregarUsuario(cliente);
        guardarDatos();
        System.out.println("  Registro exitoso. Bienvenido/a, " + nombre + "!");
        return cliente;
    }

    private void procesarOpcion(int opcion, Cliente cliente) {
        switch (opcion) {
            case 1:  registrarMesa(cliente);              break;
            case 2:  liberarMesa(cliente);                break;
            case 3:  verCatalogoJuegos();                 break;
            case 4:  pedirPrestamo(cliente);              break;
            case 5:  devolverJuego(cliente);              break;
            case 6:  pedidoCafeteria(cliente);            break;
            case 7:  comprarJuego(cliente);               break;
            case 8:  verPuntos(cliente);                  break;
            case 9:  gestionarFavoritos(cliente);         break;
            case 10: verTorneos();                        break;
            case 11: inscribirseATorneo(cliente);         break;
            case 12: desinscribirseATorneo(cliente);      break;
            case 13: System.out.println("  Hasta luego, " + cliente.getNombre() + "!"); break;
            default: System.out.println("  Opción no válida.");
        }
    }

    // -----Mesas---------------------------------------------------

    private void registrarMesa(Cliente cliente) {
        if (cliente.tieneMesa()) {
            System.out.println("  Ya tiene la mesa " + cliente.getMesaActual().getIdMesa() + " asignada.");
            pausar();
            return;
        }
        int personas = leerEnteroEnRango("  Número de personas (1-20): ", 1, 20);
        int ninos    = leerEnteroEnRango("  Número de niños (0-" + personas + "): ", 0, personas);
        int jovenes  = leerEnteroEnRango("  Número de jóvenes (0-" + (personas - ninos) + "): ", 0, personas - ninos);

        Cafeteria.Mesa mesa = sistema.registrarMesa(personas, ninos, jovenes, cliente);
        if (mesa != null) {
            System.out.println("  Mesa asignada: " + mesa.getIdMesa());
        }
        pausar();
    }

    private void liberarMesa(Cliente cliente) {
        if (!cliente.tieneMesa()) {
            System.out.println("  No tiene mesa asignada.");
            pausar();
            return;
        }
        sistema.liberarMesa(cliente);
        System.out.println("  Mesa liberada correctamente.");
        pausar();
    }

    // -----Catálogo---------------------------------------------------

    private void verCatalogoJuegos() {
        List<JuegoDeMesa> juegos = obtenerTodosLosJuegos();
        if (juegos.isEmpty()) {
            System.out.println("  No hay juegos registrados.");
            pausar();
            return;
        }
        InventarioPrestamo invP = sistema.getServicioInventario().getInventarioPrestamo();
        InventarioVenta    invV = sistema.getServicioInventario().getInventarioVenta();

        System.out.println("\n  === Catálogo de juegos ===");
        System.out.printf("  %-3s %-25s %-12s %-22s %s%n",
                "#", "Nombre", "Jugadores", "Préstamo", "Venta");
        System.out.println("  " + "-".repeat(75));
        for (int i = 0; i < juegos.size(); i++) {
            JuegoDeMesa j = juegos.get(i);
            int copiasDisp = 0;
            for (CopiaJuego c : invP.getCopiasPorJuego(j)) {
                if (c.isDisponible()) copiasDisp++;
            }
            String prestamo = copiasDisp > 0 ? copiasDisp + " copia(s)" : "Sin copias";
            String venta    = invV.hayStock(j)
                    ? "$" + String.format("%.2f", invV.getPrecio(j)) + " (" + invV.getCantidad(j) + "u)"
                    : "-";
            System.out.printf("  %-3d %-25s %d-%d jug.       %-22s %s%n",
                    i + 1, j.getNombre(), j.getMinJugadores(), j.getMaxJugadores(), prestamo, venta);
        }
        pausar();
    }

    // -----Préstamos---------------------------------------------------

    private void pedirPrestamo(Cliente cliente) {
        if (!cliente.tieneMesa()) {
            System.out.println("  Necesita una mesa asignada para pedir préstamo.");
            pausar();
            return;
        }
        InventarioPrestamo inv = sistema.getServicioInventario().getInventarioPrestamo();
        List<JuegoDeMesa> disponibles = juegosPrestamosDisponibles(inv);
        if (disponibles.isEmpty()) {
            System.out.println("  No hay juegos disponibles para préstamo.");
            pausar();
            return;
        }
        System.out.println("\n  === Juegos disponibles para préstamo ===");
        for (int i = 0; i < disponibles.size(); i++) {
            JuegoDeMesa j = disponibles.get(i);
            int copias = 0;
            for (CopiaJuego c : inv.getCopiasPorJuego(j)) {
                if (c.isDisponible()) copias++;
            }
            System.out.printf("  %2d. %-25s | %d-%d jugadores | %d copia(s)%n",
                    i + 1, j.getNombre(), j.getMinJugadores(), j.getMaxJugadores(), copias);
        }
        int idx = leerEnteroEnRango("  Seleccione juego: ", 1, disponibles.size()) - 1;
        Prestamo prestamo = sistema.prestarJuego(cliente, disponibles.get(idx), cliente.getMesaActual());
        if (prestamo != null) {
            System.out.println("  Préstamo registrado: " + disponibles.get(idx).getNombre());
        }
        pausar();
    }

    private void devolverJuego(Cliente cliente) {
        if (!cliente.tieneMesa()) {
            System.out.println("  No tiene mesa asignada.");
            pausar();
            return;
        }
        List<Prestamo> activos = cliente.getMesaActual().getPrestamosActivos();
        if (activos.isEmpty()) {
            System.out.println("  No tiene juegos en préstamo.");
            pausar();
            return;
        }
        System.out.println("\n  === Juegos en préstamo ===");
        for (int i = 0; i < activos.size(); i++) {
            System.out.printf("  %d. %s%n", i + 1, activos.get(i).getCopia().getJuego().getNombre());
        }
        int idx = leerEnteroEnRango("  Seleccione juego a devolver: ", 1, activos.size()) - 1;
        sistema.devolverJuego(activos.get(idx));
        System.out.println("  Juego devuelto correctamente.");
        pausar();
    }

    // -----Cafetería---------------------------------------------------

    private void pedidoCafeteria(Cliente cliente) {
        if (!cliente.tieneMesa()) {
            System.out.println("  Necesita una mesa asignada para ordenar.");
            pausar();
            return;
        }
        List<Alimento> menu = sistema.getServicioCafeteria().getMenu();
        if (menu.isEmpty()) {
            System.out.println("  El menú está vacío.");
            pausar();
            return;
        }
        System.out.println("\n  === Menú ===");
        for (int i = 0; i < menu.size(); i++) {
            System.out.printf("  %2d. %-25s $%.2f%n", i + 1, menu.get(i).getNombre(), menu.get(i).getPrecio());
        }

        List<ItemVenta> items = new ArrayList<>();
        while (true) {
            int idx = leerEnteroEnRango("  Seleccione item (0 para terminar): ", 0, menu.size());
            if (idx == 0) break;
            int cantidad = leerEnteroEnRango("  Cantidad: ", 1, 20);
            Alimento a = menu.get(idx - 1);
            items.add(new ItemVenta(a, cantidad, a.getPrecio(), 0));
        }
        if (items.isEmpty()) {
            System.out.println("  Pedido cancelado.");
            pausar();
            return;
        }

        String codigo = pedirCodigoDescuento(cliente);
        double propina = pedirPropina();
        double puntosAUsar = pedirUsoPuntos(cliente);

        Venta venta = sistema.realizarVentaCafeteria(cliente, cliente.getMesaActual(), items, codigo, propina);
        if (venta != null) {
            mostrarResumenVenta(venta, puntosAUsar);
        }
        pausar();
    }

    // -----Tienda---------------------------------------------------

    private void comprarJuego(Cliente cliente) {
        InventarioVenta inv = sistema.getServicioInventario().getInventarioVenta();
        List<JuegoDeMesa> enVenta = new ArrayList<>();
        for (JuegoDeMesa j : inv.getJuegos()) {
            if (inv.hayStock(j)) enVenta.add(j);
        }
        if (enVenta.isEmpty()) {
            System.out.println("  No hay juegos disponibles para compra.");
            pausar();
            return;
        }
        System.out.println("\n  === Juegos en venta ===");
        for (int i = 0; i < enVenta.size(); i++) {
            JuegoDeMesa j = enVenta.get(i);
            System.out.printf("  %2d. %-25s $%.2f | Stock: %d%n",
                    i + 1, j.getNombre(), inv.getPrecio(j), inv.getCantidad(j));
        }

        List<ItemVenta> items = new ArrayList<>();
        while (true) {
            int idx = leerEnteroEnRango("  Seleccione juego (0 para terminar): ", 0, enVenta.size());
            if (idx == 0) break;
            JuegoDeMesa juego = enVenta.get(idx - 1);
            int max = inv.getCantidad(juego);
            int cantidad = leerEnteroEnRango("  Cantidad (máx " + max + "): ", 1, max);
            items.add(new ItemVenta(juego, cantidad, inv.getPrecio(juego), 0));
        }
        if (items.isEmpty()) {
            System.out.println("  Compra cancelada.");
            pausar();
            return;
        }

        String codigo = pedirCodigoDescuento(cliente);
        double puntosAUsar = pedirUsoPuntos(cliente);

        Venta venta = sistema.realizarVentaTienda(cliente, items, codigo);
        if (venta != null) {
            mostrarResumenVenta(venta, puntosAUsar);
        }
        pausar();
    }

    // -----Torneos-----------------------------------------------

    private void verTorneos() {
        List<torneos> lista = sistema.getTorneos();
        if (lista.isEmpty()) {
            System.out.println("  No hay torneos disponibles.");
            pausar();
            return;
        }
        System.out.println("\n  === Torneos disponibles ===");
        for (int i = 0; i < lista.size(); i++) {
            torneos t = lista.get(i);
            System.out.printf("  %2d. [%-11s] %-20s | Juego: %-20s | Día: %-10s | Cupos: %d/%d%n",
                    i + 1, t.getTipo(), t.getNombre(), t.getJuego().getNombre(),
                    t.getDiaSemana(), t.cuposOcupados(), t.getCupoMaximo());
        }
        pausar();
    }

    // ----- Helpers de compra -----------------------------------------------

    private double pedirPropina() {
        System.out.print("  Propina sugerida: 10%. Ingrese % deseado (Enter para aceptar): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return 0.10;
        try {
            double p = Double.parseDouble(input);
            if (p < 0) {
                System.out.println("  Valor inválido. Se aplicará 10%.");
                return 0.10;
            }
            return p / 100.0;
        } catch (NumberFormatException e) {
            System.out.println("  Entrada inválida. Se aplicará 10%.");
            return 0.10;
        }
    }

    private String pedirCodigoDescuento(Cliente cliente) {
        if (cliente.tieneBonoTorneoAmistoso()) {
            System.out.printf("  Bono de torneo amistoso (%.0f%%) se aplicará automáticamente.%n",
                    cliente.getBonoTorneoAmistoso() * 100);
            return null;
        }
        System.out.print("  ¿Tiene código de descuento? (Enter para omitir): ");
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? null : input;
    }

    // ----- Helpers de inventario -------------------------------------------------

}