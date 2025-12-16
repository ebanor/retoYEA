package com.mikeldi.reto.controller;

import com.mikeldi.reto.dto.EstadisticasDTO;
import com.mikeldi.reto.dto.ReporteVentasDTO;
import com.mikeldi.reto.service.ReportesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// Define este controlador como REST para retornar datos en formato JSON
@RestController
// Establece la ruta base /api/reportes para todos los endpoints
@RequestMapping("/api/reportes")
// Agrupa estos endpoints en Swagger bajo la categoría "Reportes"
@Tag(name = "Reportes", description = "Reportes y estadísticas del sistema")
// Indica en Swagger que todos los endpoints requieren autenticación JWT
@SecurityRequirement(name = "bearerAuth")
public class ReportesController {
    
    // Servicio para generar reportes y estadísticas del sistema
    @Autowired
    private ReportesService reportesService;
    
    // Endpoint GET para obtener un resumen general del sistema
    @GetMapping("/estadisticas")
    // Solo ADMIN y COMERCIAL pueden ver estadísticas
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Obtener estadísticas generales",
        description = "Retorna estadísticas generales del sistema (clientes, productos, ventas, etc.)"
    )
    public ResponseEntity<EstadisticasDTO> obtenerEstadisticas() {
        // Retorna métricas clave del negocio en un solo objeto
        EstadisticasDTO estadisticas = reportesService.obtenerEstadisticas();
        return ResponseEntity.ok(estadisticas);
    }
    
    // Endpoint GET para generar reportes de ventas por rango de fechas
    @GetMapping("/ventas")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Reporte de ventas por período",
        description = "Retorna las ventas agrupadas por fecha en un rango específico"
    )
    public ResponseEntity<List<ReporteVentasDTO>> reporteVentas(
            // Fecha de inicio del período a consultar
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            // Fecha de fin del período a consultar
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        // Genera el reporte agregando ventas por día en el rango especificado
        List<ReporteVentasDTO> reporte = reportesService.reporteVentasPorPeriodo(inicio, fin);
        return ResponseEntity.ok(reporte);
    }
}
