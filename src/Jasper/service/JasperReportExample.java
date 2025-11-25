/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Jasper.service;

/**
 *
 * @author PC
 */
import Jasper.model.RolGeneral;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import java.util.*;

public class JasperReportExample {

    public static void generarReporte(RolGeneral rolGeneral, String fileName) {
        try {
            // Ruta del archivo .jasper compilado
            String jasperPath = "src/Jasper/resources/jasperRolReport.jasper";
            //ruta del logo de la empresa
            String imagePath = "src/Jasper/resources/SampleIconImage.png";
            

            rolGeneral.sumarIngresos();
            rolGeneral.sumarEgresos();
            rolGeneral.sumarDescuentos();

            // Convertir el objeto en una lista (Jasper necesita una colección)
            List<RolGeneral> listaRol = Collections.singletonList(rolGeneral);

            // Crear la fuente de datos para Jasper
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listaRol);
            // Crear datasources para las listas internas (suponiendo que getIngresos() y getEgresos() devuelven List<DetalleRol>)
            JRBeanCollectionDataSource ingresosDataSource = new JRBeanCollectionDataSource(rolGeneral.getIngresos());
            JRBeanCollectionDataSource egresosDataSource = new JRBeanCollectionDataSource(rolGeneral.getEgresos());
            JRBeanCollectionDataSource descuentosDataSource = new JRBeanCollectionDataSource(rolGeneral.getDescuentos());

            // Parámetros adicionales (opcional)
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("REPORT_LOCALE", Locale.US); // Pasar el locale como parámetro
            //parameters.put("titulo", "Reporte de Rol General");

            // Pasar las listas como parámetros
            parameters.put("ingresosDataSource", ingresosDataSource);
            parameters.put("egresosDataSource", egresosDataSource);
            parameters.put("descuentosDataSource", descuentosDataSource);
            //pasar la direccion del logo como parametro
            //parameters.put("ImagePath", imagePath);

            // Llenar el reporte con los datos de rolGeneral
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, parameters, dataSource);

            // Exportar a PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);

            System.out.println("Reporte " + fileName + " generado con éxito.");

        } catch (JRException e) {
            e.printStackTrace();
        }
    }
}
