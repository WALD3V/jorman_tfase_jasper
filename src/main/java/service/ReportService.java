package Jasper.service;

import Jasper.model.DetalleRol;
import Jasper.model.RolGeneral;
import Jasper.config.DatabaseConnection1;
import org.springframework.stereotype.Service;
import java.sql.Connection;
import java.util.Map;
import java.util.HashMap;
import java.util.Base64;
import java.io.ByteArrayOutputStream;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ReportService {

    public String generarReporte(String codInforme, String ciaCode, String tipoRol, 
                               String empInicial, String empFinal, String codCen) {
        try {
            Connection connection = DatabaseConnection1.getConnection();
            
            System.out.println("=== PARÁMETROS RECIBIDOS ===");
            System.out.println("codInforme: '" + codInforme + "'");
            System.out.println("ciaCode: '" + ciaCode + "'");
            System.out.println("tipoRol: '" + tipoRol + "'");
            System.out.println("empInicial: '" + empInicial + "'");
            System.out.println("empFinal: '" + empFinal + "'");
            System.out.println("codCen: '" + codCen + "'");
            
            Map<String, RolGeneral> rolesMap = ExtractDetails1_Fixed.getAllRolesByParametros(
                connection, empInicial, empFinal, ciaCode, tipoRol, codInforme, codCen
            );
            
            System.out.println("Empleados encontrados: " + rolesMap.size());
            
            // Siempre devolver JSON estructurado
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("total", rolesMap.size());
            resultado.put("reportes", new HashMap<String, Object>());
            
            Map<String, Object> reportes = (Map<String, Object>) resultado.get("reportes");
            int index = 0;
            
            for (Map.Entry<String, RolGeneral> entry : rolesMap.entrySet()) {
                String empCodigo = entry.getKey();
                RolGeneral rol = entry.getValue();
                
                try {
                    ByteArrayOutputStream pdfStream = JasperReportExample.generarReporteEnMemoria(rol);
                    String pdfBase64 = Base64.getEncoder().encodeToString(pdfStream.toByteArray());
                    
                    Map<String, Object> empleadoData = new HashMap<>();
                    empleadoData.put("empCodigo", empCodigo);
                    empleadoData.put("nombres", rol.getEmpNombres());
                    empleadoData.put("apellidos", rol.getEmpApellidos());
                    empleadoData.put("pdf", pdfBase64);
                    empleadoData.put("tamaño", pdfStream.size());
                    
                    reportes.put(String.valueOf(index), empleadoData);
                    index++;
                    
                    System.out.println("PDF generado para empleado: " + empCodigo);
                    
                } catch (Exception e) {
                    Map<String, Object> errorData = new HashMap<>();
                    errorData.put("empCodigo", empCodigo);
                    errorData.put("error", e.getMessage());
                    
                    reportes.put(String.valueOf(index), errorData);
                    index++;
                    
                    System.err.println("Error generando PDF para empleado " + empCodigo + ": " + e.getMessage());
                }
            }
            
            // Convertir a JSON
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(resultado);
            
        } catch (Exception e) {
            System.err.println("Error en la generación de reportes: " + e.getMessage());
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }
}
