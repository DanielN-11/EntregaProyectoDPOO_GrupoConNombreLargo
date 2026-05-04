package Cafeteria;

public abstract class Alimento {
	
	private String idProducto;
	private String nombre;
	private double precio;
	
	public Alimento(String idProducto, String nombre, double precio) {
		this.idProducto = idProducto;
		this.nombre = nombre;
		this.precio = precio;
	}
	
	public double getPrecio() {
		return precio;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public String getIdProducto() {
	    return idProducto;
	}
	
	public boolean puedeServirseEnMesa(Mesa mesa) {
		return false;
	}
}
