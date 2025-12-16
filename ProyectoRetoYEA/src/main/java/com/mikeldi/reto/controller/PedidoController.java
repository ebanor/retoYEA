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

// Define este controlador como REST para retornar datos en formato JSON
@RestController
// Establece la ruta base /api/pedidos para todos los endpoints
@RequestMapping("/api/pedidos")
// Agrupa estos endpoints en Swagger bajo la categoría "Pedidos"
@Tag(name = "Pedidos", description = "Gestión de pedidos y líneas de pedido")
// Indica en Swagger que todos los endpoints requieren autenticación JWT
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {
    
    // Servicio para la lógica de negocio de pedidos
    @Autowired
    private PedidoService pedidoService;
    
    // Servicio para exportar datos a diferentes formatos
    @Autowired
    private ExportService exportService;
    
    // Endpoint GET para listar pedidos con filtro opcional por estado
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Listar todos los pedidos",
        description = "Retorna la lista completa de pedidos ordenados por fecha"
    )
    public ResponseEntity<List<PedidoDTO>> listarPedidos(
            // Parámetro opcional para filtrar por estado del pedido
            @RequestParam(required = false) String estado) {
        
        // Si se proporciona un estado, filtra los pedidos por ese estado
        if (estado != null && !estado.isEmpty()) {
            // Convierte el string a enum EstadoPedido
            EstadoPedido estadoEnum = EstadoPedido.valueOf(estado.toUpperCase());
            List<PedidoDTO> pedidos = pedidoService.listarPorEstado(estadoEnum);
            return ResponseEntity.ok(pedidos);
        }
        
        // Si no hay filtro, retorna todos los pedidos
        List<PedidoDTO> pedidos = pedidoService.listarTodos();
        return ResponseEntity.ok(pedidos);
    }
    
    // Endpoint GET para obtener un pedido específico con todas sus líneas
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
    
    // Endpoint GET para obtener el historial de pedidos de un cliente
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
    
    // Endpoint POST para crear un nuevo pedido con sus líneas
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Crear nuevo pedido",
        description = "Crea un pedido con sus líneas y calcula totales automáticamente"
    )
    public ResponseEntity<PedidoDTO> crearPedido(@Valid @RequestBody PedidoDTO pedidoDTO) {
        // Obtiene el contexto de autenticación del usuario actual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Extrae el email del usuario que crea el pedido
        String emailUsuario = auth.getName();
        
        // Crea el pedido y registra quién lo creó para auditoría
        PedidoDTO nuevoPedido = pedidoService.crearPedido(pedidoDTO, emailUsuario);
        // Retorna código 201 Created con el pedido creado
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido);
    }
    
    // Endpoint PATCH para actualizar solo el estado de un pedido
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Cambiar estado del pedido",
        description = "Actualiza el estado de un pedido (PENDIENTE, PAGADO, ENVIADO, CANCELADO)"
    )
    public ResponseEntity<PedidoDTO> cambiarEstado(
            @PathVariable Long id,
            // Enum que representa el nuevo estado del pedido
            @RequestParam EstadoPedido estado) {
        PedidoDTO pedido = pedidoService.cambiarEstado(id, estado);
        return ResponseEntity.ok(pedido);
    }
    
    // Endpoint DELETE para eliminar un pedido del sistema
    @DeleteMapping("/{id}")
    // Solo ADMIN puede eliminar pedidos
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Eliminar pedido",
        description = "Elimina un pedido (solo si está en estado PENDIENTE). Solo ADMIN"
    )
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        // La lógica del servicio valida que solo se eliminen pedidos PENDIENTES
        pedidoService.eliminarPedido(id);
        // Retorna código 204 No Content indicando éxito sin cuerpo
        return ResponseEntity.noContent().build();
    }
    
    // Endpoint GET para exportar la lista de pedidos a formato PDF
    @GetMapping("/export/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(summary = "Exportar pedidos a PDF")
    public ResponseEntity<byte[]> exportarPedidosPDF() throws DocumentException {
        // Obtiene todos los pedidos del sistema
        List<PedidoDTO> pedidos = pedidoService.listarTodos();
        // Genera el PDF como array de bytes usando iText
        byte[] pdfBytes = exportService.exportarPedidosPDF(pedidos);
        
        // Configura headers para descarga de archivo PDF
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=pedidos.pdf")
                .body(pdfBytes);
    }
    
    // Endpoint GET para exportar la lista de pedidos a formato CSV
    @GetMapping("/export/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(summary = "Exportar pedidos a CSV")
    public ResponseEntity<String> exportarPedidosCSV() throws IOException {
        // Obtiene todos los pedidos del sistema
        List<PedidoDTO> pedidos = pedidoService.listarTodos();
        // Genera el CSV como String
        String csv = exportService.exportarPedidosCSV(pedidos);
        
        // Configura headers para descarga de archivo CSV con UTF-8
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv; charset=utf-8")
                .header("Content-Disposition", "attachment; filename=pedidos.csv")
                .body(csv);
    }
}
