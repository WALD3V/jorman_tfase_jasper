package Jasper;
import Jasper.service.JasperReportExample;
import Jasper.service.ExtractDetails1_Fixed;
import Jasper.model.DetalleRol;
import Jasper.model.RolGeneral;
import Jasper.config.DatabaseConnection1;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Clase Main que genera un PDF por cada empleado que encuentre 
 * Los parámetros se definen aquí y se pasan a la consulta
 */
public class Main1 {
    
    // PARÁMETROS DEL SISTEMA - Configura estos valores según tu entorno
    private static final String CIA_CODE = "01";      // Código de compañía
    private static final String TIPO_ROL = "001";      // Código del tipo de rol
    private static final String COD_INFORME = "01";   // Código del informe (tiene 102,952.72 en valores)
    private static final String COD_CEN = "000";       // Centro de costo (000 = todos)
    private static final String EMP_INICIAL = "0920521226";   // Primer empleado con datos
    private static final String EMP_FINAL = "0920521226";    // Último empleado con datos
    
    public static void main(String[] args) {
        try {
            // Conexión a la base de datos
            Connection connection = DatabaseConnection1.getConnection();
            
            System.out.println("Iniciando generación de reportes...");
            
            // Obtener todos los roles con una sola consulta usando tus parámetros
            Map<String, RolGeneral> rolesMap = ExtractDetails1_Fixed.getAllRolesByParametros(
                connection, EMP_INICIAL, EMP_FINAL, CIA_CODE, TIPO_ROL, COD_INFORME, COD_CEN
            );
            
            System.out.println("Empleados encontrados: " + rolesMap.size());
            
            // Generar un PDF para cada empleado encontrado
            for (Map.Entry<String, RolGeneral> entry : rolesMap.entrySet()) {
                String empCodigo = entry.getKey();
                RolGeneral rol = entry.getValue();
                
                // Imprimir información del empleado
                System.out.println("\n=== EMPLEADO: " + empCodigo + " - " + rol.getEmpNombres() + " " + rol.getEmpApellidos() + " ===");
                
                // DEBUG: Imprimir totales del modelo
                System.out.println("DEBUG TOTALES:");
                System.out.println("  - getTotalIngresos(): " + rol.getTotalIngresos());
                System.out.println("  - getTotalEgresos(): " + rol.getTotalEgresos());
                System.out.println("  - getNetoapagar(): " + rol.getNetoapagar());
                
                // Imprimir ingresos
                System.out.println("INGRESOS:");
                if (rol.getIngresos().isEmpty()) {
                    System.out.println("  - Sin ingresos");
                } else {
                    for (DetalleRol ingreso : rol.getIngresos()) {
                        System.out.println("  - " + ingreso.getNombre() + ": $" + ingreso.getValor());
                    }
                }
                
                // Imprimir egresos
                System.out.println("EGRESOS:");
                if (rol.getEgresos().isEmpty()) {
                    System.out.println("  - Sin egresos");
                } else {
                    for (DetalleRol egreso : rol.getEgresos()) {
                        System.out.println("  - " + egreso.getNombre() + ": $" + egreso.getValor());
                    }
                }
                
                // Imprimir descuentos
                System.out.println("DESCUENTOS:");
                if (rol.getDescuentos().isEmpty()) {
                    System.out.println("  - Sin descuentos");
                } else {
                    for (DetalleRol descuento : rol.getDescuentos()) {
                        System.out.println("  - " + descuento.getNombre() + ": $" + descuento.getValor());
                    }
                }
                
                String fileName = "RolGeneral_" + empCodigo + ".pdf";
                
                try {
                    JasperReportExample.generarReporte(rol, fileName);
                    System.out.println("PDF generado: " + fileName);
                } catch (Exception e) {
                    System.err.println("Error generando PDF para empleado " + empCodigo + ": " + e.getMessage());
                }
            }
            
            System.out.println("Generación de reportes completada.");
            
        } catch (Exception e) {
            System.err.println("Error en la generación de reportes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}