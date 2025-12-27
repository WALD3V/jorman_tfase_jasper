package Jasper.service;

import Jasper.config.DatabaseConnection1;
import Jasper.model.RolGeneral;
import org.springframework.stereotype.Service;
import java.sql.Connection;
import java.util.Map;

@Service
public class ReportService {

    public String generarReporte(String codInforme, String ciaCode, String tipoRol, 
                               String empInicial, String empFinal, String codCen) {
        try {
            Connection connection = DatabaseConnection1.getConnection();
            
            if (connection == null) {
                return "Error: No se pudo conectar a la base de datos";
            }

            // Usar la lógica existente
            Map<String, RolGeneral> roles = ExtractDetails1_Fixed.getAllRolesByParametros(
                connection, empInicial, empFinal, ciaCode, tipoRol, codInforme, codCen);
            
            connection.close();
            
            return "Reporte generado exitosamente. Empleados procesados: " + roles.size();
            
        } catch (Exception e) {
            return "Error generando reporte: " + e.getMessage();
        }
    }
}
