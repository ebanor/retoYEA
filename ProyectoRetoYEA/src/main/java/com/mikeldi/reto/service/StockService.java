package com.mikeldi.reto.service;

import com.mikeldi.reto.dto.MovimientoStockDTO;
import com.mikeldi.reto.entity.*;
import com.mikeldi.reto.exception.BadRequestException;
import com.mikeldi.reto.exception.ResourceNotFoundException;
import com.mikeldi.reto.repository.MovimientoStockRepository;
import com.mikeldi.reto.repository.ProductoRepository;
import com.mikeldi.reto.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Servicio especializado en gestión de inventario y trazabilidad de movimientos de stock
@Service
public class StockService {
    
    // Inyecta repositorio para registrar historial de movimientos de stock
    @Autowired
    private MovimientoStockRepository movimientoRepository;
    
    // Inyecta repositorio para actualizar cantidades de productos
    @Autowired
    private ProductoRepository productoRepository;
    
    // Inyecta repositorio para identificar quien realiza los movimientos
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    // Descuenta stock automáticamente al facturar un pedido, creando trazabilidad completa
    @Transactional
    public void descontarStockPorPedido(Pedido pedido, String emailUsuario) {
        // Obtiene el usuario que ejecuta la operación para auditoría
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", emailUsuario));
        
        // Procesa cada línea del pedido descontando productos individuales
        for (LineaPedido linea : pedido.getLineas()) {
            Producto producto = linea.getProducto();
            
            // Verifica que hay stock suficiente antes de descontar
            // Esta validación previene ventas sin inventario disponible
            if (producto.getStockActual() < linea.getCantidad()) {
                throw new BadRequestException(
                    "Stock insuficiente para el producto: " + producto.getNombre() +
                    ". Disponible: " + producto.getStockActual() + ", Requerido: " + linea.getCantidad()
                );
            }
            
            // Crea registro de movimiento tipo VENTA con trazabilidad completa
            // Incluye referencia al pedido para auditorías y rastreo
            MovimientoStock movimiento = new MovimientoStock(
                producto,
                TipoMovimiento.VENTA,
                linea.getCantidad(),
                "Venta - Pedido #" + pedido.getId(),
                usuario
            );
            movimiento.setPedido(pedido);
            
            // Actualiza el stock actual del producto restando la cantidad vendida
            // Esta operación ocurre en la misma transacción que el movimiento
            producto.setStockActual(producto.getStockActual() - linea.getCantidad());
            productoRepository.save(producto);
            
            // Persiste el movimiento creando historial inmutable
            // Guarda stockAnterior y stockNuevo para auditoría
            movimientoRepository.save(movimiento);
        }
    }
    
    // Registra un movimiento manual de stock (entrada, salida o ajuste)
    @Transactional
    public MovimientoStockDTO registrarMovimiento(MovimientoStockDTO movimientoDTO, String emailUsuario) {
        // Obtiene el producto afectado por el movimiento
        Producto producto = productoRepository.findById(movimientoDTO.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", movimientoDTO.getProductoId()));
        
        // Identifica el usuario responsable del movimiento
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", emailUsuario));
        
        // Valida stock disponible solo para salidas manuales
        // Entradas y ajustes pueden incrementar sin restricción
        if (movimientoDTO.getTipo() == TipoMovimiento.SALIDA) {
            if (producto.getStockActual() < movimientoDTO.getCantidad()) {
                throw new BadRequestException("Stock insuficiente");
            }
        }
        
        // Crea el registro de movimiento con tipo, cantidad y motivo
        MovimientoStock movimiento = new MovimientoStock(
            producto,
            movimientoDTO.getTipo(),
            movimientoDTO.getCantidad(),
            movimientoDTO.getMotivo(),
            usuario
        );
        
        // Calcula el nuevo stock según el tipo de movimiento
        // ENTRADA y AJUSTE incrementan, SALIDA y VENTA decrementan
        int nuevoStock;
        if (movimientoDTO.getTipo() == TipoMovimiento.ENTRADA || movimientoDTO.getTipo() == TipoMovimiento.AJUSTE) {
            nuevoStock = producto.getStockActual() + movimientoDTO.getCantidad();
        } else {
            nuevoStock = producto.getStockActual() - movimientoDTO.getCantidad();
        }
        
        // Actualiza el producto con el nuevo stock calculado
        producto.setStockActual(nuevoStock);
        productoRepository.save(producto);
        
        // Persiste el movimiento con stockAnterior y stockNuevo automáticos
        MovimientoStock movimientoGuardado = movimientoRepository.save(movimiento);
        
        return convertirADTO(movimientoGuardado);
    }
    
    // Lista el historial completo de movimientos de un producto específico
    @Transactional(readOnly = true)
    public List<MovimientoStockDTO> listarPorProducto(Long productoId) {
        // Verifica que el producto existe
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", productoId));
        
        // Retorna todos los movimientos ordenados cronológicamente descendente
        // Muestra desde el más reciente para auditorías y análisis
        return movimientoRepository.findByProductoOrderByFechaMovimientoDesc(producto).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // Lista los últimos 20 movimientos del sistema completo
    @Transactional(readOnly = true)
    public List<MovimientoStockDTO> listarUltimosMovimientos() {
        // Vista resumida de actividad reciente en inventario
        // Útil para dashboards y monitoreo de operaciones
        return movimientoRepository.findTop20ByOrderByFechaMovimientoDesc().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // Convierte entidad MovimientoStock a DTO con toda la información de auditoría
    private MovimientoStockDTO convertirADTO(MovimientoStock movimiento) {
        MovimientoStockDTO dto = new MovimientoStockDTO();
        dto.setId(movimiento.getId());
        dto.setProductoId(movimiento.getProducto().getId());
        dto.setProductoNombre(movimiento.getProducto().getNombre());
        dto.setTipo(movimiento.getTipo());
        dto.setCantidad(movimiento.getCantidad());
        dto.setStockAnterior(movimiento.getStockAnterior());
        dto.setStockNuevo(movimiento.getStockNuevo());
        
        // Incluye información del usuario responsable si está disponible
        if (movimiento.getUsuario() != null) {
            dto.setUsuarioId(movimiento.getUsuario().getId());
            dto.setUsuarioNombre(movimiento.getUsuario().getNombre());
        }
        
        // Incluye referencia al pedido si el movimiento es una venta
        if (movimiento.getPedido() != null) {
            dto.setPedidoId(movimiento.getPedido().getId());
        }
        
        dto.setMotivo(movimiento.getMotivo());
        dto.setFechaMovimiento(movimiento.getFechaMovimiento());
        
        return dto;
    }
}
