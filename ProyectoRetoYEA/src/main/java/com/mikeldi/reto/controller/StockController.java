package com.mikeldi.reto.controller;

import com.mikeldi.reto.dto.MovimientoStockDTO;
import com.mikeldi.reto.service.StockService;
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

import java.util.List;

// Define este controlador como REST para retornar datos en formato JSON
@RestController
// Establece la ruta base /api/stock para todos los endpoints
@RequestMapping("/api/stock")
// Agrupa estos endpoints en Swagger bajo la categoría "Stock"
@Tag(name = "Stock", description = "Gestión de movimientos de stock")
// Indica en Swagger que todos los endpoints requieren autenticación JWT
@SecurityRequirement(name = "bearerAuth")
public class StockController {
    
    // Servicio para la lógica de negocio de movimientos de stock
    @Autowired
    private StockService stockService;
    
    // Endpoint GET para consultar los últimos movimientos de stock
    @GetMapping("/movimientos")
    // Solo ADMIN y ALMACEN pueden ver movimientos de stock
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMACEN')")
    @Operation(
        summary = "Listar últimos movimientos",
        description = "Retorna los últimos 20 movimientos de stock"
    )
    public ResponseEntity<List<MovimientoStockDTO>> listarMovimientos() {
        // Retorna el historial reciente para ver actividad del almacén
        List<MovimientoStockDTO> movimientos = stockService.listarUltimosMovimientos();
        return ResponseEntity.ok(movimientos);
    }
    
    // Endpoint GET para consultar el historial completo de un producto específico
    @GetMapping("/movimientos/producto/{productoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMACEN')")
    @Operation(
        summary = "Historial de movimientos por producto",
        description = "Retorna todos los movimientos de stock de un producto específico"
    )
    public ResponseEntity<List<MovimientoStockDTO>> listarMovimientosPorProducto(
            // ID del producto a consultar
            @PathVariable Long productoId) {
        // Útil para auditar entradas/salidas de un producto en particular
        List<MovimientoStockDTO> movimientos = stockService.listarPorProducto(productoId);
        return ResponseEntity.ok(movimientos);
    }
    
    // Endpoint POST para registrar movimientos manuales de stock
    @PostMapping("/movimientos")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMACEN')")
    @Operation(
        summary = "Registrar movimiento de stock manual",
        description = "Registra una entrada, salida o ajuste de stock manual"
    )
    public ResponseEntity<MovimientoStockDTO> registrarMovimiento(
            @Valid @RequestBody MovimientoStockDTO movimientoDTO) {
        // Obtiene el contexto de autenticación del usuario actual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Extrae el email del usuario que realiza el movimiento
        String emailUsuario = auth.getName();
        
        // Registra el movimiento y vincula al usuario para trazabilidad
        MovimientoStockDTO nuevoMovimiento = stockService.registrarMovimiento(movimientoDTO, emailUsuario);
        // Retorna código 201 Created con el movimiento registrado
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMovimiento);
    }
}
