package Torneos;

import java.util.ArrayList;
import java.util.List;
import usuarios.Cliente;

import juegos.JuegoDeMesa;
import usuarios.Usuario;

public abstract class torneos {

    // Clase abstracta para los torneos
    // Un torneo tiene un juego, un día a la semana, un cupo máximo y una lista de inscripciones

    private String idTorneo;
    private String nombre;
    private JuegoDeMesa juego;
    private String diaSemana;
    private int cupoMaximo;
    private List<Inscripcion> inscripciones;

    public torneos(String idTorneo, String nombre, JuegoDeMesa juego,
                   String diaSemana, int cupoMaximo) {
        this.idTorneo = idTorneo;
        this.nombre = nombre;
        this.juego = juego;
        this.diaSemana = diaSemana;
        this.cupoMaximo = cupoMaximo;
        this.inscripciones = new ArrayList<>();
    }

    // getters y setters basicooosssssss

    public String getIdTorneo() {
        return idTorneo;
    }

    public String getNombre() {
        return nombre;
    }

    public JuegoDeMesa getJuego() {
        return juego;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public List<Inscripcion> getInscripciones() {
        return inscripciones;
    }

    // Métodos de cupos generales

    public int cuposOcupados() {
        int total = 0;
        for (Inscripcion i : inscripciones) {
            total += i.getNumParticipantes();
        }
        return total;
    }

    public int cuposDisponibles() {
        return cupoMaximo - cuposOcupados();
    }

    // Cupos reservados para fanáticos:
    // 20% del cupo máximo, redondeado hacia arriba
    public int getCuposReservadosFanaticos() {
        return (int) Math.ceil(cupoMaximo * 0.20);
    }

    public int cuposFanaticosOcupados() {
        int total = 0;
        for (Inscripcion i : inscripciones) {
            total += i.getCuposFanaticosUsados();
        }
        return total;
    }

    public int cuposFanaticosDisponibles() {
        return getCuposReservadosFanaticos() - cuposFanaticosOcupados();
    }

    public int cuposRegularesOcupados() {
        int total = 0;
        for (Inscripcion i : inscripciones) {
            total += i.getCuposRegularesUsados();
        }
        return total;
    }

    public int getCuposRegularesTotales() {
        return cupoMaximo - getCuposReservadosFanaticos();
    }

    public int cuposRegularesDisponibles() {
        return getCuposRegularesTotales() - cuposRegularesOcupados();
    }

    public boolean usuarioEsFanatico(Usuario usuario) {
        return usuario instanceof Cliente && usuario.getFavoritos().contains(juego);
    }
    
    public Inscripcion buscarInscripcion(Usuario usuario) {
        for (Inscripcion i : inscripciones) {
            if (i.getUsuario().equals(usuario)) {
                return i;
            }
        }
        return null;
    }

    public void agregarInscripcion(Inscripcion inscripcion) {
        inscripciones.add(inscripcion);
    }

    public boolean eliminarInscripcion(Usuario usuario) {
        Inscripcion ins = buscarInscripcion(usuario);
        if (ins == null) {
            return false;
        }
        inscripciones.remove(ins);
        return true;
    }

    public abstract boolean esAmistoso();

    public abstract String getTipo();
}