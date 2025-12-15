package com.mikeldi.reto.service;

import com.mikeldi.reto.dto.EstadisticasDTO;
import com.mikeldi.reto.dto.ReporteVentasDTO;
import com.mikeldi.reto.entity.EstadoFactura;
import com.mikeldi.reto.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportesService {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private FacturaRepository facturaRepository;
    
    @Transactional(readOnly = true)
    public EstadisticasDTO obtenerEstadisticas() {
        EstadisticasDTO stats = new EstadisticasDTO();
        
        // Contadores básicos
        stats.setTotalClientes(clienteRepository.count());
        stats.setTotalProductos(productoRepository.count());
        stats.setTotalPedidos(pedidoRepository.count());
        stats.setTotalFacturas(facturaRepository.count());
        
        // Productos con stock bajo
        stats.setProductosStockBajo((long) productoRepository.findByStockActualLessThanAndActivoTrue(10).size());
        
        // Facturas pendientes
        stats.setFacturasPendientes(facturaRepository.countByEstado(EstadoFactura.PENDIENTE));
        
        // Ventas del mes actual
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate finMes = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        
        var facturasDelMes = facturaRepository.findByFechaEmisionBetweenOrderByFechaEmisionDesc(inicioMes, finMes);
        BigDecimal ventasMes = facturasDelMes.stream()
                .map(f -> f.getTotalFinal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setVentasTotalesMes(ventasMes);
        
        // Ventas pendientes de cobro
        var facturasPendientes = facturaRepository.findByEstadoOrderByFechaEmisionDesc(EstadoFactura.PENDIENTE);
        BigDecimal ventasPendientes = facturasPendientes.stream()
                .map(f -> f.getTotalFinal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setVentasPendientesCobro(ventasPendientes);
        
        return stats;
    }
    
    @Transactional(readOnly = true)
    public List<ReporteVentasDTO> reporteVentasPorPeriodo(LocalDate inicio, LocalDate fin) {
        List<ReporteVentasDTO> reportes = new ArrayList<>();
        
        var facturas = facturaRepository.findByFechaEmisionBetweenOrderByFechaEmisionDesc(inicio, fin);
        
        // Agrupar por fecha
        facturas.stream()
                .collect(java.util.stream.Collectors.groupingBy(f -> f.getFechaEmision()))
                .forEach((fecha, facturasDelDia) -> {
                    BigDecimal ventasTotales = facturasDelDia.stream()
                            .map(f -> f.getTotalFinal())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    BigDecimal ventasPagadas = facturasDelDia.stream()
                            .filter(f -> f.getEstado() == EstadoFactura.PAGADA)
                            .map(f -> f.getTotalFinal())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    BigDecimal ventasPendientes = facturasDelDia.stream()
                            .filter(f -> f.getEstado() == EstadoFactura.PENDIENTE)
                            .map(f -> f.getTotalFinal())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    ReporteVentasDTO reporte = new ReporteVentasDTO(
                        fecha,
                        0, // totalPedidos (se puede calcular si es necesario)
                        facturasDelDia.size(),
                        ventasTotales,
                        ventasPendientes,
                        ventasPagadas
                    );
                    reportes.add(reporte);
                });
        
        reportes.sort((r1, r2) -> r2.getFecha().compareTo(r1.getFecha()));
        return reportes;
    }
}
