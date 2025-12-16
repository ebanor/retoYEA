package com.mikeldi.reto.controller;

import com.itextpdf.text.DocumentException;
import com.mikeldi.reto.dto.FacturaDTO;
import com.mikeldi.reto.entity.EstadoFactura;
import com.mikeldi.reto.service.ExportService;
import com.mikeldi.reto.service.FacturaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

// Define este controlador como REST para retornar datos en formato JSON
@RestController
// Establece la ruta base /api/facturas para todos los endpoints
@RequestMapping("/api/facturas")
// Agrupa estos endpoints en Swagger bajo la categoría "Facturas"
@Tag(name = "Facturas", description = "Gestión de facturas y facturación")
// Indica en Swagger que todos los endpoints requieren autenticación JWT
@SecurityRequirement(name = "bearerAuth")
public class FacturaController {
    
    // Servicio para la lógica de negocio de facturas
    @Autowired
    private FacturaService facturaService;
    
    // Servicio para exportar datos a diferentes formatos
    @Autowired
    private ExportService exportService;
    
    // Endpoint GET para listar facturas con filtro opcional por estado
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Listar todas las facturas",
        description = "Retorna la lista completa de facturas ordenadas por fecha de emisión"
    )
    public ResponseEntity<List<FacturaDTO>> listarFacturas(
            // Parámetro opcional para filtrar por estado
            @RequestParam(required = false) String estado) {
        
        // Si se proporciona un estado, filtra las facturas por ese estado
        if (estado != null && !estado.isEmpty()) {
            // Convierte el string a enum EstadoFactura
            EstadoFactura estadoEnum = EstadoFactura.valueOf(estado.toUpperCase());
            List<FacturaDTO> facturas = facturaService.listarPorEstado(estadoEnum);
            return ResponseEntity.ok(facturas);
        }
        
        // Si no hay filtro, retorna todas las facturas
        List<FacturaDTO> facturas = facturaService.listarTodas();
        return ResponseEntity.ok(facturas);
    }
    
    // Endpoint GET para obtener una factura específica por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Obtener factura por ID",
        description = "Retorna los datos completos de una factura"
    )
    public ResponseEntity<FacturaDTO> obtenerFactura(@PathVariable Long id) {
        FacturaDTO factura = facturaService.obtenerPorId(id);
        return ResponseEntity.ok(factura);
    }
    
    // Endpoint GET para obtener todas las facturas de un cliente específico
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Listar facturas por cliente",
        description = "Retorna el historial de facturas de un cliente"
    )
    public ResponseEntity<List<FacturaDTO>> listarPorCliente(@PathVariable Long clienteId) {
        List<FacturaDTO> facturas = facturaService.listarPorCliente(clienteId);
        return ResponseEntity.ok(facturas);
    }
    
    // Endpoint POST para crear y emitir una nueva factura
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Emitir nueva factura",
        description = "Crea una factura a partir de un pedido pagado y descuenta el stock"
    )
    public ResponseEntity<FacturaDTO> emitirFactura(@Valid @RequestBody FacturaDTO facturaDTO) {
        // Obtiene el contexto de autenticación del usuario actual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Extrae el email del usuario autenticado
        String emailUsuario = auth.getName();
        
        // Emite la factura y registra quién la creó
        FacturaDTO nuevaFactura = facturaService.emitirFactura(facturaDTO, emailUsuario);
        // Retorna código 201 Created con la factura emitida
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFactura);
    }
    
    // Endpoint PATCH para actualizar solo el estado de una factura
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Cambiar estado de la factura",
        description = "Actualiza el estado de una factura (PENDIENTE, PAGADA, VENCIDA, CANCELADA)"
    )
    public ResponseEntity<FacturaDTO> cambiarEstado(
            @PathVariable Long id,
            // Enum que representa el nuevo estado de la factura
            @RequestParam EstadoFactura estado) {
        FacturaDTO factura = facturaService.cambiarEstado(id, estado);
        return ResponseEntity.ok(factura);
    }
    
    // Endpoint GET para exportar la lista de facturas a formato PDF
    @GetMapping("/export/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(summary = "Exportar facturas a PDF")
    public ResponseEntity<byte[]> exportarFacturasPDF() throws DocumentException {
        // Obtiene todas las facturas
        List<FacturaDTO> facturas = facturaService.listarTodas();
        // Genera el PDF como array de bytes usando iText
        byte[] pdfBytes = exportService.exportarFacturasPDF(facturas);
        
        // Configura headers para descarga de archivo PDF
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=facturas.pdf")
                .body(pdfBytes);
    }
    
    // Endpoint GET para exportar la lista de facturas a formato CSV
    @GetMapping("/export/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(summary = "Exportar facturas a CSV")
    public ResponseEntity<String> exportarFacturasCSV() throws IOException {
        // Obtiene todas las facturas
        List<FacturaDTO> facturas = facturaService.listarTodas();
        // Genera el CSV como String
        String csv = exportService.exportarFacturasCSV(facturas);
        
        // Configura headers para descarga de archivo CSV con UTF-8
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv; charset=utf-8")
                .header("Content-Disposition", "attachment; filename=facturas.csv")
                .body(csv);
    }
}
