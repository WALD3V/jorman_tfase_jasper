package Jasper;
import Jasper.service.JasperReportExample;
import Jasper.service.ExtractDetails1;
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
    private static final String CIA_CODE = "001";      // Código de compañía
    private static final String TIPO_ROL = "001";      // Código del tipo de rol
    private static final String COD_INFORME = "001";   // Código del informe
    private static final String COD_CEN = "000";       // Centro de costo (000 = todos)
    private static final String EMP_INICIAL = "000";   // Empleado inicial
    private static final String EMP_FINAL = "999999";  // Empleado final
    
    public static void main(String[] args) {
        try {
            // Conexión a la base de datos
            Connection connection = DatabaseConnection1.getConnection();
            
            System.out.println("Iniciando generación de reportes...");
            
            // Obtener todos los roles con una sola consulta usando tus parámetros
            Map<String, RolGeneral> rolesMap = ExtractDetails1.getAllRolesByParametros(
                connection, EMP_INICIAL, EMP_FINAL, CIA_CODE, TIPO_ROL, COD_INFORME, COD_CEN
            );
            
            System.out.println("Empleados encontrados: " + rolesMap.size());
            
            // Generar un PDF para cada empleado encontrado
            for (Map.Entry<String, RolGeneral> entry : rolesMap.entrySet()) {
                String empCodigo = entry.getKey();
                RolGeneral rol = entry.getValue();
                
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