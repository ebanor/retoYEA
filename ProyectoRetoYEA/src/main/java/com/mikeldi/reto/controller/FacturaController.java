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

@RestController
@RequestMapping("/api/facturas")
@Tag(name = "Facturas", description = "Gestión de facturas y facturación")
@SecurityRequirement(name = "bearerAuth")
public class FacturaController {
    
    @Autowired
    private FacturaService facturaService;
    
    @Autowired
    private ExportService exportService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Listar todas las facturas",
        description = "Retorna la lista completa de facturas ordenadas por fecha de emisión"
    )
    public ResponseEntity<List<FacturaDTO>> listarFacturas(
            @RequestParam(required = false) String estado) {
        
        if (estado != null && !estado.isEmpty()) {
            EstadoFactura estadoEnum = EstadoFactura.valueOf(estado.toUpperCase());
            List<FacturaDTO> facturas = facturaService.listarPorEstado(estadoEnum);
            return ResponseEntity.ok(facturas);
        }
        
        List<FacturaDTO> facturas = facturaService.listarTodas();
        return ResponseEntity.ok(facturas);
    }
    
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
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Emitir nueva factura",
        description = "Crea una factura a partir de un pedido pagado y descuenta el stock"
    )
    public ResponseEntity<FacturaDTO> emitirFactura(@Valid @RequestBody FacturaDTO facturaDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuario = auth.getName();
        
        FacturaDTO nuevaFactura = facturaService.emitirFactura(facturaDTO, emailUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFactura);
    }
    
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Cambiar estado de la factura",
        description = "Actualiza el estado de una factura (PENDIENTE, PAGADA, VENCIDA, CANCELADA)"
    )
    public ResponseEntity<FacturaDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoFactura estado) {
        FacturaDTO factura = facturaService.cambiarEstado(id, estado);
        return ResponseEntity.ok(factura);
    }
    
    // ============== EXPORTACIÓN ==============
    
    @GetMapping("/export/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(summary = "Exportar facturas a PDF")
    public ResponseEntity<byte[]> exportarFacturasPDF() throws DocumentException {
        List<FacturaDTO> facturas = facturaService.listarTodas();
        byte[] pdfBytes = exportService.exportarFacturasPDF(facturas);
        
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=facturas.pdf")
                .body(pdfBytes);
    }
    
    @GetMapping("/export/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(summary = "Exportar facturas a CSV")
    public ResponseEntity<String> exportarFacturasCSV() throws IOException {
        List<FacturaDTO> facturas = facturaService.listarTodas();
        String csv = exportService.exportarFacturasCSV(facturas);
        
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv; charset=utf-8")
                .header("Content-Disposition", "attachment; filename=facturas.csv")
                .body(csv);
    }
}
