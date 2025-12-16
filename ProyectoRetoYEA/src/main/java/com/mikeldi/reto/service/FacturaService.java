package com.mikeldi.reto.service;

import com.mikeldi.reto.dto.FacturaDTO;
import com.mikeldi.reto.entity.*;
import com.mikeldi.reto.exception.BadRequestException;
import com.mikeldi.reto.exception.ResourceNotFoundException;
import com.mikeldi.reto.repository.ClienteRepository;
import com.mikeldi.reto.repository.FacturaRepository;
import com.mikeldi.reto.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Servicio que encapsula la lógica de negocio para gestión de facturas
@Service
public class FacturaService {
    
    // Inyecta repositorio para acceso a datos de facturas
    @Autowired
    private FacturaRepository facturaRepository;
    
    // Inyecta repositorio para obtener pedidos a facturar
    @Autowired
    private PedidoRepository pedidoRepository;
    
    // Inyecta repositorio para acceso a información de clientes
    @Autowired
    private ClienteRepository clienteRepository;
    
    // Inyecta servicio de stock para descontar productos al facturar
    @Autowired
    private StockService stockService;
    
    // Emite una nueva factura desde un pedido pagado, descontando stock automáticamente
    @Transactional
    public FacturaDTO emitirFactura(FacturaDTO facturaDTO, String emailUsuario) {
        // Obtiene el pedido que se va a facturar
        Pedido pedido = pedidoRepository.findById(facturaDTO.getPedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", facturaDTO.getPedidoId()));
        
        // Valida que el pedido esté en estado PAGADO antes de facturar
        // Esto garantiza que no se facturen pedidos pendientes o cancelados
        if (pedido.getEstado() != EstadoPedido.PAGADO) {
            throw new BadRequestException("Solo se pueden facturar pedidos en estado PAGADO");
        }
        
        // Crea la factura usando el constructor que copia datos del pedido
        // Esto incluye cliente, totales y fecha de emisión automáticos
        Factura factura = new Factura(pedido);
        // Añade observaciones opcionales si fueron proporcionadas
        if (facturaDTO.getObservaciones() != null) {
            factura.setObservaciones(facturaDTO.getObservaciones());
        }
        
        // Descuenta el stock de productos del pedido y registra movimiento
        // Esto asegura que el inventario refleje la venta real
        stockService.descontarStockPorPedido(pedido, emailUsuario);
        
        // Persiste la factura en la base de datos con número generado
        Factura facturaGuardada = facturaRepository.save(factura);
        
        return convertirADTO(facturaGuardada);
    }
    
    // Lista todas las facturas ordenadas por fecha de emisión descendente
    @Transactional(readOnly = true)
    public List<FacturaDTO> listarTodas() {
        // Las más recientes aparecen primero para facilitar revisión
        return facturaRepository.findAllByOrderByFechaEmisionDesc().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // Filtra facturas por estado específico (PENDIENTE, PAGADA, VENCIDA, CANCELADA)
    @Transactional(readOnly = true)
    public List<FacturaDTO> listarPorEstado(EstadoFactura estado) {
        // Útil para dashboards que muestran facturas pendientes o vencidas
        return facturaRepository.findByEstadoOrderByFechaEmisionDesc(estado).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // Lista todas las facturas de un cliente específico
    @Transactional(readOnly = true)
    public List<FacturaDTO> listarPorCliente(Long clienteId) {
        // Verifica que el cliente existe antes de buscar sus facturas
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", clienteId));
        
        // Retorna el historial de facturación del cliente
        return facturaRepository.findByClienteOrderByFechaEmisionDesc(cliente).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // Obtiene una factura específica por su ID
    @Transactional(readOnly = true)
    public FacturaDTO obtenerPorId(Long id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura", "id", id));
        return convertirADTO(factura);
    }
    
    // Cambia el estado de una factura (ej: de PENDIENTE a PAGADA)
    @Transactional
    public FacturaDTO cambiarEstado(Long id, EstadoFactura nuevoEstado) {
        // Obtiene la factura a modificar
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura", "id", id));
        
        // Actualiza el estado (PENDIENTE → PAGADA, PENDIENTE → VENCIDA, etc.)
        factura.setEstado(nuevoEstado);
        Factura facturaActualizada = facturaRepository.save(factura);
        
        return convertirADTO(facturaActualizada);
    }
    
    // Convierte una entidad Factura a DTO para transferencia segura
    // Extrae solo los campos necesarios sin exponer estructura interna
    private FacturaDTO convertirADTO(Factura factura) {
        FacturaDTO dto = new FacturaDTO();
        dto.setId(factura.getId());
        dto.setNumeroFactura(factura.getNumeroFactura());
        dto.setPedidoId(factura.getPedido().getId());
        dto.setClienteId(factura.getCliente().getId());
        dto.setClienteNombre(factura.getCliente().getNombre());
        dto.setFechaEmision(factura.getFechaEmision());
        dto.setFechaVencimiento(factura.getFechaVencimiento());
        dto.setEstado(factura.getEstado());
        dto.setTotalBase(factura.getTotalBase());
        dto.setTotalIva(factura.getTotalIva());
        dto.setTotalFinal(factura.getTotalFinal());
        dto.setObservaciones(factura.getObservaciones());
        return dto;
    }
}
