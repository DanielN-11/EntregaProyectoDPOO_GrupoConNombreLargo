package Servicios;
import Cafeteria.Mesa;
import inventario.InventarioPrestamo;
import juegos.CopiaJuego;
import juegos.JuegoDeMesa;
import transacciones.Prestamo;
import usuarios.Cliente;
import usuarios.Empleado;
import usuarios.Mesero;
import usuarios.Usuario;
import java.util.ArrayList;
import java.util.List;
import usuarios.Administrador;

public class ServicioPrestamo {

    private InventarioPrestamo inventarioPrestamo;
    private List<Prestamo> historialPrestamos;
    private List<Mesero> meseros;

    public ServicioPrestamo(InventarioPrestamo inventarioPrestamo, List<Mesero> meseros) {
        this.inventarioPrestamo = inventarioPrestamo;
        this.meseros = meseros;
        this.historialPrestamos = new ArrayList<>();
    }

    public Prestamo prestarJuego(String idPrestamo, Usuario usuario, JuegoDeMesa juego, Mesa mesa, List<Mesa> todasLasMesas) {
        if (!validarPrestamo(usuario, juego, mesa, todasLasMesas)) {
            return null;
        }
        CopiaJuego copia = inventarioPrestamo.getCopiaDisponible(juego);
        if (copia == null) {
            return null;
        }
        if (juego.esDificil()) {
            Mesero capacitado = buscarMeseroCapacitado(juego, meseros);
            if (capacitado == null) {
                System.out.println("ADVERTENCIA: No hay mesero capacitado para este juego.");
            }
        }
        Prestamo prestamo = new Prestamo(idPrestamo, copia, usuario, mesa);
        if (mesa != null) {
            mesa.agregarPrestamo(prestamo);
        }
        historialPrestamos.add(prestamo);
        return prestamo;
    }

    public void devolverJuego(Prestamo prestamo) {
        prestamo.devolver();
        Mesa mesa = prestamo.getMesa();
        if (mesa != null) {
            mesa.getPrestamosActivos().remove(prestamo);
        }
    }

    public boolean validarPrestamo(Usuario usuario, JuegoDeMesa juego, Mesa mesa, List<Mesa> todasLasMesas) {
    	if (usuario instanceof Administrador) { 
            System.out.println("El administrador no puede pedir juegos prestados.");
            return false;
        }
        if (usuario instanceof Empleado) {
            Empleado empleado = (Empleado) usuario;
            if (empleado.estaEnTurno()) {
                boolean hayClientes = false;
                for (Mesa m : todasLasMesas) {
                    if (m.isOcupada()) {
                        hayClientes = true;
                        break;
                    }
                }
                if (hayClientes) return false;  
            }
            return true;
        }
        // Validaciones para cliente
        if (mesa == null || !mesa.isOcupada()) {
            return false;
        }
        if (mesa.cantidadJuegosPrestados() >= 2) {
            return false;
        }
        if (!juego.esAptoParaPersonas(mesa.getNumeroPersonas())) {
            return false;
        }
        if (!juego.esAptoParaEdad(mesa.hayNinos(), mesa.hayJovenes())) {
            return false;
        }
        if (!juego.esCompatibleConBebidaCaliente() && mesa.tieneBebidaCaliente()) {
            return false;
        }
        return true;
    }

    public Mesero buscarMeseroCapacitado(JuegoDeMesa juego, List<Mesero> meseros) {
        for (Mesero mesero : meseros) {
            if (mesero.conoceJuego(juego) && mesero.estaEnTurno()) {
                return mesero;
            }
        }
        return null;
    }

    public List<Prestamo> getHistorialPrestamos() {
        return historialPrestamos;
    }

    public List<Prestamo> getPrestamosActivos() {
        List<Prestamo> activos = new ArrayList<>();
        for (Prestamo p : historialPrestamos) {
            if (p.isActivo()) activos.add(p);
        }
        return activos;
    }
    
    public List<Mesero> getMeseros() {
        return meseros;
    }
}