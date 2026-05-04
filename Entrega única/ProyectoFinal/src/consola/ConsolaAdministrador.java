package consola;

import Cafeteria.Alimento;
import Cafeteria.Bebida;
import Cafeteria.Pasteleria;
import Cafeteria.SolicitudCambioTurno;
import Cafeteria.Turno;
import Servicios.ServicioReportes;
import Servicios.Sugerencia;
import Torneos.torneoAmistoso;
import Torneos.torneoCompetitivo;
import Torneos.torneos;
import inventario.InventarioVenta;
import juegos.CopiaJuego;
import juegos.JuegoDeMesa;
import transacciones.Venta;
import usuarios.Administrador;
import usuarios.Cliente;
import usuarios.Cocinero;
import usuarios.Empleado;
import usuarios.Mesero;
import usuarios.Usuario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConsolaAdministrador extends ConsolaBase {

    private static final String[] DIAS_SEMANA = {
        "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
    };

    private static final String[] OPCIONES = {
        "Agregar usuario",
        "Listar usuarios",
        "Agregar juego al inventario",
        "Gestionar inventario de juegos",
        "Agregar item al menú",
        "Construir / modificar turno de empleado",
        "Ver solicitudes de cambio de turno",
        "Aprobar / Rechazar solicitud de turno",
        "Ver sugerencias de menú",
        "Aprobar / Rechazar sugerencia",
        "Crear torneo",
        "Ver torneos",
        "Registrar ganador de torneo",
        "Ver reporte de ventas",
        "Salir"
    };

    public static void main(String[] args) {
        ConsolaAdministrador consola = new ConsolaAdministrador();
        consola.ejecutar();
        consola.guardarDatos();
    }

    @Override
    protected void ejecutar() {
        Administrador admin = (Administrador) autenticar("Administrador", Administrador.class);
        int opcion;
        do {
            mostrarMenu("Menú Administrador — " + admin.getNombre(), OPCIONES);
            opcion = leerEnteroEnRango("", 1, OPCIONES.length);
            procesarOpcion(opcion, admin);
        } while (opcion != OPCIONES.length);
    }

    private void procesarOpcion(int opcion, Administrador admin) {
        switch (opcion) {
            case 1:  agregarUsuario();           break;
            case 2:  listarUsuarios();           break;
            case 3:  agregarJuego();             break;
            case 4:  gestionarInventario();      break;
            case 5:  agregarItemMenu();          break;
            case 6:  gestionarTurno();           break;
            case 7:  verSolicitudesTurno();      break;
            case 8:  resolverSolicitudTurno();   break;
            case 9:  verSugerencias();           break;
            case 10: resolverSugerencia();       break;
            case 11: crearTorneo();              break;
            case 12: verTorneos();               break;
            case 13: registrarGanador();         break;
            case 14: verReporteVentas();         break;
            case 15: System.out.println("  Hasta luego, " + admin.getNombre() + "!"); break;
            default: System.out.println("  Opción no válida.");
        }
    }

    // ----- Usuarios -----------------------------------------------

    private void agregarUsuario() {
        String[] tipos = { "Cliente", "Mesero", "Cocinero", "Cancelar" };
        mostrarMenu("Tipo de usuario", tipos);
        int tipo = leerEnteroEnRango("", 1, tipos.length);
        if (tipo == tipos.length) { pausar(); return; }

        String id    = generarIdUsuario();
        String login;
        while (true) {
            login = leerTexto("  Login         : ");
            if (!loginExiste(login)) break;
            System.out.println("  El login '" + login + "' ya está en uso. Elija otro.");
        }
        String pass   = leerTexto("  Contraseña    : ");
        String nombre = leerTexto("  Nombre        : ");

        Usuario nuevo;
        if (tipo == 1) {
            String idCliente = generarIdCliente();
            nuevo = new Cliente(id, login, pass, nombre, idCliente);
        } else {
            String codigo;
            while (true) {
                codigo = leerTexto("  Código descuento: ");
                boolean existe = false;
                for (Usuario u : sistema.getUsuarios()) {
                    if (u instanceof Empleado && codigo.equals(((Empleado) u).getCodigoDescuento())) {
                        existe = true;
                        break;
                    }
                }
                if (!existe) break;
                System.out.println("  El código '" + codigo + "' ya está en uso. Elija otro.");
            }
            nuevo = (tipo == 2)
                    ? new Mesero(id, login, pass, nombre, codigo)
                    : new Cocinero(id, login, pass, nombre, codigo);
        }
        sistema.agregarUsuario(nuevo);
        System.out.println("  Usuario agregado: " + nombre + " (" + tipos[tipo - 1] + ")");
        pausar();
    }

    private void listarUsuarios() {
        List<Usuario> usuarios = sistema.getUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("  No hay usuarios registrados.");
            pausar();
            return;
        }
        System.out.println("\n  === Usuarios registrados ===");
        System.out.printf("  %-5s %-20s %-10s %-15s%n", "ID", "Nombre", "Tipo", "Login");
        System.out.println("  " + "-".repeat(55));
        for (Usuario u : usuarios) {
            String tipo = tipoDe(u);
            System.out.printf("  %-5s %-20s %-10s %-15s%n",
                    u.getIdUsuario(), u.getNombre(), tipo, u.getLogin());
        }
        pausar();
    }

    // ----- Juegos -------------------------------------------------

    private void agregarJuego() {
        String id;
        while (true) {
            id = leerTexto("  ID juego        : ");
            if (!idJuegoExiste(id)) break;
            System.out.println("  El ID '" + id + "' ya existe. Elija otro.");
        }
        String nombre   = leerTexto("  Nombre          : ");
        int anio        = leerEnteroEnRango("  Año publicación : ", 1900, 2100);
        String empresa  = leerTexto("  Empresa matriz  : ");
        int minJug      = leerEnteroEnRango("  Mín. jugadores  : ", 1, 20);
        int maxJug      = leerEnteroEnRango("  Máx. jugadores  : ", minJug, 20);
        int edadMin     = leerEnteroEnRango("  Edad mínima     : ", 0, 99);
        String categoria = leerTexto("  Categoría       : ");
        boolean dificil = leerSiNo("  ¿Es difícil?");

        JuegoDeMesa juego = new JuegoDeMesa(id, nombre, anio, empresa,
                minJug, maxJug, edadMin, categoria, dificil);

        System.out.println("\n  ¿A qué inventario agregar?");
        String[] destinos = { "Préstamo (crear copia)", "Venta (agregar stock)", "Ambos", "Cancelar" };
        mostrarMenu("Destino", destinos);
        int dest = leerEnteroEnRango("", 1, destinos.length);
        if (dest == destinos.length) { pausar(); return; }

        if (dest == 1 || dest == 3) {
            String idCopia;
            while (true) {
                idCopia = leerTexto("  ID copia        : ");
                if (!idCopiaExiste(idCopia)) break;
                System.out.println("  El ID de copia '" + idCopia + "' ya existe. Elija otro.");
            }
            CopiaJuego copia = new CopiaJuego(idCopia, juego, "Nuevo", true);
            sistema.getServicioInventario().reabastecerPrestamo(copia);
        }
        if (dest == 2 || dest == 3) {
            int cantidad = leerEnteroEnRango("  Cantidad en venta: ", 1, 999);
            double precio = leerDouble("  Precio unitario  : $");
            sistema.getServicioInventario().reabastecerVenta(juego, cantidad, precio);
        }
        System.out.println("  Juego agregado: " + nombre);
        pausar();
    }

    private void gestionarInventario() {
        String[] sub = {
            "Ver copias de un juego",
            "Mover unidad de venta a préstamo",
            "Reparar copia dañada",
            "Marcar copia como robada",
            "Reabastecer inventario de venta",
            "Volver"
        };
        int op;
        do {
            mostrarMenu("Gestión de inventario", sub);
            op = leerEnteroEnRango("", 1, sub.length);
            switch (op) {
                case 1: verCopias();            break;
                case 2: moverAPrestamoF();      break;
                case 3: repararCopiaF();        break;
                case 4: marcarRobadaF();        break;
                case 5: reabastecerVentaF();    break;
            }
        } while (op != sub.length);
    }

    private void verCopias() {
        List<JuegoDeMesa> juegos = obtenerTodosLosJuegos();
        if (juegos.isEmpty()) { System.out.println("  No hay juegos."); pausar(); return; }
        mostrarListaJuegos(juegos);
        int idx = leerEnteroEnRango("  Seleccione juego: ", 1, juegos.size()) - 1;
        JuegoDeMesa juego = juegos.get(idx);
        List<CopiaJuego> copias = sistema.getServicioInventario().getCopiasPorJuego(juego);
        if (copias.isEmpty()) { System.out.println("  Sin copias en préstamo."); pausar(); return; }
        System.out.println("\n  === Copias de " + juego.getNombre() + " ===");
        for (CopiaJuego c : copias) {
            System.out.printf("  ID: %-10s | Estado: %-12s | Disponible: %s%n",
                    c.getIdCopia(), c.getEstadoCopia(), c.isDisponible() ? "Sí" : "No");
        }
        pausar();
    }

    private void moverAPrestamoF() {
        List<JuegoDeMesa> juegos = juegosDentroDeVenta();
        if (juegos.isEmpty()) { System.out.println("  No hay juegos con stock en venta."); pausar(); return; }
        mostrarListaJuegos(juegos);
        int idx = leerEnteroEnRango("  Seleccione juego: ", 1, juegos.size()) - 1;
        String idCopia = leerTexto("  ID para la nueva copia: ");
        boolean ok = sistema.getServicioInventario().moverDeVentaAPrestamo(juegos.get(idx), idCopia);
        System.out.println(ok ? "  Copia movida al inventario de préstamo." : "  No hay stock disponible.");
        pausar();
    }

    private void repararCopiaF() {
        CopiaJuego copia = seleccionarCopia("Seleccione copia a reparar");
        if (copia == null) return;
        boolean ok = sistema.getServicioInventario().repararCopia(copia, copia.getJuego());
        System.out.println(ok ? "  Copia reparada." : "  Sin stock de venta para reponer.");
        pausar();
    }

    private void marcarRobadaF() {
        CopiaJuego copia = seleccionarCopia("Seleccione copia robada");
        if (copia == null) return;
        sistema.getServicioInventario().marcarComoRobada(copia);
        System.out.println("  Copia marcada como desaparecida.");
        pausar();
    }

    private void reabastecerVentaF() {
        List<JuegoDeMesa> todos = obtenerTodosLosJuegos();
        if (todos.isEmpty()) { System.out.println("  No hay juegos."); pausar(); return; }
        mostrarListaJuegos(todos);
        int idx      = leerEnteroEnRango("  Seleccione juego: ", 1, todos.size()) - 1;
        int cantidad = leerEnteroEnRango("  Cantidad a agregar: ", 1, 999);
        double precio = leerDouble("  Precio unitario: $");
        sistema.getServicioInventario().reabastecerVenta(todos.get(idx), cantidad, precio);
        System.out.println("  Inventario de venta actualizado.");
        pausar();
    }

    // ----- Menú -------------------------------------------------

    private void agregarItemMenu() {
        String[] tipos = { "Bebida", "Pastelería", "Cancelar" };
        mostrarMenu("Tipo de item", tipos);
        int tipo = leerEnteroEnRango("", 1, tipos.length);
        if (tipo == tipos.length) { pausar(); return; }

        String id;
        while (true) {
            id = leerTexto("  ID producto    : ");
            if (!idMenuExiste(id)) break;
            System.out.println("  El ID '" + id + "' ya existe en el menú. Elija otro.");
        }
        String nombre = leerTexto("  Nombre         : ");
        double precio = leerDouble("  Precio         : $");

        Alimento item;
        if (tipo == 1) {
            boolean alcoholica = leerSiNo("  ¿Es alcohólica?");
            boolean caliente   = leerSiNo("  ¿Es caliente?");
            item = new Bebida(id, nombre, precio, alcoholica, caliente);
        } else {
            System.out.print("  Alérgenos (separados por coma, Enter si ninguno): ");
            String input = scanner.nextLine().trim();
            List<String> alergenos = new ArrayList<>();
            if (!input.isEmpty()) {
                for (String a : input.split(",")) alergenos.add(a.trim());
            }
            item = new Pasteleria(id, nombre, precio, alergenos);
        }
        sistema.agregarAlimento(item);
        System.out.println("  Item agregado al menú: " + nombre);
        pausar();
    }

    // ----- Turnos -------------------------------------------------

    private void gestionarTurno() {
        String[] sub = { "Ver todos los turnos", "Crear turno", "Eliminar turno", "Volver" };
        int op;
        do {
            mostrarMenu("Gestión de turnos", sub);
            op = leerEnteroEnRango("", 1, sub.length);
            switch (op) {
                case 1: verTodosLosTurnos(); break;
                case 2: crearTurnoF();       break;
                case 3: eliminarTurnoF();    break;
            }
        } while (op != sub.length);
    }

    private void verTodosLosTurnos() {
        List<Turno> turnos = sistema.getServicioTurnos().getTurnos();
        if (turnos.isEmpty()) { System.out.println("  No hay turnos asignados."); pausar(); return; }
        System.out.println("\n  === Turnos actuales ===");
        for (Turno t : turnos) {
            System.out.printf("  ID: %-10s | Empleado: %-20s | Día: %s%n",
                    t.getIdTurno(), t.getEmpleado().getNombre(), t.getDiaSemana());
        }
        pausar();
    }

    private void crearTurnoF() {
        List<Empleado> sinTurno = empleadosSinTurno();
        if (sinTurno.isEmpty()) {
            System.out.println("  Todos los empleados ya tienen turno asignado.");
            pausar();
            return;
        }
        System.out.println("\n  === Empleados sin turno ===");
        for (int i = 0; i < sinTurno.size(); i++) {
            System.out.printf("  %d. %-20s (%s)%n",
                    i + 1, sinTurno.get(i).getNombre(), tipoDe(sinTurno.get(i)));
        }
        int idx = leerEnteroEnRango("  Seleccione empleado: ", 1, sinTurno.size()) - 1;
        Empleado emp = sinTurno.get(idx);

        System.out.println("\n  Días de la semana:");
        for (int i = 0; i < DIAS_SEMANA.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, DIAS_SEMANA[i]);
        }
        int diaIdx = leerEnteroEnRango("  Seleccione día: ", 1, DIAS_SEMANA.length) - 1;
        String dia = DIAS_SEMANA[diaIdx];

        String idTurno = "T-" + System.currentTimeMillis();
        Turno turno = sistema.getServicioTurnos().crearTurno(idTurno, emp, dia);
        emp.setEnTurno(true);
        System.out.println("  Turno creado para " + emp.getNombre() + " el " + dia + ".");
        pausar();
    }

    private void eliminarTurnoF() {
        List<Turno> turnos = sistema.getServicioTurnos().getTurnos();
        if (turnos.isEmpty()) { System.out.println("  No hay turnos para eliminar."); pausar(); return; }
        System.out.println("\n  === Turnos actuales ===");
        for (int i = 0; i < turnos.size(); i++) {
            Turno t = turnos.get(i);
            System.out.printf("  %d. %-20s — %s%n", i + 1, t.getEmpleado().getNombre(), t.getDiaSemana());
        }
        int idx = leerEnteroEnRango("  Seleccione turno a eliminar: ", 1, turnos.size()) - 1;
        Turno turno = turnos.get(idx);
        turno.getEmpleado().setEnTurno(false);
        sistema.getServicioTurnos().eliminarTurno(turno);
        System.out.println("  Turno eliminado.");
        pausar();
    }

    private void verSolicitudesTurno() {
        List<SolicitudCambioTurno> pendientes = sistema.getServicioTurnos().getSolicitudesPendientes();
        if (pendientes.isEmpty()) { System.out.println("  No hay solicitudes pendientes."); pausar(); return; }
        System.out.println("\n  === Solicitudes de cambio de turno ===");
        for (int i = 0; i < pendientes.size(); i++) {
            SolicitudCambioTurno s = pendientes.get(i);
            String destino = s.getEmpleadoDestino() != null ? s.getEmpleadoDestino().getNombre() : "—";
            System.out.printf("  %d. %-20s | Tipo: %-16s | Con: %-20s | Día afectado: %s%n",
                    i + 1,
                    s.getSolicitante().getNombre(),
                    s.getTipo(),
                    destino,
                    s.getTurnoAfectado() != null ? s.getTurnoAfectado().getDiaSemana() : "—");
        }
        pausar();
    }

    private void resolverSolicitudTurno() {
        List<SolicitudCambioTurno> pendientes = sistema.getServicioTurnos().getSolicitudesPendientes();
        if (pendientes.isEmpty()) { System.out.println("  No hay solicitudes pendientes."); pausar(); return; }
        System.out.println("\n  === Solicitudes pendientes ===");
        for (int i = 0; i < pendientes.size(); i++) {
            SolicitudCambioTurno s = pendientes.get(i);
            System.out.printf("  %d. %s — %s%n", i + 1, s.getSolicitante().getNombre(), s.getTipo());
        }
        int idx = leerEnteroEnRango("  Seleccione solicitud: ", 1, pendientes.size()) - 1;
        SolicitudCambioTurno solicitud = pendientes.get(idx);

        boolean aprobar = leerSiNo("  ¿Aprobar solicitud?");
        if (aprobar) {
            sistema.aprobarCambio(solicitud);
            System.out.println("  Solicitud aprobada.");
        } else {
            sistema.rechazarCambio(solicitud);
            System.out.println("  Solicitud rechazada.");
        }
        pausar();
    }

    // ----- Sugerencias -------------------------------------------------

    private void verSugerencias() {
        List<Sugerencia> sugerencias = sistema.getSugerencias();
        if (sugerencias.isEmpty()) { System.out.println("  No hay sugerencias registradas."); pausar(); return; }
        System.out.println("\n  === Sugerencias de menú ===");
        for (int i = 0; i < sugerencias.size(); i++) {
            Sugerencia s = sugerencias.get(i);
            String empleado = s.getEmpleado() != null ? s.getEmpleado().getNombre() : "—";
            System.out.printf("  %d. %-25s | Categoría: %-15s | Por: %-20s | Estado: %s%n",
                    i + 1, s.getNombrePropuesto(), s.getCategoria(), empleado, s.getEstado());
        }
        pausar();
    }

    private void resolverSugerencia() {
        List<Sugerencia> pendientes = new ArrayList<>();
        for (Sugerencia s : sistema.getSugerencias()) {
            if (s.getEstado() == Sugerencia.EstadoSugerencia.PENDIENTE) pendientes.add(s);
        }
        if (pendientes.isEmpty()) { System.out.println("  No hay sugerencias pendientes."); pausar(); return; }
        System.out.println("\n  === Sugerencias pendientes ===");
        for (int i = 0; i < pendientes.size(); i++) {
            System.out.printf("  %d. %-25s (%s)%n",
                    i + 1, pendientes.get(i).getNombrePropuesto(), pendientes.get(i).getCategoria());
        }
        int idx = leerEnteroEnRango("  Seleccione sugerencia: ", 1, pendientes.size()) - 1;
        Sugerencia sugerencia = pendientes.get(idx);

        boolean aprobar = leerSiNo("  ¿Aprobar sugerencia?");
        if (aprobar) {
            sistema.aprobarSugerencia(sugerencia);
            System.out.println("  Sugerencia aprobada.");
            if (leerSiNo("  ¿Agregar este item al menú ahora?")) {
                agregarItemMenuDesdeSugerencia(sugerencia);
            }
        } else {
            sugerencia.rechazar();
            System.out.println("  Sugerencia rechazada.");
        }
        pausar();
    }

    private void agregarItemMenuDesdeSugerencia(Sugerencia sugerencia) {
        String id;
        while (true) {
            id = leerTexto("  ID producto    : ");
            if (!idMenuExiste(id)) break;
            System.out.println("  El ID '" + id + "' ya existe en el menú. Elija otro.");
        }
        double precio = leerDouble("  Precio         : $");

        String[] tipos = { "Bebida", "Pastelería" };
        mostrarMenu("Tipo de item", tipos);
        int tipo = leerEnteroEnRango("", 1, tipos.length);

        Alimento item;
        if (tipo == 1) {
            boolean alcoholica = leerSiNo("  ¿Es alcohólica?");
            boolean caliente   = leerSiNo("  ¿Es caliente?");
            item = new Bebida(id, sugerencia.getNombrePropuesto(), precio, alcoholica, caliente);
        } else {
            System.out.print("  Alérgenos (separados por coma, Enter si ninguno): ");
            String input = scanner.nextLine().trim();
            List<String> alergenos = new ArrayList<>();
            if (!input.isEmpty()) {
                for (String a : input.split(",")) alergenos.add(a.trim());
            }
            item = new Pasteleria(id, sugerencia.getNombrePropuesto(), precio, alergenos);
        }
        sistema.agregarAlimento(item);
        System.out.println("  Item agregado al menú: " + sugerencia.getNombrePropuesto());
    }

    // ----- Torneos -------------------------------------------------

    private void crearTorneo() {
        String[] tipos = { "Amistoso", "Competitivo", "Cancelar" };
        mostrarMenu("Tipo de torneo", tipos);
        int tipo = leerEnteroEnRango("", 1, tipos.length);
        if (tipo == tipos.length) { pausar(); return; }

        String id;
        while (true) {
            id = leerTexto("  ID torneo  : ");
            if (!idTorneoExiste(id)) break;
            System.out.println("  El ID '" + id + "' ya existe. Elija otro.");
        }
        String nombre = leerTexto("  Nombre     : ");

        List<JuegoDeMesa> juegos = obtenerTodosLosJuegos();
        if (juegos.isEmpty()) { System.out.println("  No hay juegos registrados."); pausar(); return; }
        mostrarListaJuegos(juegos);
        int idxJuego = leerEnteroEnRango("  Juego del torneo: ", 1, juegos.size()) - 1;
        JuegoDeMesa juego = juegos.get(idxJuego);

        System.out.println("\n  Días de la semana:");
        for (int i = 0; i < DIAS_SEMANA.length; i++) System.out.printf("  %d. %s%n", i + 1, DIAS_SEMANA[i]);
        int diaIdx = leerEnteroEnRango("  Seleccione día: ", 1, DIAS_SEMANA.length) - 1;
        String dia = DIAS_SEMANA[diaIdx];

        int cupoMax = leerEnteroEnRango("  Cupo máximo: ", 1, 999);

        torneos torneo;
        if (tipo == 1) {
            double bono = leerDouble("  Porcentaje del bono (ej: 0.15 para 15%): ");
            torneo = sistema.crearTorneoAmistoso(id, nombre, juego, dia, cupoMax, bono);
        } else {
            double tarifa = leerDouble("  Tarifa de entrada por participante: $");
            torneo = sistema.crearTorneoCompetitivo(id, nombre, juego, dia, cupoMax, tarifa);
        }
        if (torneo != null) System.out.println("  Torneo creado: " + nombre);
        pausar();
    }

    private void verTorneos() {
        List<torneos> lista = sistema.getTorneos();
        if (lista.isEmpty()) { System.out.println("  No hay torneos registrados."); pausar(); return; }
        System.out.println("\n  === Torneos ===");
        for (int i = 0; i < lista.size(); i++) {
            torneos t = lista.get(i);
            String extra = t.esAmistoso()
                    ? "Bono: " + ((torneoAmistoso) t).getBonoPremio() * 100 + "%"
                    : "Tarifa: $" + ((torneoCompetitivo) t).getTarifaEntrada();
            System.out.printf("  %2d. [%-11s] %-20s | Juego: %-18s | Día: %-10s | Cupos: %d/%d | %s%n",
                    i + 1, t.getTipo(), t.getNombre(), t.getJuego().getNombre(),
                    t.getDiaSemana(), t.cuposOcupados(), t.getCupoMaximo(), extra);
        }
        pausar();
    }

    private void registrarGanador() {
        List<torneos> lista = sistema.getTorneos();
        if (lista.isEmpty()) { System.out.println("  No hay torneos."); pausar(); return; }
        mostrarMenu("Seleccione torneo", nombresTorneos(lista));
        int idx = leerEnteroEnRango("", 1, lista.size()) - 1;
        torneos torneo = lista.get(idx);

        List<Usuario> inscritos = inscritosEnTorneo(torneo);
        if (inscritos.isEmpty()) { System.out.println("  Nadie inscrito en este torneo."); pausar(); return; }

        System.out.println("\n  === Participantes inscritos ===");
        for (int i = 0; i < inscritos.size(); i++) {
            System.out.printf("  %d. %s (%s)%n", i + 1, inscritos.get(i).getNombre(), tipoDe(inscritos.get(i)));
        }
        int idxGanador = leerEnteroEnRango("  Seleccione ganador: ", 1, inscritos.size()) - 1;
        sistema.registrarGanadorTorneo(torneo, inscritos.get(idxGanador));
        pausar();
    }

    // ----- Reportes -------------------------------------------------

    private void verReporteVentas() {
        String[] sub = { "Por tipo y rango de fechas", "Por semana", "Por mes", "Volver" };
        int op;
        do {
            mostrarMenu("Reporte de ventas", sub);
            op = leerEnteroEnRango("", 1, sub.length);
            switch (op) {
                case 1: reportePorRango();  break;
                case 2: reportePorSemana(); break;
                case 3: reportePorMes();    break;
            }
        } while (op != sub.length);
    }

    private void reportePorRango() {
        String[] tipos = { "Tienda (juegos)", "Cafetería" };
        mostrarMenu("Tipo de venta", tipos);
        int tipoIdx = leerEnteroEnRango("", 1, tipos.length);
        Venta.TipoVenta tipo = tipoIdx == 1 ? Venta.TipoVenta.JUEGO : Venta.TipoVenta.CAFETERIA;

        Date desde = leerFecha("  Fecha inicio (dd/MM/yyyy): ");
        Date hasta = leerFecha("  Fecha fin    (dd/MM/yyyy): ");

        List<Venta> ventas = sistema.generarInforme(tipo, desde, hasta);
        imprimirReporte(ventas);
    }

    private void reportePorSemana() {
        int semana = leerEnteroEnRango("  Número de semana (1-52): ", 1, 52);
        ServicioReportes rep = sistema.getServicioReportes();
        List<Venta> ventas = rep.ventasPorSemana(semana);
        imprimirReporte(ventas);
    }

    private void reportePorMes() {
        int mes = leerEnteroEnRango("  Mes (1-12): ", 1, 12);
        ServicioReportes rep = sistema.getServicioReportes();
        List<Venta> ventas = rep.ventasPorMes(mes - 1);
        imprimirReporte(ventas);
    }

    private void imprimirReporte(List<Venta> ventas) {
        if (ventas.isEmpty()) { System.out.println("  Sin ventas en ese período."); pausar(); return; }
        ServicioReportes rep = sistema.getServicioReportes();
        System.out.println("\n  === Reporte de ventas ===");
        System.out.printf("  %-12s %-20s %-12s %-12s %-12s %-12s%n",
                "ID", "Comprador", "Subtotal", "Impuesto", "Propina", "Total");
        System.out.println("  " + "-".repeat(84));
        for (Venta v : ventas) {
            System.out.printf("  %-12s %-20s $%-11.2f $%-11.2f $%-11.2f $%-11.2f%n",
                    v.getIdVenta(), v.getComprador().getNombre(),
                    v.getSubtotal(), v.getImpuesto(), v.getPropina(), v.getTotal());
        }
        System.out.println("  " + "-".repeat(84));
        System.out.printf("  TOTAL: subtotal $%.2f | impuestos $%.2f | propinas $%.2f | total $%.2f%n",
                rep.getTotalSubtotal(ventas),
                rep.getTotalImpuestos(ventas),
                rep.getTotalPropinas(ventas),
                rep.getTotalSubtotal(ventas) + rep.getTotalImpuestos(ventas) + rep.getTotalPropinas(ventas));
        pausar();
    }

    // ----- Helpers -------------------------------------------------

    private List<JuegoDeMesa> juegosDentroDeVenta() {
        List<JuegoDeMesa> lista = new ArrayList<>();
        InventarioVenta inv = sistema.getServicioInventario().getInventarioVenta();
        for (JuegoDeMesa j : inv.getJuegos()) {
            if (inv.hayStock(j)) lista.add(j);
        }
        return lista;
    }

    private CopiaJuego seleccionarCopia(String titulo) {
        List<JuegoDeMesa> juegos = obtenerTodosLosJuegos();
        if (juegos.isEmpty()) { System.out.println("  No hay juegos."); pausar(); return null; }
        mostrarListaJuegos(juegos);
        int idx = leerEnteroEnRango("  Seleccione juego: ", 1, juegos.size()) - 1;
        List<CopiaJuego> copias = sistema.getServicioInventario().getCopiasPorJuego(juegos.get(idx));
        if (copias.isEmpty()) { System.out.println("  Sin copias."); pausar(); return null; }
        System.out.println("\n  === " + titulo + " ===");
        for (int i = 0; i < copias.size(); i++) {
            CopiaJuego c = copias.get(i);
            System.out.printf("  %d. ID: %-10s | Estado: %-12s | Disponible: %s%n",
                    i + 1, c.getIdCopia(), c.getEstadoCopia(), c.isDisponible() ? "Sí" : "No");
        }
        int idxCopia = leerEnteroEnRango("  Seleccione copia: ", 1, copias.size()) - 1;
        return copias.get(idxCopia);
    }

    private List<Empleado> empleadosSinTurno() {
        List<Empleado> lista = new ArrayList<>();
        for (Usuario u : sistema.getUsuarios()) {
            if (u instanceof Empleado && ((Empleado) u).getTurno() == null) lista.add((Empleado) u);
        }
        return lista;
    }

    private List<Usuario> inscritosEnTorneo(torneos torneo) {
        List<Usuario> lista = new ArrayList<>();
        for (Torneos.Inscripcion ins : torneo.getInscripciones()) {
            lista.add(ins.getUsuario());
        }
        return lista;
    }

    private String[] nombresTorneos(List<torneos> lista) {
        String[] nombres = new String[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            nombres[i] = "[" + lista.get(i).getTipo() + "] " + lista.get(i).getNombre();
        }
        return nombres;
    }

}
