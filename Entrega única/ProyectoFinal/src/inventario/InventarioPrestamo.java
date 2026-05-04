package inventario;
import java.util.ArrayList;
import java.util.List;
import juegos.CopiaJuego;
import juegos.JuegoDeMesa;

public class InventarioPrestamo {
	
	private List<CopiaJuego> copias;
	
	public InventarioPrestamo () {
		this.copias = new ArrayList<>();
	}
	
	public void agregarCopia(CopiaJuego copia) { 
		if (copias.contains(copia) == false) {
			copias.add(copia);
			}
		}
	
	public void retirarCopia(CopiaJuego copia) { 
		if (copias.contains(copia)) {
			copias.remove(copia);
			} 
		else{
		System.out.println("La copia no se encuentra en la lista de copias");
			}
		}

    public CopiaJuego getCopiaDisponible(JuegoDeMesa juego) {
        for (CopiaJuego copia : copias) {
            if (copia.getJuego().equals(juego) && copia.isDisponible()) {
                return copia;
            }
        }
        return null;
    }

    public List<CopiaJuego> getCopiasPorJuego(JuegoDeMesa juego) {
        List<CopiaJuego> resultado = new ArrayList<>();
        for (CopiaJuego copia : copias) {
            if (copia.getJuego().equals(juego)) {
                resultado.add(copia);
            }
        }
        return resultado;
    }

    public List<CopiaJuego> getCopias() {
        return copias;
    }
}