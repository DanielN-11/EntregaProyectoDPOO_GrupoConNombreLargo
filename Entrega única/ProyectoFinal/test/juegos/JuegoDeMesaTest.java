package juegos;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JuegoDeMesaTest {

    private JuegoDeMesa juegoCartas;
    private JuegoDeMesa juegoAccion;

    @BeforeEach
    void setUp() {
        juegoCartas = new JuegoDeMesa("J1", "Uno", 1992, "Mattel", 2, 4, 6, "Cartas", false);
        juegoAccion = new JuegoDeMesa("J2", "Twister", 1966, "Hasbro", 2, 6, 19, "Accion", false);
    }

    @Test
    void esAptoParaPersonas_yEdad_variosEscenarios() {
        assertTrue(juegoCartas.esAptoParaPersonas(3));
        assertFalse(juegoCartas.esAptoParaPersonas(1));
        assertFalse(juegoCartas.esAptoParaPersonas(5));

        assertTrue(juegoCartas.esAptoParaEdad(false, false));
        assertFalse(juegoCartas.esAptoParaEdad(true, false));

        assertFalse(juegoAccion.esAptoParaEdad(false, true));
        assertTrue(juegoAccion.esAptoParaEdad(false, false));
    }

    @Test
    void esCompatibleConBebidaCaliente_falsoParaAccion() {
        assertFalse(juegoAccion.esCompatibleConBebidaCaliente());
        assertTrue(juegoCartas.esCompatibleConBebidaCaliente());
    }
}