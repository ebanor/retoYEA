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

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "Reportes y estadísticas del sistema")
@SecurityRequirement(name = "bearerAuth")
public class ReportesController {
    
    @Autowired
    private ReportesService reportesService;
    
    @GetMapping("/estadisticas")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Obtener estadísticas generales",
        description = "Retorna estadísticas generales del sistema (clientes, productos, ventas, etc.)"
    )
    public ResponseEntity<EstadisticasDTO> obtenerEstadisticas() {
        EstadisticasDTO estadisticas = reportesService.obtenerEstadisticas();
        return ResponseEntity.ok(estadisticas);
    }
    
    @GetMapping("/ventas")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Reporte de ventas por período",
        description = "Retorna las ventas agrupadas por fecha en un rango específico"
    )
    public ResponseEntity<List<ReporteVentasDTO>> reporteVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        List<ReporteVentasDTO> reporte = reportesService.reporteVentasPorPeriodo(inicio, fin);
        return ResponseEntity.ok(reporte);
    }
}
