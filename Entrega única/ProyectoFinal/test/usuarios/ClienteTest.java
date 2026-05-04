package usuarios;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClienteTest {

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente("U1", "MarioGei", "Luiguitmb", "Juan", "Nintendobot12");
        cliente.agregarPuntos(500);
    }

    @Test
    void usarPuntos_exitoso_YFallaConInsuficientes() {
        assertTrue(cliente.usarPuntos(300));
        assertEquals(200, cliente.getPuntosFidelidad(), 0.01);

        assertFalse(cliente.usarPuntos(500));
        assertEquals(200, cliente.getPuntosFidelidad(), 0.01);
    }
}