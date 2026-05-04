package sistema;

import Cafeteria.Alimento;
import Torneos.Inscripcion;
import Cafeteria.Mesa;
import Cafeteria.SolicitudCambioTurno;
import Cafeteria.Turno;
import inventario.InventarioPrestamo;
import inventario.InventarioVenta;
import juegos.JuegoDeMesa;
import transacciones.ItemVenta;
import transacciones.Pedido;
import transacciones.Prestamo;
import transacciones.Venta;
import Servicios.ServicioCafeteria;
import Servicios.ServicioInventario;
import Servicios.ServicioPrestamo;
import Servicios.ServicioReportes;
import Servicios.ServicioTurnos;
import Servicios.ServicioVentas;
import Servicios.Sugerencia;
import usuarios.Administrador;
import usuarios.Cliente;
import usuarios.Empleado;
import usuarios.Mesero;
import usuarios.Usuario;

import Torneos.torneos;
import Servicios.ServicioTorneo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SistemaCafe {

    private List<Usuario> usuarios;
    private List<Mesa> mesas;
    private InventarioPrestamo inventarioPrestamo;
    private InventarioVenta inventarioVenta;
    private List<Turno> turnos;
    private List<Venta> ventas;
    private List<Sugerencia> sugerencias;

    private ServicioPrestamo servicioPrestamo;
    private ServicioVentas servicioVentas;
    private ServicioInventario servicioInventario;
    private ServicioTurnos servicioTurnos;
    private ServicioCafeteria servicioCafeteria;
    private ServicioReportes servicioReportes;
    private ServicioTorneo servicioTorneo;

    private int capacidadMaxima;

    public SistemaCafe(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
        this.usuarios = new ArrayList<>();
        this.mesas = new ArrayList<>();
        this.turnos = new ArrayList<>();
        this.ventas = new ArrayList<>();
        this.sugerencias = new ArrayList<>();

        this.inventarioPrestamo = new InventarioPrestamo();
        this.inventarioVenta = new InventarioVenta();

        // Extraer meseros para ServicioPrestamo
        List<Mesero> meseros = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u instanceof Mesero) meseros.add((Mesero) u);
        }

        // Extraer empleados para ServicioTurnos
        List<Empleado> empleados = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u instanceof Empleado) empleados.add((Empleado) u);
        }

        this.servicioPrestamo = new ServicioPrestamo(inventarioPrestamo, meseros);
        this.servicioVentas = new ServicioVentas(inventarioVenta);
        this.servicioInventario = new ServicioInventario(inventarioPrestamo, inventarioVenta);
        this.servicioTurnos = new ServicioTurnos(empleados);
        this.servicioCafeteria = new ServicioCafeteria(new ArrayList<>());
        this.servicioReportes = new ServicioReportes(
            servicioVentas.getHistorialVentas(),
            servicioCafeteria.getHistorialVentas()
        );
        this.servicioTorneo = new ServicioTorneo(inventarioPrestamo);
    }

    // ── Autenticación ──────────────────────────────────────────

    public Usuario login(String login, String password) {
        for (Usuario u : usuarios) {
            if (u.autenticar(login, password)) {
                return u;
            }
        }
        return null;
    }

    // ── Mesas ──────────────────────────────────────────────────

    public Mesa registrarMesa(int numPersonas, int numNinos, int numJovenes, Cliente cliente) {
        // Validar capacidad máxima
        int personasActuales = 0;
        for (Mesa m : mesas) {
            if (m.isOcupada()) {
                personasActuales += m.getNumeroPersonas();
            }
        }
        if (personasActuales + numPersonas > capacidadMaxima) {
            System.out.println("RECHAZADO: El café no tiene capacidad para más personas.");
            return null;
        }
        // Buscar mesa libre
        for (Mesa m : mesas) {
            if (!m.isOcupada()) {
                m.ocupar(numPersonas, numNinos, numJovenes);
                cliente.setMesaActual(m);
                return m;
            }
        }
        System.out.println("RECHAZADO: No hay mesas disponibles.");
        return null;
    }

    // ── Préstamos ──────────────────────────────────────────────

    public Prestamo prestarJuego(Usuario usuario, JuegoDeMesa juego, Mesa mesa) {
        // Administrador no puede pedir prestado
        if (usuario instanceof Administrador) {
            System.out.println("RECHAZADO: El administrador no puede pedir juegos prestados.");
            return null;
        }
        String idPrestamo = "P-" + System.currentTimeMillis();
        Prestamo prestamo = servicioPrestamo.prestarJuego(idPrestamo, usuario, juego, mesa, mesas);
        return prestamo;
    }

    public void devolverJuego(Prestamo prestamo) {
        servicioPrestamo.devolverJuego(prestamo);
    }

    // ── Ventas tienda ──────────────────────────────────────────

    public Venta realizarVentaTienda(Usuario comprador, List<ItemVenta> items) {
        return realizarVentaTienda(comprador, items, null);
    }

    public Venta realizarVentaTienda(Usuario comprador, List<ItemVenta> items, String codigoDescuento) {
        if (comprador instanceof Administrador) {
            System.out.println("RECHAZADO: El administrador no puede comprar.");
            return null;
        }
        String idVenta = "VT-" + System.currentTimeMillis();
        servicioVentas.crearVenta(idVenta, comprador, Venta.TipoVenta.JUEGO);
        for (ItemVenta item : items) {
            servicioVentas.agregarItem(idVenta, item);
        }
        String codigo = comprador instanceof Empleado
            ? ((Empleado) comprador).getCodigoDescuento() : codigoDescuento;
        servicioVentas.aplicarDescuento(idVenta, codigo, comprador);
        Venta venta = servicioVentas.finalizarVenta(idVenta, comprador, Venta.TipoVenta.JUEGO);
        if (venta != null) ventas.add(venta);
        return venta;
    }

    // ── Ventas cafetería ───────────────────────────────────────

    public Venta realizarVentaCafeteria(Usuario comprador, Mesa mesa, List<ItemVenta> items) {
        return realizarVentaCafeteria(comprador, mesa, items, null, 0.10);
    }

    public Venta realizarVentaCafeteria(Usuario comprador, Mesa mesa, List<ItemVenta> items, String codigoDescuento) {
        return realizarVentaCafeteria(comprador, mesa, items, codigoDescuento, 0.10);
    }

    public Venta realizarVentaCafeteria(Usuario comprador, Mesa mesa, List<ItemVenta> items, String codigoDescuento, double propina) {
        if (comprador instanceof Administrador) {
            System.out.println("RECHAZADO: El administrador no puede ordenar platillos.");
            return null;
        }
        String idPedido = "PED-" + System.currentTimeMillis();
        Pedido pedido = servicioCafeteria.crearPedido(idPedido, mesa);
        pedido.setPropina(propina);
        for (ItemVenta item : items) {
            if (item.getAlimento() != null) {
                servicioCafeteria.agregarAlPedido(pedido, item.getAlimento(), item.getCantidad());
            }
        }
        String codigo = comprador instanceof Empleado
            ? ((Empleado) comprador).getCodigoDescuento() : codigoDescuento;
        String idVenta = "VC-" + System.currentTimeMillis();
        Venta venta = servicioCafeteria.finalizarPedido(idVenta, pedido, comprador, codigo);
        if (venta != null) ventas.add(venta);
        return venta;
    }

    // ── Turnos ─────────────────────────────────────────────────

    public void solicitarCambioTurno(SolicitudCambioTurno solicitud) {
        servicioTurnos.solicitarCambio(solicitud);
    }

    public void aprobarCambio(SolicitudCambioTurno solicitud) {
        servicioTurnos.aprobarSolicitud(solicitud);
    }

    // ── Menú y sugerencias ─────────────────────────────────────

    public void agregarAlimento(Alimento alimento) {
        servicioCafeteria.agregarAlMenu(alimento);
    }

    public void aprobarSugerencia(Sugerencia sugerencia) {
        sugerencia.aprobar();
        // El administrador decide si además agrega el alimento al menú
    }

    // ── Reportes ───────────────────────────────────────────────

    public List<Venta> generarInforme(Venta.TipoVenta tipo, Date desde, Date hasta) {
        return servicioReportes.generarInformeVentas(tipo, desde, hasta);
    }

    // ── Registro de usuarios y mesas ───────────────────────────

    public void agregarUsuario(Usuario usuario) {
        usuarios.add(usuario);
        if (usuario instanceof Empleado) {
            servicioTurnos.getEmpleados().add((Empleado) usuario);
        }
        if (usuario instanceof Mesero) {
            servicioPrestamo.getMeseros().add((Mesero) usuario);
        }
    }

    public void agregarMesa(Mesa mesa) {
        mesas.add(mesa);
    }
    
    public void liberarMesa(Cliente cliente) {
        Mesa mesa = cliente.getMesaActual();
        if (mesa == null) {
            System.out.println("El cliente no tiene una mesa asignada.");
            return;
        }
        mesa.liberar();
        cliente.setMesaActual(null);
    }
    
    public void agregarSugerencia(Sugerencia sugerencia) {
        sugerencias.add(sugerencia);
    }
    
    public void rechazarCambio(SolicitudCambioTurno solicitud) {
        servicioTurnos.rechazarSolicitud(solicitud);
    }
    
    
    
    //REQUERIMIENTO - NUEVOS METODOS
    
    //Todo esto, la logica de ello esta en servicio torneos, esto es mas que todo para llamar a consola
    public torneos crearTorneoAmistoso(String idTorneo, String nombre, JuegoDeMesa juego, String diaSemana, int cupoMaximo, double bonoPremio) {
		return servicioTorneo.crearTorneoAmistoso(idTorneo, nombre, juego, diaSemana, cupoMaximo, bonoPremio);
		}
	
	public torneos crearTorneoCompetitivo(String idTorneo, String nombre, JuegoDeMesa juego, String diaSemana, int cupoMaximo, double tarifaEntrada) {
		return servicioTorneo.crearTorneoCompetitivo( idTorneo, nombre, juego, diaSemana, cupoMaximo, tarifaEntrada);
		}
	
	public boolean inscribirseATorneo(torneos torneo, Usuario usuario, int numParticipantes) {
		return servicioTorneo.inscribirse(torneo, usuario, numParticipantes);
		}
	
	public boolean desinscribirseATorneo(torneos torneo, Usuario usuario) {
		return servicioTorneo.desinscribirse(torneo, usuario);
		}
	
	public Inscripcion registrarGanadorTorneo(torneos torneo, Usuario ganador) {
	    return servicioTorneo.registrarGanador(torneo, ganador);
	}
	
	//getters 
	public List<torneos> getTorneos() {
		return servicioTorneo.getTorneos();
		}
		
	public ServicioTorneo getServicioTorneo() {
		return servicioTorneo;
		}
	
	

    
    
    
    
    
    
    
    

    // ── Getters para persistencia ───────────────────────────────
    
    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Mesa> getMesas() { return mesas; }
    public List<Venta> getVentas() { return ventas; }
    public List<Sugerencia> getSugerencias() { return sugerencias; }
    public int getCapacidadMaxima() { return capacidadMaxima; }
    public ServicioInventario getServicioInventario() { return servicioInventario; }
    public ServicioPrestamo getServicioPrestamo() { return servicioPrestamo; }
    public ServicioTurnos getServicioTurnos() { return servicioTurnos; }
    public ServicioReportes getServicioReportes() { return servicioReportes; }
    public ServicioCafeteria getServicioCafeteria() { return servicioCafeteria; }
    public ServicioVentas getServicioVentas() { return servicioVentas; }

}