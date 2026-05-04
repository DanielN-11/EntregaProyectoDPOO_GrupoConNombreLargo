package consola;

import Cafeteria.Mesa;
import Cafeteria.SolicitudCambioTurno;
import Cafeteria.Alimento;
import inventario.InventarioPrestamo;
import inventario.InventarioVenta;
import juegos.CopiaJuego;
import juegos.JuegoDeMesa;
import transacciones.ItemVenta;
import transacciones.Prestamo;
import transacciones.Venta;
import usuarios.Empleado;

import java.util.ArrayList;
import java.util.List;

public class ConsolaEmpleado extends ConsolaBase {

    private static final String[] OPCIONES = {
        "Ver mi turno actual",
        "Solicitar cambio de turno",
        "Pedir préstamo de juego",
        "Devolver juego prestado",
        "Realizar pedido en cafetería",
        "Comprar juego en tienda",
        "Sugerir item al menú",
        "Ver mis puntos de fidelidad",
        "Ver / agregar juegos favoritos",
        "Inscribirse a torneo",
        "Desinscribirse de torneo",
        "Salir"
    };

    public static void main(String[] args) {
        ConsolaEmpleado consola = new ConsolaEmpleado();
        consola.ejecutar();
        consola.guardarDatos();
    }

    @Override
    protected void ejecutar() {
        Empleado empleado = (Empleado) autenticar("Empleado", Empleado.class);
        int opcion;
        do {
            mostrarMenu("Menú Empleado — " + empleado.getNombre(), OPCIONES);
            opcion = leerEnteroEnRango("", 1, OPCIONES.length);
            procesarOpcion(opcion, empleado);
        } while (opcion != OPCIONES.length);
    }

    private void procesarOpcion(int opcion, Empleado empleado) {
        switch (opcion) {
            case 1:  verTurno(empleado);                break;
            case 2:  solicitarCambioTurno(empleado);    break;
            case 3:  pedirPrestamo(empleado);           break;
            case 4:  devolverJuego(empleado);           break;
            case 5:  pedidoCafeteria(empleado);         break;
            case 6:  comprarJuego(empleado);            break;
            case 7:  sugerirItemMenu(empleado);         break;
            case 8:  verPuntos(empleado);               break;
            case 9:  gestionarFavoritos(empleado);      break;
            case 10: inscribirseATorneo(empleado);      break;
            case 11: desinscribirseATorneo(empleado);   break;
            case 12: System.out.println("  Hasta luego, " + empleado.getNombre() + "!"); break;
            default: System.out.println("  Opción no válida.");
        }
    }

    // ----- Turnos -------------------------------------------------

    private void verTurno(Empleado empleado) {
        if (!empleado.estaEnTurno() || empleado.getTurno() == null) {
            System.out.println("  No tiene turno asignado actualmente.");
        } else {
            System.out.println("  Turno asignado:");
            System.out.println("    ID   : " + empleado.getTurno().getIdTurno());
            System.out.println("    Día  : " + empleado.getTurno().getDiaSemana());
            System.out.println("    Estado: En turno");
        }
        pausar();
    }

    private void solicitarCambioTurno(Empleado empleado) {
        if (empleado.getTurno() == null) {
            System.out.println("  No tiene turno asignado — no puede solicitar cambio.");
            pausar();
            return;
        }

        String[] tipos = { "Cambio general (liberar turno)", "Intercambio con otro empleado", "Cancelar" };
        mostrarMenu("Tipo de solicitud", tipos);
        int tipo = leerEnteroEnRango("", 1, tipos.length);
        if (tipo == tipos.length) { pausar(); return; }

        String idSolicitud = "SOL-" + System.currentTimeMillis();

        if (tipo == 1) {
            SolicitudCambioTurno solicitud = new SolicitudCambioTurno(
                    idSolicitud, empleado, empleado.getTurno(), null,
                    SolicitudCambioTurno.CAMBIO_GENERAL);
            sistema.solicitarCambioTurno(solicitud);
            System.out.println("  Solicitud enviada. Queda pendiente de aprobación.");
        } else {
            List<Empleado> otrosConTurno = otrosEmpleadosConTurno(empleado);
            if (otrosConTurno.isEmpty()) {
                System.out.println("  No hay otros empleados con turno para intercambiar.");
                pausar();
                return;
            }
            System.out.println("\n  === Empleados disponibles para intercambio ===");
            for (int i = 0; i < otrosConTurno.size(); i++) {
                Empleado e = otrosConTurno.get(i);
                System.out.printf("  %d. %-20s | Turno: %s%n",
                        i + 1, e.getNombre(), e.getTurno().getDiaSemana());
            }
            int idx = leerEnteroEnRango("  Seleccione empleado: ", 1, otrosConTurno.size()) - 1;
            Empleado destino = otrosConTurno.get(idx);
            SolicitudCambioTurno solicitud = new SolicitudCambioTurno(
                    idSolicitud, empleado, empleado.getTurno(), destino,
                    SolicitudCambioTurno.INTERCAMBIO);
            sistema.solicitarCambioTurno(solicitud);
            System.out.println("  Solicitud de intercambio enviada con " + destino.getNombre() + ".");
        }
        pausar();
    }

    // ----- Préstamos -------------------------------------------------

    private void pedirPrestamo(Empleado empleado) {
        if (empleado.estaEnTurno() && !mesasOcupadas().isEmpty()) {
            System.out.println("  No puede pedir prestado: está en turno y hay clientes.");
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
        Prestamo prestamo = sistema.prestarJuego(empleado, disponibles.get(idx), null);
        if (prestamo != null) {
            System.out.println("  Préstamo registrado: " + disponibles.get(idx).getNombre());
        }
        pausar();
    }

    private void devolverJuego(Empleado empleado) {
        List<Prestamo> activos = prestamosActivosDelEmpleado(empleado);
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

    // ----- Cafetería -------------------------------------------------

    private void pedidoCafeteria(Empleado empleado) {
        List<Mesa> ocupadas = mesasOcupadas();
        if (ocupadas.isEmpty()) {
            System.out.println("  No hay mesas ocupadas en este momento.");
            pausar();
            return;
        }
        List<Alimento> menu = sistema.getServicioCafeteria().getMenu();
        if (menu.isEmpty()) {
            System.out.println("  El menú está vacío.");
            pausar();
            return;
        }

        System.out.println("\n  === Mesas ocupadas ===");
        for (int i = 0; i < ocupadas.size(); i++) {
            System.out.printf("  %d. Mesa %s (%d personas)%n",
                    i + 1, ocupadas.get(i).getIdMesa(), ocupadas.get(i).getNumeroPersonas());
        }
        int idxMesa = leerEnteroEnRango("  ¿Para qué mesa es el pedido?: ", 1, ocupadas.size()) - 1;
        Mesa mesa = ocupadas.get(idxMesa);

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

        if (empleado.tieneBonoTorneoAmistoso()) {
            System.out.printf("  Bono de torneo (%.0f%%) se aplicará automáticamente.%n",
                    empleado.getBonoTorneoAmistoso() * 100);
        } else {
            System.out.println("  Descuento de empleado (20%) aplicado automáticamente.");
        }
        double puntosAUsar = pedirUsoPuntos(empleado);

        Venta venta = sistema.realizarVentaCafeteria(empleado, mesa, items);
        if (venta != null) {
            mostrarResumenVenta(venta, puntosAUsar);
        }
        pausar();
    }

    // ----- Tienda -------------------------------------------------

    private void comprarJuego(Empleado empleado) {
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

        if (empleado.tieneBonoTorneoAmistoso()) {
            System.out.printf("  Bono de torneo (%.0f%%) se aplicará automáticamente.%n",
                    empleado.getBonoTorneoAmistoso() * 100);
        } else {
            System.out.println("  Descuento de empleado (20%) aplicado automáticamente.");
        }
        double puntosAUsar = pedirUsoPuntos(empleado);

        Venta venta = sistema.realizarVentaTienda(empleado, items);
        if (venta != null) {
            mostrarResumenVenta(venta, puntosAUsar);
        }
        pausar();
    }

    // ----- Sugerencias -------------------------------------------------

    private void sugerirItemMenu(Empleado empleado) {
        String nombre    = leerTexto("  Nombre del item propuesto: ");
        String categoria = leerTexto("  Categoría (ej: Bebida, Pastelería, Plato): ");
        String id        = "SUG-" + System.currentTimeMillis();
        Servicios.Sugerencia sugerencia = new Servicios.Sugerencia(id, empleado, nombre, categoria);
        sistema.agregarSugerencia(sugerencia);
        System.out.println("  Sugerencia enviada. Queda pendiente de aprobación del administrador.");
        pausar();
    }

    // ----- Helpers -------------------------------------------------

    private List<Prestamo> prestamosActivosDelEmpleado(Empleado empleado) {
        List<Prestamo> activos = new ArrayList<>();
        for (Prestamo p : sistema.getServicioPrestamo().getHistorialPrestamos()) {
            if (p.isActivo() && p.getUsuario().equals(empleado)) activos.add(p);
        }
        return activos;
    }

    private List<Mesa> mesasOcupadas() {
        List<Mesa> ocupadas = new ArrayList<>();
        for (Mesa m : sistema.getMesas()) {
            if (m.isOcupada()) ocupadas.add(m);
        }
        return ocupadas;
    }

    private List<Empleado> otrosEmpleadosConTurno(Empleado solicitante) {
        List<Empleado> lista = new ArrayList<>();
        for (Empleado e : sistema.getServicioTurnos().getEmpleados()) {
            if (!e.equals(solicitante) && e.getTurno() != null) lista.add(e);
        }
        return lista;
    }
}