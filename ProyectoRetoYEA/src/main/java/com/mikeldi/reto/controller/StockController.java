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

@RestController
@RequestMapping("/api/stock")
@Tag(name = "Stock", description = "Gestión de movimientos de stock")
@SecurityRequirement(name = "bearerAuth")
public class StockController {
    
    @Autowired
    private StockService stockService;
    
    @GetMapping("/movimientos")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMACEN')")
    @Operation(
        summary = "Listar últimos movimientos",
        description = "Retorna los últimos 20 movimientos de stock"
    )
    public ResponseEntity<List<MovimientoStockDTO>> listarMovimientos() {
        List<MovimientoStockDTO> movimientos = stockService.listarUltimosMovimientos();
        return ResponseEntity.ok(movimientos);
    }
    
    @GetMapping("/movimientos/producto/{productoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMACEN')")
    @Operation(
        summary = "Historial de movimientos por producto",
        description = "Retorna todos los movimientos de stock de un producto específico"
    )
    public ResponseEntity<List<MovimientoStockDTO>> listarMovimientosPorProducto(
            @PathVariable Long productoId) {
        List<MovimientoStockDTO> movimientos = stockService.listarPorProducto(productoId);
        return ResponseEntity.ok(movimientos);
    }
    
    @PostMapping("/movimientos")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMACEN')")
    @Operation(
        summary = "Registrar movimiento de stock manual",
        description = "Registra una entrada, salida o ajuste de stock manual"
    )
    public ResponseEntity<MovimientoStockDTO> registrarMovimiento(
            @Valid @RequestBody MovimientoStockDTO movimientoDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuario = auth.getName();
        
        MovimientoStockDTO nuevoMovimiento = stockService.registrarMovimiento(movimientoDTO, emailUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMovimiento);
    }
}
