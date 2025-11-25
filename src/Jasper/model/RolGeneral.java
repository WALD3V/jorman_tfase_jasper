package Jasper.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Clase que representa los detalles del rol de un empleado.
 */
public class RolGeneral {

    private String periodoAnio;
    private int periodoMes;
    private String fechaCorte;
    private String fechaInicio;
    private String fechaFin;
    private String empCodigo;
    private String empNombres;
    private String empApellidos;
    private String empFuncion;
    private double sumaI;
    private double sumaE;
    private double sumaD;
    
    
    
    public void setPeriodoAnio(String periodoAnio) {
        this.periodoAnio = periodoAnio;
    }
    public void setPeriodoMes(int periodoMes) {
        this.periodoMes = periodoMes;
    }
    public void setFechaCorte(String fechaCorte) {
        this.fechaCorte = fechaCorte;
    }
    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }
    public void setEmpCodigo(String empCodigo) {
        this.empCodigo = empCodigo;
    }
    public void setEmpNombres(String empNombres) {
        this.empNombres = empNombres;
    }
    public void setEmpApellidos(String empApellidos) {
        this.empApellidos = empApellidos;
    }
    public void setEmpFuncion(String empFuncion) {
        this.empFuncion = empFuncion;
    }

    public double getSumaI() {
        return this.sumaI;
    }

    public void setSumaI(double sumaI) {
        this.sumaI = sumaI;
    }

    public double getSumaE() {
        return this.sumaE;
    }

    public void setSumaE(double sumaE) {
        this.sumaE = sumaE;
    }

    public double getSumaD() {
        return this.sumaD;
    }

    public void setSumaD(double sumaD) {
        this.sumaD = sumaD;
    }
    public void setIngresos(List<DetalleRol> ingresos) {
        this.ingresos = ingresos;
    }
    public void setEgresos(List<DetalleRol> egresos) {
        this.egresos = egresos;
    }
    public void setDescuentos(List<DetalleRol> descuentos) {
        this.descuentos = descuentos;
    }

    private List<DetalleRol> ingresos;
    private List<DetalleRol> egresos;
    private List<DetalleRol> descuentos; // Nueva lista para descuentos

    // Constructor con todos los parámetros
    public RolGeneral(String periodoAnio, int periodoMes, String fechaCorte, String fechaInicio, String fechaFin, String empCodigo, String empNombres, String empApellidos, String empFuncion, List<DetalleRol> ingresos, List<DetalleRol> egresos, List<DetalleRol> descuentos) {
        this.periodoAnio = periodoAnio;
        this.periodoMes = periodoMes;
        this.fechaCorte = fechaCorte;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.empCodigo = empCodigo;
        this.empNombres = empNombres;
        this.empApellidos = empApellidos;
        this.empFuncion = empFuncion;
        this.ingresos = ingresos;
        this.egresos = egresos;
        this.descuentos = descuentos; // Inicializar la nueva lista
    }

    // Métodos de acceso a los campos
    public String getPeriodoAnio() {
        return periodoAnio;
    }

    public String getPeriodoMes() {
        String formattedMonth = String.format("%02d", periodoMes);
        return formattedMonth;
    }

    public String getFechaCorte() {
        String fechaFormateada = "";
        String fechaOriginal = fechaCorte;
        SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date fecha = formatoEntrada.parse(fechaOriginal);
            fechaFormateada = formatoSalida.format(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fechaFormateada;
    }
    public String getFechaInicio() {
        String fechaFormateada = "";
        String fechaOriginal = fechaInicio;
        SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date fecha = formatoEntrada.parse(fechaOriginal);
            fechaFormateada = formatoSalida.format(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fechaFormateada;
    }
    public String getFechaFin() {
        String fechaFormateada = "";
        String fechaOriginal = fechaFin;
        SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date fecha = formatoEntrada.parse(fechaOriginal);
            fechaFormateada = formatoSalida.format(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fechaFormateada;
    }

    public String getEmpCodigo() {
        return empCodigo;
    }

    public String getEmpNombres() {
        return empNombres;
    }

    public String getEmpNombreCompleto() {
        String name = empNombres + " " + empApellidos;
        return name;
    }

    public String getEmpApellidos() {
        return empApellidos;
    }

    public String getEmpFuncion() {
        return empFuncion;
    }

    public List<DetalleRol> getIngresos() {
        return ingresos;
    }

    public List<DetalleRol> getEgresos() {
        return egresos;
    }

    public List<DetalleRol> getDescuentos() {
        return descuentos;
    }

    public double sumarIngresos() {
        double suma = 0.0;
        for (DetalleRol detalle : ingresos) {
            sumaI += detalle.getValor();
        }
        return suma;
    }

    // Método para sumar egresos
    public double sumarEgresos() {
        double suma = 0.0;
        for (DetalleRol detalle : egresos) {
            sumaE += detalle.getValor();
        }
        return suma;
    }

    // Método para sumar egresos
    public double sumarDescuentos() {
        double suma = 0.0;
        for (DetalleRol detalle : descuentos) {
            sumaD += detalle.getValor();
        }
        return suma;
    }

    public double calcularBalance() {
        return sumaI - sumaE - sumaD;
    }

    // Formateo de argumentos adicionales al objeto RolGeneral
    public String getNombreMes() {
        String nombreMes = Month.of(periodoMes).getDisplayName(TextStyle.FULL, new Locale("es")).toUpperCase();
        return nombreMes;
    }
}
