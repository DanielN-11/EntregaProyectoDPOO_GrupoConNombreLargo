package Servicios;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import transacciones.Venta;
import usuarios.Cliente;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ServicioReportesTest {

    private ServicioReportes servicioReportes;
    private List<Venta> ventasJuegos;
    private List<Venta> ventasCafeteria;
    private Cliente cliente;
    private Date hoy;

    @BeforeEach
    void setUp() {
        cliente = new Cliente("U1", "MarioGei", "Luiguitmb", "Juan", "Nintendobot12");
        ventasJuegos = new ArrayList<>();
        ventasCafeteria = new ArrayList<>();
        servicioReportes = new ServicioReportes(ventasJuegos, ventasCafeteria);
        hoy = new Date();

        ventasJuegos.add(new Venta("V1", cliente, new ArrayList<>(), hoy, Venta.TipoVenta.JUEGO, 50000, 9500, 0));
        ventasJuegos.add(new Venta("V2", cliente, new ArrayList<>(), hoy, Venta.TipoVenta.JUEGO, 30000, 5700, 0));
        ventasCafeteria.add(new Venta("V3", cliente, new ArrayList<>(), hoy, Venta.TipoVenta.CAFETERIA, 20000, 1600, 2000));
    }

    @Test
    void generarInformeVentas_filtraPorTipoYFecha() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date ayer = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 2);
        Date manana = cal.getTime();

        List<Venta> juegos = servicioReportes.generarInformeVentas(Venta.TipoVenta.JUEGO, ayer, manana);
        List<Venta> cafeteria = servicioReportes.generarInformeVentas(Venta.TipoVenta.CAFETERIA, ayer, manana);

        assertEquals(2, juegos.size());
        assertEquals(1, cafeteria.size());
    }

    @Test
    void ventasPorDia_semana_mes() {
        List<Venta> porDia = servicioReportes.ventasPorDia(hoy);
        assertEquals(3, porDia.size());

        Calendar cal = Calendar.getInstance();
        cal.setTime(hoy);
        int semana = cal.get(Calendar.WEEK_OF_YEAR);
        int mes = cal.get(Calendar.MONTH);

        assertEquals(3, servicioReportes.ventasPorSemana(semana).size());
        assertEquals(3, servicioReportes.ventasPorMes(mes).size());
        assertEquals(0, servicioReportes.ventasPorDia(new Date(0)).size());
    }

    @Test
    void getTotales_subtotal_impuestos_propinas() {
        List<Venta> todas = new ArrayList<>();
        todas.addAll(ventasJuegos);
        todas.addAll(ventasCafeteria);

        assertEquals(100000, servicioReportes.getTotalSubtotal(todas), 0.01);
        assertEquals(16800,  servicioReportes.getTotalImpuestos(todas), 0.01);
        assertEquals(2000,   servicioReportes.getTotalPropinas(todas), 0.01);
    }
}