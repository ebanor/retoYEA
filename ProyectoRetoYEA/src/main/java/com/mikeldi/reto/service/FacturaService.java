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

@Service
public class FacturaService {
    
    @Autowired
    private FacturaRepository facturaRepository;
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private StockService stockService;
    
    @Transactional
    public FacturaDTO emitirFactura(FacturaDTO facturaDTO, String emailUsuario) {
        // Obtener pedido
        Pedido pedido = pedidoRepository.findById(facturaDTO.getPedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", facturaDTO.getPedidoId()));
        
        // Verificar que el pedido esté pagado
        if (pedido.getEstado() != EstadoPedido.PAGADO) {
            throw new BadRequestException("Solo se pueden facturar pedidos en estado PAGADO");
        }
        
        // Crear factura
        Factura factura = new Factura(pedido);
        if (facturaDTO.getObservaciones() != null) {
            factura.setObservaciones(facturaDTO.getObservaciones());
        }
        
        // Descontar stock
        stockService.descontarStockPorPedido(pedido, emailUsuario);
        
        // Guardar factura
        Factura facturaGuardada = facturaRepository.save(factura);
        
        return convertirADTO(facturaGuardada);
    }
    
    @Transactional(readOnly = true)
    public List<FacturaDTO> listarTodas() {
        return facturaRepository.findAllByOrderByFechaEmisionDesc().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<FacturaDTO> listarPorEstado(EstadoFactura estado) {
        return facturaRepository.findByEstadoOrderByFechaEmisionDesc(estado).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<FacturaDTO> listarPorCliente(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", clienteId));
        
        return facturaRepository.findByClienteOrderByFechaEmisionDesc(cliente).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public FacturaDTO obtenerPorId(Long id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura", "id", id));
        return convertirADTO(factura);
    }
    
    @Transactional
    public FacturaDTO cambiarEstado(Long id, EstadoFactura nuevoEstado) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura", "id", id));
        
        factura.setEstado(nuevoEstado);
        Factura facturaActualizada = facturaRepository.save(factura);
        
        return convertirADTO(facturaActualizada);
    }
    
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
