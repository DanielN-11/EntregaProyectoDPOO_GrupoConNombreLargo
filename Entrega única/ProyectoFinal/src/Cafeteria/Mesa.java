package Cafeteria;

import java.util.List;
import java.util.ArrayList;

import juegos.CopiaJuego;
import juegos.JuegoDeMesa;
import transacciones.ItemVenta;
import transacciones.Pedido;
import transacciones.Prestamo;

public class Mesa {
	private String idMesa;
	private boolean ocupada;
	private int numeroPersonas;
	private int numeroNinos;
	private int numeroJovenes;
	private List<Prestamo> prestamosActivos;
	private Pedido pedidoActual;
	
	public Mesa(String idMesa, boolean ocupada, int numeroPersonas, int numeroNinos,
            int numeroJovenes, List<Prestamo> prestamosActivos, Pedido pedidoActual) {
    this.idMesa = idMesa;
    this.ocupada = ocupada;
    this.numeroPersonas = numeroPersonas;
    this.numeroNinos = numeroNinos;
    this.numeroJovenes = numeroJovenes;
    this.prestamosActivos = (prestamosActivos != null) ? prestamosActivos : new ArrayList<>();
    this.pedidoActual = pedidoActual;
	}
	
	public void ocupar(int numPersonas, int numNinos, int numJovenes) {
		numeroPersonas += numPersonas;
		numeroNinos += numNinos;
		numeroJovenes += numJovenes;
		ocupada = true;
	}
	
	public void liberar() {
		numeroPersonas = 0;
		numeroNinos = 0;
		numeroJovenes = 0;
		ocupada = false;
		for (Prestamo prestamo : prestamosActivos) {
	        if (prestamo.isActivo()) {
	            prestamo.devolver();
	        }
	    }
		prestamosActivos.clear();
		pedidoActual = null;
	}
	
	public List<Prestamo> getPrestamosActivos() {
		return prestamosActivos;
	}
	
	public boolean agregarPrestamo(Prestamo prestamo) {

	    if (prestamosActivos.size() >= 2) {
	        System.out.println("La mesa ya tiene 2 juegos prestados.");
	        return false;
	    }

	    JuegoDeMesa juego = prestamo.getCopia().getJuego();

	    if (!juego.esAptoParaPersonas(numeroPersonas)) {
	        System.out.println("El juego no soporta el número de personas.");
	        return false;
	    }

	    if (!juego.esAptoParaEdad(hayNinos(), hayJovenes())) {
	        System.out.println("El juego no es apto para la edad de los comensales.");
	        return false;
	    }

	    if (tieneBebidaCaliente() && juego.getCategoria().equals("Accion")) {
	        System.out.println("No se puede pedir juego de Acción con bebida caliente.");
	        return false;
	    }

	    prestamosActivos.add(prestamo);
	    return true;
	}
	
	public boolean tieneJuegoDeAccion() {
		int i = 0;
		while (i < prestamosActivos.size()) {
			Prestamo prestamo = prestamosActivos.get(i);
			CopiaJuego copia = prestamo.getCopia();
			JuegoDeMesa juego = copia.getJuego();
			String categoria = juego.getCategoria();
			i ++;
			if (categoria.equals("Accion")) {
				return true;
			}
		}
		return false;	
	}
	
	public boolean hayNinos() {
		if(numeroNinos > 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean hayJovenes() {
		if(numeroJovenes > 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isOcupada() {
		return ocupada;
	}
	
	public int cantidadJuegosPrestados() {
		return prestamosActivos.size();
	}
	
	public int getNumeroPersonas() {
	    return numeroPersonas;
	}

	public boolean tieneBebidaCaliente() {
	    if (pedidoActual == null) {
	        return false;
	    }
	    for (ItemVenta item : pedidoActual.getItems()) {
	        if (item.getAlimento() instanceof Bebida) {
	            Bebida bebida = (Bebida) item.getAlimento();
	            if (bebida.isCaliente()) {
	                return true;
	            }
	        }
	    }
	    return false;
	}
	
	public void setPedidoActual(Pedido pedido) {
	    this.pedidoActual = pedido;
	}
	
	public String getIdMesa() {
	    return idMesa;
	}
}