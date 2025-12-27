package Jasper.controller;

import Jasper.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/rol-pagos")
    public ResponseEntity<String> generarRolPagos(
            @RequestParam(defaultValue = "001") String codInforme,
            @RequestParam(defaultValue = "01") String ciaCode,
            @RequestParam(defaultValue = "001") String tipoRol,
            @RequestParam(defaultValue = "000") String empInicial,
            @RequestParam(defaultValue = "999999") String empFinal,
            @RequestParam(defaultValue = "000") String codCen) {
        
        try {
            String resultado = reportService.generarReporte(
                codInforme, ciaCode, tipoRol, empInicial, empFinal, codCen);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error generando reporte: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Servicio de reportes activo");
    }
}
