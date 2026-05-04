package Cafeteria;

import java.util.List;

public class Pasteleria extends Alimento{
	
	private List<String> alergenos;

	public Pasteleria(String idProducto, String nombre, double precio, List<String> alergenos) {
		super(idProducto, nombre, precio);
		this.alergenos = alergenos;
	}

	public List<String> getAlergenos() {
		return alergenos;
	}
	
	public boolean tieneAlergenos() {
		if (alergenos.isEmpty()) {
			return false;
		}
		else {
			return true;
		}
	}
	
	@Override
	public boolean puedeServirseEnMesa(Mesa mesa) {
	    if (tieneAlergenos()) {
	        System.out.println("El producto '" + getNombre() + 
	            "' contiene los siguientes alérgenos: " + alergenos.toString());
	    }
	    return true;
	}

}
