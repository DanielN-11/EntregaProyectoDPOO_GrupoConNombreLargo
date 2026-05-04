package persistencia;

import Cafeteria.Alimento;
import Cafeteria.Mesa;
import Cafeteria.SolicitudCambioTurno;
import Cafeteria.Turno;
import Servicios.ServicioCafeteria;
import Servicios.ServicioPrestamo;
import Servicios.ServicioTurnos;
import Servicios.ServicioVentas;
import Servicios.Sugerencia;
import inventario.InventarioPrestamo;
import inventario.InventarioVenta;
import juegos.CopiaJuego;
import juegos.JuegoDeMesa;
import sistema.SistemaCafe;
import transacciones.Prestamo;
import transacciones.Venta;
import usuarios.Empleado;
import usuarios.Mesero;
import usuarios.Usuario;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Coordinador principal de persistencia.
 *
 * Se encarga de orquestar el guardado y la carga completa del sistema,
 * respetando el orden de dependencias entre entidades.
 *
 * Archivos generados en la carpeta 'datos/' (fuera de src):
 *   cafe.txt, mesas.txt, juegos.txt, copias_prestamo.txt,
 *   inventario_venta.txt, usuarios.txt, menu.txt,
 *   turnos.txt, solicitudes.txt, prestamos.txt, ventas.txt
 *
 * Sugerencias se guardan en: sugerencias.txt
 */
public class PersistenciaCafe {

    private static final String SEP    = "|";
    private static final String ARCH_CAFE       = "cafe.txt";
    private static final String ARCH_MESAS      = "mesas.txt";
    private static final String ARCH_SUGERENCIAS = "sugerencias.txt";

    private final String rutaDatos;

    private final PersistenciaJuegos    persJuegos;
    private final PersistenciaCopias    persCopias;
    private final PersistenciaUsuarios  persUsuarios;
    private final PersistenciaMenu      persMenu;
    private final PersistenciaTurnos    persTurnos;
    private final PersistenciaPrestamos persPrestamos;
    private final PersistenciaVentas    persVentas;

    /**
     * @param rutaDatos Ruta a la carpeta de datos, p.ej. "datos/"
     *                  La carpeta se crea automáticamente si no existe.
     */
    public PersistenciaCafe(String rutaDatos) {
        // Asegurarse de que la ruta termina en separador
        this.rutaDatos = rutaDatos.endsWith(File.separator) || rutaDatos.endsWith("/")
                ? rutaDatos : rutaDatos + "/";

        this.persJuegos    = new PersistenciaJuegos(this.rutaDatos);
        this.persCopias    = new PersistenciaCopias(this.rutaDatos);
        this.persUsuarios  = new PersistenciaUsuarios(this.rutaDatos);
        this.persMenu      = new PersistenciaMenu(this.rutaDatos);
        this.persTurnos    = new PersistenciaTurnos(this.rutaDatos);
        this.persPrestamos = new PersistenciaPrestamos(this.rutaDatos);
        this.persVentas    = new PersistenciaVentas(this.rutaDatos);
    }

    /**
     * Lee la capacidadMaxima guardada sin necesitar un SistemaCafe.
     * Usar antes de construir el sistema:
     * <pre>
     *   int cap = PeristenciaCafe.leerCapacidadMaxima("datos/");
     *   SistemaCafe sistema = new SistemaCafe(cap);
     *   new PeristenciaCafe("datos/").cargar(sistema);
     * </pre>
     *
     * @return la capacidad guardada, o 50 si no existe el archivo.
     */
    public static int leerCapacidadMaxima(String rutaDatos) {
        String ruta = rutaDatos.endsWith("/") || rutaDatos.endsWith(File.separator)
                ? rutaDatos : rutaDatos + "/";
        File archivo = new File(ruta + ARCH_CAFE);
        if (!archivo.exists()) return 50; // valor por defecto

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("capacidadMaxima=")) {
                    return Integer.parseInt(linea.split("=")[1].trim());
                }
            }
        } catch (IOException | NumberFormatException e) {
            // ignorar, retornar default
        }
        return 50;
    }

    // ══════════════════════════════════════════════════════════
    //  GUARDAR
    // ══════════════════════════════════════════════════════════

    /**
     * Guarda el estado completo del sistema en disco.
     */
    public void guardar(SistemaCafe sistema) throws IOException {
        guardarConfigCafe(sistema);
        guardarMesas(sistema.getMesas());
        persJuegos.guardar(obtenerTodosLosJuegos(sistema));
        persCopias.guardarCopiasPrestamo(sistema.getServicioInventario().getInventarioPrestamo());
        persCopias.guardarInventarioVenta(sistema.getServicioInventario().getInventarioVenta());
        persUsuarios.guardar(sistema.getUsuarios());
        persMenu.guardar(sistema.getServicioCafeteria().getMenu());
        persTurnos.guardarTurnos(sistema.getServicioTurnos().getTurnos());
        persTurnos.guardarSolicitudes(sistema.getServicioTurnos().getSolicitudesPendientes());
        persPrestamos.guardar(sistema.getServicioPrestamo().getHistorialPrestamos());
        // Ventas: combinar tienda + cafetería
        List<Venta> todasVentas = new ArrayList<>(sistema.getVentas());
        persVentas.guardar(todasVentas);
        guardarSugerencias(sistema.getSugerencias());

        System.out.println("[Persistencia] Estado guardado correctamente en: " + rutaDatos);
    }

    // ══════════════════════════════════════════════════════════
    //  CARGAR
    // ══════════════════════════════════════════════════════════

    /**
     * Carga el estado completo desde disco y lo inyecta en el sistema.
     * El sistema debe estar recién creado (vacío) antes de llamar a este método.
     *
     * @return true si se cargaron datos; false si no existía ningún archivo de datos.
     */
    public boolean cargar(SistemaCafe sistema) throws IOException {
        File archCafe = new File(rutaDatos + ARCH_CAFE);
        if (!archCafe.exists()) {
            System.out.println("[Persistencia] No se encontraron datos previos. Iniciando desde cero.");
            return false;
        }

        // 1. Juegos (sin dependencias)
        List<JuegoDeMesa> juegos = persJuegos.cargar();
        List<Alimento> menu = persMenu.cargar();

        // 2. Copias (dependen de juegos)
        InventarioPrestamo invPrestamo = sistema.getServicioInventario().getInventarioPrestamo();
        InventarioVenta    invVenta    = sistema.getServicioInventario().getInventarioVenta();
        persCopias.cargarCopiasPrestamo(invPrestamo, juegos);
        persCopias.cargarInventarioVenta(invVenta, juegos);
        List<CopiaJuego> copias = invPrestamo.getCopias();

        // 3. Usuarios (dependen de juegos para favoritos)
        List<Usuario> usuarios = persUsuarios.cargar(juegos);
        for (Usuario u : usuarios) {
            sistema.agregarUsuario(u);
        }

        // 4. Menú
        ServicioCafeteria sc = sistema.getServicioCafeteria();
        for (Alimento a : menu) {
            sc.agregarAlMenu(a);
        }

        // 5. Mesas
        List<Mesa> mesas = cargarMesas();
        for (Mesa m : mesas) {
            sistema.agregarMesa(m);
        }

        // 6. Turnos (dependen de usuarios/empleados)
        List<Turno> turnos = persTurnos.cargarTurnos(usuarios);
        ServicioTurnos st = sistema.getServicioTurnos();
        for (Turno t : turnos) {
            st.getTurnos().add(t);
        }
        List<SolicitudCambioTurno> solicitudes = persTurnos.cargarSolicitudes(usuarios, turnos);
        for (SolicitudCambioTurno s : solicitudes) {
            // Solo re-agregar las pendientes al servicio
            if (SolicitudCambioTurno.PENDIENTE.equals(s.getEstado())) {
                st.getSolicitudesPendientes().add(s);
            }
        }

        // 7. Préstamos (dependen de copias, usuarios, mesas)
        List<Prestamo> prestamos = persPrestamos.cargar(copias, usuarios, mesas);
        ServicioPrestamo sp = sistema.getServicioPrestamo();
        for (Prestamo p : prestamos) {
            sp.getHistorialPrestamos().add(p);
        }

        // 8. Ventas (dependen de usuarios, juegos, menú)
        List<Venta> ventas = persVentas.cargar(usuarios, juegos, menu);
        for (Venta v : ventas) {
            // Lista global del sistema
            sistema.getVentas().add(v);
            // Historial interno de cada servicio (usado por ServicioReportes)
            if (v.getTipo() == Venta.TipoVenta.JUEGO) {
                sistema.getServicioVentas().getHistorialVentas().add(v);
            } else {
                sistema.getServicioCafeteria().getHistorialVentas().add(v);
            }
            // Historial de compras del cliente
            if (v.getComprador() instanceof usuarios.Cliente) {
                ((usuarios.Cliente) v.getComprador()).agregarCompra(v);
            }
        }

        // 9. Sugerencias
        List<Sugerencia> sugerencias = cargarSugerencias(usuarios);
        for (Sugerencia s : sugerencias) {
            sistema.agregarSugerencia(s);
        }

        System.out.println("[Persistencia] Datos cargados correctamente desde: " + rutaDatos);
        System.out.println("  Juegos: " + juegos.size() + " | Usuarios: " + usuarios.size()
                + " | Mesas: " + mesas.size() + " | Préstamos: " + prestamos.size()
                + " | Ventas: " + ventas.size());
        return true;
    }

    // ══════════════════════════════════════════════════════════
    //  Helpers privados
    // ══════════════════════════════════════════════════════════

    private void guardarConfigCafe(SistemaCafe sistema) throws IOException {
        File dir = new File(rutaDatos);
        if (!dir.exists()) dir.mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDatos + ARCH_CAFE))) {
            pw.println("version=1");
            pw.println("capacidadMaxima=" + sistema.getCapacidadMaxima());
        }
    }

    private void guardarMesas(List<Mesa> mesas) throws IOException {
        File dir = new File(rutaDatos);
        if (!dir.exists()) dir.mkdirs();

        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDatos + ARCH_MESAS))) {
            for (Mesa m : mesas) {
                // Guardamos solo el id de la mesa; el estado de ocupación
                // se reconstituye a partir de los préstamos activos.
                pw.println(m.getIdMesa());
            }
        }
    }

    private List<Mesa> cargarMesas() throws IOException {
        List<Mesa> mesas = new ArrayList<>();
        File archivo = new File(rutaDatos + ARCH_MESAS);
        if (!archivo.exists()) return mesas;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                // Las mesas se crean desocupadas; los préstamos activos se wired en paso 7
                mesas.add(new Mesa(linea, false, 0, 0, 0, new ArrayList<>(), null));
            }
        }
        return mesas;
    }

    private List<JuegoDeMesa> obtenerTodosLosJuegos(SistemaCafe sistema) {
        // Unir juegos del inventario de préstamo + inventario de venta (sin duplicados)
        List<JuegoDeMesa> todos = new ArrayList<>();
        for (CopiaJuego c : sistema.getServicioInventario().getInventarioPrestamo().getCopias()) {
            JuegoDeMesa j = c.getJuego();
            if (!todos.contains(j)) todos.add(j);
        }
        for (JuegoDeMesa j : sistema.getServicioInventario().getInventarioVenta().getJuegos()) {
            if (!todos.contains(j)) todos.add(j);
        }
        return todos;
    }

    private void guardarSugerencias(List<Sugerencia> sugerencias) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDatos + ARCH_SUGERENCIAS))) {
            for (Sugerencia s : sugerencias) {
                String idEmp = s.getEmpleado() != null ? s.getEmpleado().getIdUsuario() : "null";
                pw.println(s.getIdSugerencia() + SEP + idEmp + SEP
                        + s.getNombrePropuesto() + SEP + s.getCategoria() + SEP
                        + s.getEstado().name());
            }
        }
    }

    private List<Sugerencia> cargarSugerencias(List<Usuario> usuarios) throws IOException {
        List<Sugerencia> lista = new ArrayList<>();
        File archivo = new File(rutaDatos + ARCH_SUGERENCIAS);
        if (!archivo.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] p = linea.split("\\|", -1);
                if (p.length < 5) continue;

                Empleado emp = null;
                if (!"null".equals(p[1])) {
                    for (Usuario u : usuarios) {
                        if (u.getIdUsuario().equals(p[1]) && u instanceof Empleado) {
                            emp = (Empleado) u;
                            break;
                        }
                    }
                }
                Sugerencia s = new Sugerencia(p[0], emp, p[2], p[3]);
                String estado = p[4];
                if ("APROBADA".equals(estado))  s.aprobar();
                if ("RECHAZADA".equals(estado)) s.rechazar();
                lista.add(s);
            }
        }
        return lista;
    }
}
