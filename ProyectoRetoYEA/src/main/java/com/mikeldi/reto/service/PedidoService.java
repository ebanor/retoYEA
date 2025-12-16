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

// Servicio que encapsula la lógica de negocio para gestión de pedidos
@Service
public class PedidoService {
    
    // Inyecta repositorio para acceso a datos de pedidos
    @Autowired
    private PedidoRepository pedidoRepository;
    
    // Inyecta repositorio para obtener información de clientes
    @Autowired
    private ClienteRepository clienteRepository;
    
    // Inyecta repositorio para obtener el usuario que crea el pedido
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    // Inyecta repositorio para validar productos y stock
    @Autowired
    private ProductoRepository productoRepository;
    
    // Crea un nuevo pedido con validación de stock para cada línea
    @Transactional
    public PedidoDTO crearPedido(PedidoDTO pedidoDTO, String emailUsuario) {
        // Obtiene el cliente que realiza el pedido
        Cliente cliente = clienteRepository.findById(pedidoDTO.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", pedidoDTO.getClienteId()));
        
        // Obtiene el usuario comercial autenticado que crea el pedido
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", emailUsuario));
        
        // Crea el pedido con estado PENDIENTE por defecto
        Pedido pedido = new Pedido(cliente, usuario);
        pedido.setObservaciones(pedidoDTO.getObservaciones());
        
        // Añade cada línea de pedido validando stock disponible
        if (pedidoDTO.getLineas() != null && !pedidoDTO.getLineas().isEmpty()) {
            for (LineaPedidoDTO lineaDTO : pedidoDTO.getLineas()) {
                // Obtiene el producto de cada línea
                Producto producto = productoRepository.findById(lineaDTO.getProductoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", lineaDTO.getProductoId()));
                
                // Valida que hay stock suficiente antes de añadir al pedido
                // Esto previene sobreventa de productos sin inventario
                if (producto.getStockActual() < lineaDTO.getCantidad()) {
                    throw new BadRequestException("Stock insuficiente para el producto: " + producto.getNombre());
                }
                
                // Crea la línea con precio e IVA del momento actual
                // Los valores se congelan para mantener histórico correcto
                LineaPedido linea = new LineaPedido(
                    producto,
                    lineaDTO.getCantidad(),
                    lineaDTO.getPrecioUnitario(),
                    lineaDTO.getIva()
                );
                
                // Añade línea al pedido manteniendo relación bidireccional
                pedido.addLinea(linea);
            }
        }
        
        // Calcula los totales sumando todas las líneas
        pedido.calcularTotales();
        
        // Persiste el pedido con todas sus líneas (cascade)
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        
        return convertirADTO(pedidoGuardado);
    }
    
    // Lista todos los pedidos ordenados por fecha descendente
    @Transactional(readOnly = true)
    public List<PedidoDTO> listarTodos() {
        // Los más recientes aparecen primero
        return pedidoRepository.findAllOrderByFechaPedidoDesc().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // Filtra pedidos por estado específico (PENDIENTE, PAGADO, ENVIADO, CANCELADO)
    @Transactional(readOnly = true)
    public List<PedidoDTO> listarPorEstado(EstadoPedido estado) {
        // Útil para dashboards que muestran pedidos pendientes o en proceso
        return pedidoRepository.findByEstadoOrderByFechaPedidoDesc(estado).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // Lista todos los pedidos de un cliente específico
    @Transactional(readOnly = true)
    public List<PedidoDTO> listarPorCliente(Long clienteId) {
        // Verifica que el cliente existe antes de buscar sus pedidos
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", clienteId));
        
        // Retorna el historial de pedidos del cliente
        return pedidoRepository.findByClienteOrderByFechaPedidoDesc(cliente).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // Obtiene un pedido específico por su ID con todas sus líneas
    @Transactional(readOnly = true)
    public PedidoDTO obtenerPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));
        return convertirADTO(pedido);
    }
    
    // Cambia el estado de un pedido (ej: PENDIENTE → PAGADO → ENVIADO)
    @Transactional
    public PedidoDTO cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));
        
        // Actualiza el estado del pedido según el flujo del negocio
        pedido.setEstado(nuevoEstado);
        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        
        return convertirADTO(pedidoActualizado);
    }
    
    // Elimina un pedido solo si está en estado PENDIENTE
    @Transactional
    public void eliminarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));
        
        // Valida que el pedido no haya sido procesado antes de eliminar
        // Pedidos pagados o enviados no deben eliminarse para mantener trazabilidad
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new BadRequestException("Solo se pueden eliminar pedidos en estado PENDIENTE");
        }
        
        // Elimina el pedido y sus líneas (orphanRemoval)
        pedidoRepository.delete(pedido);
    }
    
    // Convierte una entidad Pedido a DTO incluyendo todas sus líneas
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
        
        // Convierte cada línea de pedido a DTO preservando detalle completo
        List<LineaPedidoDTO> lineasDTO = pedido.getLineas().stream()
                .map(this::convertirLineaADTO)
                .collect(Collectors.toList());
        dto.setLineas(lineasDTO);
        
        return dto;
    }
    
    // Convierte una línea de pedido a DTO con todos sus cálculos
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
