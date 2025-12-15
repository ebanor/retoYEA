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

@Service
public class StockService {
    
    @Autowired
    private MovimientoStockRepository movimientoRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Transactional
    public void descontarStockPorPedido(Pedido pedido, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", emailUsuario));
        
        for (LineaPedido linea : pedido.getLineas()) {
            Producto producto = linea.getProducto();
            
            // Verificar stock disponible
            if (producto.getStockActual() < linea.getCantidad()) {
                throw new BadRequestException(
                    "Stock insuficiente para el producto: " + producto.getNombre() +
                    ". Disponible: " + producto.getStockActual() + ", Requerido: " + linea.getCantidad()
                );
            }
            
            // Crear movimiento
            MovimientoStock movimiento = new MovimientoStock(
                producto,
                TipoMovimiento.VENTA,
                linea.getCantidad(),
                "Venta - Pedido #" + pedido.getId(),
                usuario
            );
            movimiento.setPedido(pedido);
            
            // Actualizar stock del producto
            producto.setStockActual(producto.getStockActual() - linea.getCantidad());
            productoRepository.save(producto);
            
            // Guardar movimiento
            movimientoRepository.save(movimiento);
        }
    }
    
    @Transactional
    public MovimientoStockDTO registrarMovimiento(MovimientoStockDTO movimientoDTO, String emailUsuario) {
        Producto producto = productoRepository.findById(movimientoDTO.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", movimientoDTO.getProductoId()));
        
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", emailUsuario));
        
        // Verificar stock si es salida
        if (movimientoDTO.getTipo() == TipoMovimiento.SALIDA) {
            if (producto.getStockActual() < movimientoDTO.getCantidad()) {
                throw new BadRequestException("Stock insuficiente");
            }
        }
        
        // Crear movimiento
        MovimientoStock movimiento = new MovimientoStock(
            producto,
            movimientoDTO.getTipo(),
            movimientoDTO.getCantidad(),
            movimientoDTO.getMotivo(),
            usuario
        );
        
        // Actualizar stock del producto
        int nuevoStock;
        if (movimientoDTO.getTipo() == TipoMovimiento.ENTRADA || movimientoDTO.getTipo() == TipoMovimiento.AJUSTE) {
            nuevoStock = producto.getStockActual() + movimientoDTO.getCantidad();
        } else {
            nuevoStock = producto.getStockActual() - movimientoDTO.getCantidad();
        }
        
        producto.setStockActual(nuevoStock);
        productoRepository.save(producto);
        
        // Guardar movimiento
        MovimientoStock movimientoGuardado = movimientoRepository.save(movimiento);
        
        return convertirADTO(movimientoGuardado);
    }
    
    @Transactional(readOnly = true)
    public List<MovimientoStockDTO> listarPorProducto(Long productoId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", productoId));
        
        return movimientoRepository.findByProductoOrderByFechaMovimientoDesc(producto).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MovimientoStockDTO> listarUltimosMovimientos() {
        return movimientoRepository.findTop20ByOrderByFechaMovimientoDesc().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    private MovimientoStockDTO convertirADTO(MovimientoStock movimiento) {
        MovimientoStockDTO dto = new MovimientoStockDTO();
        dto.setId(movimiento.getId());
        dto.setProductoId(movimiento.getProducto().getId());
        dto.setProductoNombre(movimiento.getProducto().getNombre());
        dto.setTipo(movimiento.getTipo());
        dto.setCantidad(movimiento.getCantidad());
        dto.setStockAnterior(movimiento.getStockAnterior());
        dto.setStockNuevo(movimiento.getStockNuevo());
        
        if (movimiento.getUsuario() != null) {
            dto.setUsuarioId(movimiento.getUsuario().getId());
            dto.setUsuarioNombre(movimiento.getUsuario().getNombre());
        }
        
        if (movimiento.getPedido() != null) {
            dto.setPedidoId(movimiento.getPedido().getId());
        }
        
        dto.setMotivo(movimiento.getMotivo());
        dto.setFechaMovimiento(movimiento.getFechaMovimiento());
        
        return dto;
    }
}
