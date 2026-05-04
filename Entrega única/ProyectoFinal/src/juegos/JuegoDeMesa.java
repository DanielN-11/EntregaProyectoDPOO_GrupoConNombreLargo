package juegos;

public class JuegoDeMesa {
	
	private String idJuego;
	private String nombre;
	private int anioPublicacion;
	private String empresaMatriz;
	private int minJugadores;
	private int maxJugadores;
	private int edadMinima;
	private String categoria;
	private boolean dificil;
	
	public JuegoDeMesa(String idJuego, String nombre, int anioPublicacion, String empresaMatriz, int minJugadores,
			int maxJugadores, int edadMinima, String categoria, boolean esDificil) {
		this.idJuego = idJuego;
		this.nombre = nombre;
		this.anioPublicacion = anioPublicacion;
		this.empresaMatriz = empresaMatriz;
		this.minJugadores = minJugadores;
		this.maxJugadores = maxJugadores;
		this.edadMinima = edadMinima;
		this.categoria = categoria;
		this.dificil = esDificil;
	}

	public String getNombre() {
		return nombre;
	}

	public String getCategoria() {
		return categoria;
	}

	public boolean esDificil() {
		return dificil;
	}
	
	public boolean esAptoParaPersonas(int numPersonas) {
	    return numPersonas >= minJugadores && numPersonas <= maxJugadores;
	}
	
	public boolean esAptoParaEdad(boolean hayNinos, boolean hayJovenes) {
		if (hayNinos == true && edadMinima > 5) {
			return false;
		}
		else if(hayJovenes == true && edadMinima > 18) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public boolean esCompatibleConBebidaCaliente() {
		if (categoria.equals("Accion")) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public String getIdJuego() {
	    return idJuego;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null || getClass() != obj.getClass()) return false;
	    JuegoDeMesa otro = (JuegoDeMesa) obj;
	    return idJuego.equals(otro.idJuego);
	}

	@Override
	public int hashCode() {
	    return idJuego.hashCode();
	}
	
	public int getMinJugadores() {
		return minJugadores; 
		}
	
	public int getMaxJugadores() {
		return maxJugadores; 
		}
	
	public int getEdadMinima() {
		return edadMinima; 
		}
	
	public int getAnioPublicacion() {
		return anioPublicacion; 
		}
	
	public String getEmpresaMatriz() { 
		return empresaMatriz; 
		}
}