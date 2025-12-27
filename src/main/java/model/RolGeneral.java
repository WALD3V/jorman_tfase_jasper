package Jasper.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Clase que representa los detalles del rol de un empleado.
 * Los totales vienen calculados directamente de las queries SQL.
 */
public class RolGeneral {

    private String periodoAnio;
    private String periodoMes;
    private String fechaCorte;
    private String fechaInicio;
    private String fechaFin;
    private String empCodigo;
    private String empNombres;
    private String empApellidos;
    private String empFuncion;
    private String descripcionTipoRol;
    private String ciaDescripcion;
    
    // Totales calculados por las queries SQL
    private double totalIngresos = 0.0;
    private double totalEgresos = 0.0;
    private double netoAPagar = 0.0;

    private List<DetalleRol> ingresos;
    private List<DetalleRol> egresos;
    private List<DetalleRol> descuentos;

    // Constructor
    public RolGeneral(String periodoAnio, String periodoMes, String fechaCorte,
            String fechaInicio, String fechaFin, String empCodigo, String empNombres,
            String empApellidos, String empFuncion, String descripcionTipoRol,
            String ciaDescripcion, List<DetalleRol> ingresos, List<DetalleRol> egresos,
            List<DetalleRol> descuentos) {
        this.periodoAnio = periodoAnio;
        this.periodoMes = periodoMes;
        this.fechaCorte = fechaCorte;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.empCodigo = empCodigo;
        this.empNombres = empNombres;
        this.empApellidos = empApellidos;
        this.empFuncion = empFuncion;
        this.descripcionTipoRol = descripcionTipoRol;
        this.ciaDescripcion = ciaDescripcion;
        this.ingresos = ingresos;
        this.egresos = egresos;
        this.descuentos = descuentos;
    }

    // Getters y Setters básicos
    public String getPeriodoAnio() { return periodoAnio; }
    public void setPeriodoAnio(String periodoAnio) { this.periodoAnio = periodoAnio; }

    public String getPeriodoMes() { return periodoMes; }
    public void setPeriodoMes(String periodoMes) { this.periodoMes = periodoMes; }

    public String getNombreMes() { return periodoMes; }

    public String getFechaCorte() {
        return formatearFecha(fechaCorte);
    }
    public void setFechaCorte(String fechaCorte) { this.fechaCorte = fechaCorte; }

    public String getFechaInicio() {
        return formatearFecha(fechaInicio);
    }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() {
        return formatearFecha(fechaFin);
    }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }

    public String getEmpCodigo() { return empCodigo; }
    public void setEmpCodigo(String empCodigo) { this.empCodigo = empCodigo; }

    public String getEmpNombres() { return empNombres; }
    public void setEmpNombres(String empNombres) { this.empNombres = empNombres; }

    public String getEmpApellidos() { return empApellidos; }
    public void setEmpApellidos(String empApellidos) { this.empApellidos = empApellidos; }

    public String getEmpNombreCompleto() { return empNombres + " " + empApellidos; }

    public String getEmpFuncion() { return empFuncion; }
    public void setEmpFuncion(String empFuncion) { this.empFuncion = empFuncion; }

    public String getDescripcionTipoRol() { return descripcionTipoRol; }
    public void setDescripcionTipoRol(String descripcionTipoRol) { this.descripcionTipoRol = descripcionTipoRol; }

    public String getCiaDescripcion() { return ciaDescripcion; }
    public void setCiaDescripcion(String ciaDescripcion) { this.ciaDescripcion = ciaDescripcion; }

    // Listas de detalles
    public List<DetalleRol> getIngresos() { return ingresos; }
    public void setIngresos(List<DetalleRol> ingresos) { this.ingresos = ingresos; }

    public List<DetalleRol> getEgresos() { return egresos; }
    public void setEgresos(List<DetalleRol> egresos) { this.egresos = egresos; }

    public List<DetalleRol> getDescuentos() { return descuentos; }
    public void setDescuentos(List<DetalleRol> descuentos) { this.descuentos = descuentos; }

    // Totales calculados por las queries SQL
    public double getTotalIngresos() { return totalIngresos; }
    public void setTotalIngresos(double totalIngresos) { this.totalIngresos = totalIngresos; }

    public double getTotalEgresos() { return totalEgresos; }
    public void setTotalEgresos(double totalEgresos) { this.totalEgresos = totalEgresos; }

    public double getNetoAPagar() { return netoAPagar; }
    public void setNetoAPagar(double netoAPagar) { this.netoAPagar = netoAPagar; }

    // Método auxiliar para formatear fechas
    private String formatearFecha(String fecha) {
        if (fecha == null) return "";
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy");
            Date fechaObj = formatoEntrada.parse(fecha);
            return formatoSalida.format(fechaObj);
        } catch (ParseException e) {
            return fecha; // Retorna la fecha original si no se puede formatear
        }
    }
    
    // Getter para compatibilidad con JasperReports (todo en minúsculas)
    public double getNetoapagar() { return netoAPagar; }
}
