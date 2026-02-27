package Jasper;

import Jasper.service.JasperReportExample;
import Jasper.service.ExtractDetails1_Fixed;
import Jasper.model.RolGeneral;
import Jasper.config.DatabaseConnection1;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.util.Map;

/**
 * Genera un PDF de rol de pagos por cada empleado encontrado.
 * Los parámetros se leen desde el archivo .env
 */
public class Main1 {

    public static void main(String[] args) {
        try {
            // Leer parámetros desde el .env
            Dotenv dotenv = Dotenv.load();
            String ciaCode = dotenv.get("DEFAULT_CIA_CODE");
            String tipoRol = dotenv.get("DEFAULT_TIPO_ROL");
            String codInforme = dotenv.get("DEFAULT_COD_INFORME");
            String codCen = dotenv.get("DEFAULT_COD_CEN");
            String empInicial = dotenv.get("DEFAULT_EMP_INICIAL");
            String empFinal = dotenv.get("DEFAULT_EMP_FINAL");

            // Conexión a la base de datos (credenciales también vienen del .env)
            Connection connection = DatabaseConnection1.getConnection();

            System.out.println("Iniciando generación de reportes...");

            // Obtener los roles para el rango de empleados indicado
            Map<String, RolGeneral> rolesMap = ExtractDetails1_Fixed.getAllRolesByParametros(
                    connection, empInicial, empFinal, ciaCode, tipoRol, codInforme, codCen);

            System.out.println("Empleados encontrados: " + rolesMap.size());

            // Generar un PDF por cada empleado
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