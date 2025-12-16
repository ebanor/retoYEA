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

// Servicio especializado en generación de reportes y estadísticas del negocio
@Service
public class ReportesService {
    
    // Inyecta repositorio para contar clientes totales
    @Autowired
    private ClienteRepository clienteRepository;
    
    // Inyecta repositorio para estadísticas de inventario
    @Autowired
    private ProductoRepository productoRepository;
    
    // Inyecta repositorio para métricas de pedidos
    @Autowired
    private PedidoRepository pedidoRepository;
    
    // Inyecta repositorio para análisis de ventas y cobros
    @Autowired
    private FacturaRepository facturaRepository;
    
    // Genera un resumen ejecutivo con KPIs principales del negocio
    @Transactional(readOnly = true)
    public EstadisticasDTO obtenerEstadisticas() {
        EstadisticasDTO stats = new EstadisticasDTO();
        
        // Contadores básicos de entidades principales
        // Proporcionan vista general del tamaño del negocio
        stats.setTotalClientes(clienteRepository.count());
        stats.setTotalProductos(productoRepository.count());
        stats.setTotalPedidos(pedidoRepository.count());
        stats.setTotalFacturas(facturaRepository.count());
        
        // Cuenta productos con stock bajo que requieren reposición
        // Umbral configurado en 10 unidades como ejemplo
        stats.setProductosStockBajo((long) productoRepository.findByStockActualLessThanAndActivoTrue(10).size());
        
        // Cuenta facturas pendientes de pago para flujo de caja
        // Métrica crítica para gestión de cuentas por cobrar
        stats.setFacturasPendientes(facturaRepository.countByEstado(EstadoFactura.PENDIENTE));
        
        // Calcula ventas del mes actual desde el día 1 hasta el último día
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate finMes = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        
        // Obtiene todas las facturas emitidas en el mes actual
        var facturasDelMes = facturaRepository.findByFechaEmisionBetweenOrderByFechaEmisionDesc(inicioMes, finMes);
        // Suma el total final de todas las facturas usando reduce
        BigDecimal ventasMes = facturasDelMes.stream()
                .map(f -> f.getTotalFinal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setVentasTotalesMes(ventasMes);
        
        // Calcula el importe total de facturas pendientes de cobro
        // Indicador de liquidez: cuánto dinero está por cobrar
        var facturasPendientes = facturaRepository.findByEstadoOrderByFechaEmisionDesc(EstadoFactura.PENDIENTE);
        BigDecimal ventasPendientes = facturasPendientes.stream()
                .map(f -> f.getTotalFinal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setVentasPendientesCobro(ventasPendientes);
        
        return stats;
    }
    
    // Genera reporte detallado de ventas agrupado por día en un rango de fechas
    @Transactional(readOnly = true)
    public List<ReporteVentasDTO> reporteVentasPorPeriodo(LocalDate inicio, LocalDate fin) {
        List<ReporteVentasDTO> reportes = new ArrayList<>();
        
        // Obtiene todas las facturas del periodo solicitado
        var facturas = facturaRepository.findByFechaEmisionBetweenOrderByFechaEmisionDesc(inicio, fin);
        
        // Agrupa facturas por fecha de emisión para análisis diario
        // Esto permite identificar tendencias y picos de ventas
        facturas.stream()
                .collect(java.util.stream.Collectors.groupingBy(f -> f.getFechaEmision()))
                .forEach((fecha, facturasDelDia) -> {
                    // Calcula ventas totales del día sumando todas las facturas
                    BigDecimal ventasTotales = facturasDelDia.stream()
                            .map(f -> f.getTotalFinal())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    // Calcula ventas cobradas filtrando solo facturas PAGADAS
                    BigDecimal ventasPagadas = facturasDelDia.stream()
                            .filter(f -> f.getEstado() == EstadoFactura.PAGADA)
                            .map(f -> f.getTotalFinal())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    // Calcula ventas pendientes filtrando facturas PENDIENTES
                    // Útil para analizar ratio de cobro inmediato vs crédito
                    BigDecimal ventasPendientes = facturasDelDia.stream()
                            .filter(f -> f.getEstado() == EstadoFactura.PENDIENTE)
                            .map(f -> f.getTotalFinal())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    // Crea el DTO con todas las métricas del día
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
        
        // Ordena el reporte por fecha descendente (más reciente primero)
        reportes.sort((r1, r2) -> r2.getFecha().compareTo(r1.getFecha()));
        return reportes;
    }
}
