package com.mikeldi.reto.controller;

import com.mikeldi.reto.dto.ProductoDTO;
import com.mikeldi.reto.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Gestión de productos y control de stock")
@SecurityRequirement(name = "bearerAuth")
public class ProductoController {
    
    @Autowired
    private ProductoService productoService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL', 'ALMACEN')")
    @Operation(
        summary = "Listar productos",
        description = "Retorna la lista de productos. Puede filtrar por nombre o categoría"
    )
    public ResponseEntity<List<ProductoDTO>> listarProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String categoria) {
        
        if (nombre != null && !nombre.isEmpty()) {
            List<ProductoDTO> productos = productoService.buscarPorNombre(nombre);
            return ResponseEntity.ok(productos);
        }
        
        if (categoria != null && !categoria.isEmpty()) {
            List<ProductoDTO> productos = productoService.buscarPorCategoria(categoria);
            return ResponseEntity.ok(productos);
        }
        
        List<ProductoDTO> productos = productoService.listarTodos();
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL', 'ALMACEN')")
    @Operation(
        summary = "Listar productos activos",
        description = "Retorna solo los productos activos disponibles"
    )
    public ResponseEntity<List<ProductoDTO>> listarProductosActivos() {
        List<ProductoDTO> productos = productoService.listarActivos();
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/stock-bajo")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMACEN')")
    @Operation(
        summary = "Listar productos con stock bajo",
        description = "Retorna productos cuyo stock actual es menor o igual al stock mínimo"
    )
    public ResponseEntity<List<ProductoDTO>> listarProductosStockBajo() {
        List<ProductoDTO> productos = productoService.listarConStockBajo();
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/categorias")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL', 'ALMACEN')")
    @Operation(
        summary = "Listar categorías",
        description = "Retorna todas las categorías de productos disponibles"
    )
    public ResponseEntity<List<String>> listarCategorias() {
        List<String> categorias = productoService.listarCategorias();
        return ResponseEntity.ok(categorias);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL', 'ALMACEN')")
    @Operation(
        summary = "Obtener producto por ID",
        description = "Retorna los datos completos de un producto específico"
    )
    public ResponseEntity<ProductoDTO> obtenerProducto(@PathVariable Long id) {
        ProductoDTO producto = productoService.obtenerPorId(id);
        return ResponseEntity.ok(producto);
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMACEN')")
    @Operation(
        summary = "Crear nuevo producto",
        description = "Registra un nuevo producto con precio, IVA y stock. Solo ADMIN y ALMACEN"
    )
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        ProductoDTO nuevoProducto = productoService.crearProducto(productoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMACEN')")
    @Operation(
        summary = "Actualizar producto",
        description = "Actualiza los datos de un producto existente. Solo ADMIN y ALMACEN"
    )
    public ResponseEntity<ProductoDTO> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoDTO productoDTO) {
        ProductoDTO productoActualizado = productoService.actualizarProducto(id, productoDTO);
        return ResponseEntity.ok(productoActualizado);
    }
    
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMACEN')")
    @Operation(
        summary = "Actualizar stock del producto",
        description = "Actualiza solo el stock actual de un producto. Solo ADMIN y ALMACEN"
    )
    public ResponseEntity<ProductoDTO> actualizarStock(
            @PathVariable Long id,
            @RequestParam Integer stock) {
        ProductoDTO producto = productoService.actualizarStock(id, stock);
        return ResponseEntity.ok(producto);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Eliminar producto",
        description = "Elimina un producto del sistema. Solo ADMIN"
    )
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Cambiar estado del producto",
        description = "Activa o desactiva un producto. Solo ADMIN"
    )
    public ResponseEntity<ProductoDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo) {
        ProductoDTO producto = productoService.cambiarEstado(id, activo);
        return ResponseEntity.ok(producto);
    }
}
