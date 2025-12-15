package com.mikeldi.reto.controller;

import com.itextpdf.text.DocumentException;
import com.mikeldi.reto.dto.PedidoDTO;
import com.mikeldi.reto.entity.EstadoPedido;
import com.mikeldi.reto.service.ExportService;
import com.mikeldi.reto.service.PedidoService;
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
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Gestión de pedidos y líneas de pedido")
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {
    
    @Autowired
    private PedidoService pedidoService;
    
    @Autowired
    private ExportService exportService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Listar todos los pedidos",
        description = "Retorna la lista completa de pedidos ordenados por fecha"
    )
    public ResponseEntity<List<PedidoDTO>> listarPedidos(
            @RequestParam(required = false) String estado) {
        
        if (estado != null && !estado.isEmpty()) {
            EstadoPedido estadoEnum = EstadoPedido.valueOf(estado.toUpperCase());
            List<PedidoDTO> pedidos = pedidoService.listarPorEstado(estadoEnum);
            return ResponseEntity.ok(pedidos);
        }
        
        List<PedidoDTO> pedidos = pedidoService.listarTodos();
        return ResponseEntity.ok(pedidos);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Obtener pedido por ID",
        description = "Retorna los datos completos de un pedido con sus líneas"
    )
    public ResponseEntity<PedidoDTO> obtenerPedido(@PathVariable Long id) {
        PedidoDTO pedido = pedidoService.obtenerPorId(id);
        return ResponseEntity.ok(pedido);
    }
    
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Listar pedidos por cliente",
        description = "Retorna el historial de pedidos de un cliente"
    )
    public ResponseEntity<List<PedidoDTO>> listarPorCliente(@PathVariable Long clienteId) {
        List<PedidoDTO> pedidos = pedidoService.listarPorCliente(clienteId);
        return ResponseEntity.ok(pedidos);
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Crear nuevo pedido",
        description = "Crea un pedido con sus líneas y calcula totales automáticamente"
    )
    public ResponseEntity<PedidoDTO> crearPedido(@Valid @RequestBody PedidoDTO pedidoDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuario = auth.getName();
        
        PedidoDTO nuevoPedido = pedidoService.crearPedido(pedidoDTO, emailUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido);
    }
    
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Cambiar estado del pedido",
        description = "Actualiza el estado de un pedido (PENDIENTE, PAGADO, ENVIADO, CANCELADO)"
    )
    public ResponseEntity<PedidoDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPedido estado) {
        PedidoDTO pedido = pedidoService.cambiarEstado(id, estado);
        return ResponseEntity.ok(pedido);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Eliminar pedido",
        description = "Elimina un pedido (solo si está en estado PENDIENTE). Solo ADMIN"
    )
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        pedidoService.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }
    
    // ============== EXPORTACIÓN ==============
    
    @GetMapping("/export/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(summary = "Exportar pedidos a PDF")
    public ResponseEntity<byte[]> exportarPedidosPDF() throws DocumentException {
        List<PedidoDTO> pedidos = pedidoService.listarTodos();
        byte[] pdfBytes = exportService.exportarPedidosPDF(pedidos);
        
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=pedidos.pdf")
                .body(pdfBytes);
    }
    
    @GetMapping("/export/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(summary = "Exportar pedidos a CSV")
    public ResponseEntity<String> exportarPedidosCSV() throws IOException {
        List<PedidoDTO> pedidos = pedidoService.listarTodos();
        String csv = exportService.exportarPedidosCSV(pedidos);
        
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv; charset=utf-8")
                .header("Content-Disposition", "attachment; filename=pedidos.csv")
                .body(csv);
    }
}
