package Cafeteria;

public class Bebida extends Alimento{
	
	private boolean Alcoholica;
	private boolean Caliente;
	
	public Bebida(String idProducto, String nombre, double precio, boolean alcoholica, boolean caliente) {
		super(idProducto, nombre, precio);
		this.Alcoholica = alcoholica;
		this.Caliente = caliente;
	}

	public boolean isAlcoholica() {
		return Alcoholica;
	}

	public boolean isCaliente() {
		return Caliente;
	}
	
	public boolean puedeServirseEnMesa(Mesa mesa) {
		if (isAlcoholica() == true && (mesa.hayNinos() || mesa.hayJovenes())) {
			return false;
		}
		else if (isCaliente() == true && mesa.tieneJuegoDeAccion() == true) {
			return false;
		}
		else {
			return true;
		}
	}
}
