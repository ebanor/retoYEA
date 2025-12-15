package com.mikeldi.reto.service;

import com.mikeldi.reto.dto.LineaPedidoDTO;
import com.mikeldi.reto.dto.PedidoDTO;
import com.mikeldi.reto.entity.*;
import com.mikeldi.reto.exception.BadRequestException;
import com.mikeldi.reto.exception.ResourceNotFoundException;
import com.mikeldi.reto.repository.ClienteRepository;
import com.mikeldi.reto.repository.PedidoRepository;
import com.mikeldi.reto.repository.ProductoRepository;
import com.mikeldi.reto.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Transactional
    public PedidoDTO crearPedido(PedidoDTO pedidoDTO, String emailUsuario) {
        // Obtener cliente
        Cliente cliente = clienteRepository.findById(pedidoDTO.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", pedidoDTO.getClienteId()));
        
        // Obtener usuario actual
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", emailUsuario));
        
        // Crear pedido
        Pedido pedido = new Pedido(cliente, usuario);
        pedido.setObservaciones(pedidoDTO.getObservaciones());
        
        // Añadir líneas
        if (pedidoDTO.getLineas() != null && !pedidoDTO.getLineas().isEmpty()) {
            for (LineaPedidoDTO lineaDTO : pedidoDTO.getLineas()) {
                Producto producto = productoRepository.findById(lineaDTO.getProductoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", lineaDTO.getProductoId()));
                
                // Verificar stock
                if (producto.getStockActual() < lineaDTO.getCantidad()) {
                    throw new BadRequestException("Stock insuficiente para el producto: " + producto.getNombre());
                }
                
                LineaPedido linea = new LineaPedido(
                    producto,
                    lineaDTO.getCantidad(),
                    lineaDTO.getPrecioUnitario(),
                    lineaDTO.getIva()
                );
                
                pedido.addLinea(linea);
            }
        }
        
        // Calcular totales
        pedido.calcularTotales();
        
        // Guardar
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        
        return convertirADTO(pedidoGuardado);
    }
    
    @Transactional(readOnly = true)
    public List<PedidoDTO> listarTodos() {
        return pedidoRepository.findAllOrderByFechaPedidoDesc().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PedidoDTO> listarPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstadoOrderByFechaPedidoDesc(estado).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PedidoDTO> listarPorCliente(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", clienteId));
        
        return pedidoRepository.findByClienteOrderByFechaPedidoDesc(cliente).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PedidoDTO obtenerPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));
        return convertirADTO(pedido);
    }
    
    @Transactional
    public PedidoDTO cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));
        
        pedido.setEstado(nuevoEstado);
        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        
        return convertirADTO(pedidoActualizado);
    }
    
    @Transactional
    public void eliminarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));
        
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new BadRequestException("Solo se pueden eliminar pedidos en estado PENDIENTE");
        }
        
        pedidoRepository.delete(pedido);
    }
    
    // Método auxiliar de conversión
    private PedidoDTO convertirADTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(pedido.getId());
        dto.setClienteId(pedido.getCliente().getId());
        dto.setClienteNombre(pedido.getCliente().getNombre());
        dto.setUsuarioId(pedido.getUsuario().getId());
        dto.setUsuarioNombre(pedido.getUsuario().getNombre());
        dto.setEstado(pedido.getEstado());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setTotalBase(pedido.getTotalBase());
        dto.setTotalIva(pedido.getTotalIva());
        dto.setTotalFinal(pedido.getTotalFinal());
        dto.setObservaciones(pedido.getObservaciones());
        
        // Convertir líneas
        List<LineaPedidoDTO> lineasDTO = pedido.getLineas().stream()
                .map(this::convertirLineaADTO)
                .collect(Collectors.toList());
        dto.setLineas(lineasDTO);
        
        return dto;
    }
    
    private LineaPedidoDTO convertirLineaADTO(LineaPedido linea) {
        LineaPedidoDTO dto = new LineaPedidoDTO();
        dto.setId(linea.getId());
        dto.setProductoId(linea.getProducto().getId());
        dto.setProductoNombre(linea.getProducto().getNombre());
        dto.setCantidad(linea.getCantidad());
        dto.setPrecioUnitario(linea.getPrecioUnitario());
        dto.setIva(linea.getIva());
        dto.setSubtotal(linea.getSubtotal());
        dto.setImporteIva(linea.getImporteIva());
        dto.setTotal(linea.getTotal());
        return dto;
    }
}
