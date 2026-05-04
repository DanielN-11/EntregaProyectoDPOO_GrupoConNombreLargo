package Servicios;

import transacciones.Venta;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ServicioReportes {

    private List<Venta> ventasJuegos;
    private List<Venta> ventasCafeteria;

    public ServicioReportes(List<Venta> ventasJuegos, List<Venta> ventasCafeteria) {
        this.ventasJuegos = ventasJuegos;
        this.ventasCafeteria = ventasCafeteria;
    }

    public List<Venta> generarInformeVentas(Venta.TipoVenta tipo, Date desde, Date hasta) {
        List<Venta> fuente = tipo == Venta.TipoVenta.JUEGO ? ventasJuegos : ventasCafeteria;
        List<Venta> resultado = new ArrayList<>();
        for (Venta v : fuente) {
            if (!v.getFecha().before(desde) && !v.getFecha().after(hasta)) {
                resultado.add(v);
            }
        }
        return resultado;
    }

    public List<Venta> ventasPorDia(Date fecha) {
        List<Venta> todas = new ArrayList<>();
        todas.addAll(ventasJuegos);
        todas.addAll(ventasCafeteria);
        List<Venta> resultado = new ArrayList<>();
        for (Venta v : todas) {
            if (mismoDia(v.getFecha(), fecha)) resultado.add(v);
        }
        return resultado;
    }

    public List<Venta> ventasPorSemana(int semana) {
        List<Venta> todas = new ArrayList<>();
        todas.addAll(ventasJuegos);
        todas.addAll(ventasCafeteria);
        List<Venta> resultado = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        for (Venta v : todas) {
            cal.setTime(v.getFecha());
            if (cal.get(Calendar.WEEK_OF_YEAR) == semana) resultado.add(v);
        }
        return resultado;
    }

    public List<Venta> ventasPorMes(int mes) {
        List<Venta> todas = new ArrayList<>();
        todas.addAll(ventasJuegos);
        todas.addAll(ventasCafeteria);
        List<Venta> resultado = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        for (Venta v : todas) {
            cal.setTime(v.getFecha());
            if (cal.get(Calendar.MONTH) == mes) resultado.add(v);
        }
        return resultado;
    }

    public double getTotalSubtotal(List<Venta> ventas) {
        double total = 0;
        for (Venta v : ventas) total += v.getSubtotal();
        return total;
    }

    public double getTotalImpuestos(List<Venta> ventas) {
        double total = 0;
        for (Venta v : ventas) total += v.getImpuesto();
        return total;
    }

    public double getTotalPropinas(List<Venta> ventas) {
        double total = 0;
        for (Venta v : ventas) total += v.getPropina();
        return total;
    }

    private boolean mismoDia(Date a, Date b) {
        Calendar ca = Calendar.getInstance();
        Calendar cb = Calendar.getInstance();
        ca.setTime(a); cb.setTime(b);
        return ca.get(Calendar.YEAR) == cb.get(Calendar.YEAR)
            && ca.get(Calendar.DAY_OF_YEAR) == cb.get(Calendar.DAY_OF_YEAR);
    }
}