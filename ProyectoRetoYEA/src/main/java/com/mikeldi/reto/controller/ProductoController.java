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

// Define este controlador como REST para retornar datos en formato JSON
@RestController
// Establece la ruta base /api/productos para todos los endpoints
@RequestMapping("/api/productos")
// Agrupa estos endpoints en Swagger bajo la categoría "Productos"
@Tag(name = "Productos", description = "Gestión de productos y control de stock")
// Indica en Swagger que todos los endpoints requieren autenticación JWT
@SecurityRequirement(name = "bearerAuth")
public class ProductoController {
    
    // Servicio para la lógica de negocio de productos
    @Autowired
    private ProductoService productoService;
    
    // Endpoint GET para listar productos con filtros opcionales
    @GetMapping
    // Tres roles tienen acceso: ADMIN, COMERCIAL y ALMACEN
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL', 'ALMACEN')")
    @Operation(
        summary = "Listar productos",
        description = "Retorna la lista de productos. Puede filtrar por nombre o categoría"
    )
    public ResponseEntity<List<ProductoDTO>> listarProductos(
            // Parámetro opcional para buscar por nombre
            @RequestParam(required = false) String nombre,
            // Parámetro opcional para filtrar por categoría
            @RequestParam(required = false) String categoria) {
        
        // Si se proporciona nombre, busca productos que coincidan
        if (nombre != null && !nombre.isEmpty()) {
            List<ProductoDTO> productos = productoService.buscarPorNombre(nombre);
            return ResponseEntity.ok(productos);
        }
        
        // Si se proporciona categoría, filtra por esa categoría
        if (categoria != null && !categoria.isEmpty()) {
            List<ProductoDTO> productos = productoService.buscarPorCategoria(categoria);
            return ResponseEntity.ok(productos);
        }
        
        // Si no hay filtros, retorna todos los productos
        List<ProductoDTO> productos = productoService.listarTodos();
        return ResponseEntity.ok(productos);
    }
    
    // Endpoint GET para listar solo productos activos
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
    
    // Endpoint GET para alertas de stock bajo
    @GetMapping("/stock-bajo")
    // Solo ADMIN y ALMACEN pueden ver alertas de stock
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMACEN')")
    @Operation(
        summary = "Listar productos con stock bajo",
        description = "Retorna productos cuyo stock actual es menor o igual al stock mínimo"
    )
    public ResponseEntity<List<ProductoDTO>> listarProductosStockBajo() {
        // Identifica productos que necesitan reposición urgente
        List<ProductoDTO> productos = productoService.listarConStockBajo();
        return ResponseEntity.ok(productos);
    }
    
    // Endpoint GET para obtener lista de categorías disponibles
    @GetMapping("/categorias")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL', 'ALMACEN')")
    @Operation(
        summary = "Listar categorías",
        description = "Retorna todas las categorías de productos disponibles"
    )
    public ResponseEntity<List<String>> listarCategorias() {
        // Útil para poblar selectores en interfaces de usuario
        List<String> categorias = productoService.listarCategorias();
        return ResponseEntity.ok(categorias);
    }
    
    // Endpoint GET para obtener un producto específico por ID
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
    
    // Endpoint POST para crear un nuevo producto en el catálogo
    @PostMapping
    // Solo ADMIN y ALMACEN pueden crear productos
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMACEN')")
    @Operation(
        summary = "Crear nuevo producto",
        description = "Registra un nuevo producto con precio, IVA y stock. Solo ADMIN y ALMACEN"
    )
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        ProductoDTO nuevoProducto = productoService.crearProducto(productoDTO);
        // Retorna código 201 Created con el producto creado
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }
    
    // Endpoint PUT para actualizar todos los datos de un producto
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
    
    // Endpoint PATCH para actualizar solo el stock sin modificar otros campos
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMACEN')")
    @Operation(
        summary = "Actualizar stock del producto",
        description = "Actualiza solo el stock actual de un producto. Solo ADMIN y ALMACEN"
    )
    public ResponseEntity<ProductoDTO> actualizarStock(
            @PathVariable Long id,
            // Nueva cantidad de stock disponible
            @RequestParam Integer stock) {
        // Útil para entradas y ajustes de inventario rápidos
        ProductoDTO producto = productoService.actualizarStock(id, stock);
        return ResponseEntity.ok(producto);
    }
    
    // Endpoint DELETE para eliminar permanentemente un producto
    @DeleteMapping("/{id}")
    // Solo ADMIN puede eliminar productos del sistema
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Eliminar producto",
        description = "Elimina un producto del sistema. Solo ADMIN"
    )
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        // Retorna código 204 No Content indicando éxito sin cuerpo
        return ResponseEntity.noContent().build();
    }
    
    // Endpoint PATCH para activar o desactivar un producto
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Cambiar estado del producto",
        description = "Activa o desactiva un producto. Solo ADMIN"
    )
    public ResponseEntity<ProductoDTO> cambiarEstado(
            @PathVariable Long id,
            // Booleano para el nuevo estado activo/inactivo
            @RequestParam boolean activo) {
        // Desactivar es preferible a eliminar para mantener historial
        ProductoDTO producto = productoService.cambiarEstado(id, activo);
        return ResponseEntity.ok(producto);
    }
}
