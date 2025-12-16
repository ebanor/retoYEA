package com.mikeldi.reto.service;

import com.mikeldi.reto.dto.ProductoDTO;
import com.mikeldi.reto.entity.Producto;
import com.mikeldi.reto.exception.BadRequestException;
import com.mikeldi.reto.exception.ResourceNotFoundException;
import com.mikeldi.reto.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Servicio que encapsula la lógica de negocio para gestión de productos
@Service
public class ProductoService {
    
    // Inyecta repositorio para acceso a datos de productos
    @Autowired
    private ProductoRepository productoRepository;
    
    // Crea un nuevo producto en el catálogo
    @Transactional
    public ProductoDTO crearProducto(ProductoDTO productoDTO) {
        // Convierte DTO a entidad para persistencia
        Producto producto = convertirDTOAEntidad(productoDTO);
        // Establece el producto como activo por defecto
        producto.setActivo(true);
        
        // Persiste en la base de datos
        Producto productoGuardado = productoRepository.save(producto);
        return convertirADTO(productoGuardado);
    }
    
    // Lista todos los productos del sistema incluyendo inactivos
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // Lista solo productos activos disponibles para venta
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarActivos() {
        // Filtra productos que están activos en el catálogo
        return productoRepository.findByActivo(true).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // Obtiene un producto específico por su ID
    @Transactional(readOnly = true)
    public ProductoDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        return convertirADTO(producto);
    }
    
    // Busca productos cuyo nombre contenga el texto especificado
    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarPorNombre(String nombre) {
        // Búsqueda parcial insensible a mayúsculas para autocompletado
        return productoRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // Filtra productos activos por categoría específica
    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarPorCategoria(String categoria) {
        // Solo retorna productos activos de la categoría solicitada
        return productoRepository.findByCategoriaAndActivo(categoria, true).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // Lista productos que requieren reposición (stock ≤ stockMínimo)
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarConStockBajo() {
        // Consulta JPQL que compara stockActual con stockMinimo
        // Útil para alertas de reposición en dashboards
        return productoRepository.findProductosConStockBajo().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // Obtiene lista única de todas las categorías existentes
    @Transactional(readOnly = true)
    public List<String> listarCategorias() {
        // Retorna lista de strings sin objetos Producto completos
        // Usado para poblar selectores y filtros en la interfaz
        return productoRepository.findAllCategorias();
    }
    
    // Actualiza todos los datos de un producto existente
    @Transactional
    public ProductoDTO actualizarProducto(Long id, ProductoDTO productoDTO) {
        // Verifica que el producto existe
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        
        // Actualiza todos los campos del producto
        producto.setNombre(productoDTO.getNombre());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setIva(productoDTO.getIva());
        producto.setStockActual(productoDTO.getStockActual());
        producto.setStockMinimo(productoDTO.getStockMinimo());
        producto.setCategoria(productoDTO.getCategoria());
        
        // Persiste los cambios
        Producto productoActualizado = productoRepository.save(producto);
        return convertirADTO(productoActualizado);
    }
    
    // Actualiza solo el stock de un producto sin modificar otros campos
    @Transactional
    public ProductoDTO actualizarStock(Long id, Integer nuevoStock) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        
        // Valida que el stock no sea negativo
        // Regla de negocio que previene valores inválidos
        if (nuevoStock < 0) {
            throw new BadRequestException("El stock no puede ser negativo");
        }
        
        // Actualiza solo el campo de stock
        producto.setStockActual(nuevoStock);
        Producto productoActualizado = productoRepository.save(producto);
        return convertirADTO(productoActualizado);
    }
    
    // Elimina un producto permanentemente del sistema
    @Transactional
    public void eliminarProducto(Long id) {
        // Verifica que el producto existe antes de eliminar
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        // Eliminación permanente (mejor práctica: usar cambiarEstado a inactivo)
        productoRepository.delete(producto);
    }
    
    // Activa o desactiva un producto sin eliminarlo
    @Transactional
    public ProductoDTO cambiarEstado(Long id, boolean activo) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        // Cambio de estado preserva histórico y permite reactivación
        producto.setActivo(activo);
        Producto productoActualizado = productoRepository.save(producto);
        return convertirADTO(productoActualizado);
    }
    
    // Convierte una entidad Producto a DTO para transferencia segura
    // Incluye el campo calculado isStockBajo() del método @Transient
    private ProductoDTO convertirADTO(Producto producto) {
        return new ProductoDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getIva(),
                producto.getStockActual(),
                producto.getStockMinimo(),
                producto.getCategoria(),
                producto.getActivo(),
                producto.isStockBajo(),  // Método calculado no persistido
                producto.getFechaCreacion()
        );
    }
    
    // Convierte un DTO a entidad Producto para persistencia
    // Solo mapea campos modificables por el usuario
    private Producto convertirDTOAEntidad(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setIva(dto.getIva());
        producto.setStockActual(dto.getStockActual());
        producto.setStockMinimo(dto.getStockMinimo());
        producto.setCategoria(dto.getCategoria());
        return producto;
    }
}
