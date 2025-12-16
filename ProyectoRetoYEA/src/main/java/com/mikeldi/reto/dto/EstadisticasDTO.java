package com.mikeldi.reto.dto;

import java.math.BigDecimal;

// DTO para encapsular métricas y estadísticas del sistema en un solo objeto
public class EstadisticasDTO {
    
    // Contador total de clientes registrados en el sistema
    private Long totalClientes;
    
    // Contador total de productos en el catálogo
    private Long totalProductos;
    
    // Contador total de pedidos realizados
    private Long totalPedidos;
    
    // Contador total de facturas emitidas
    private Long totalFacturas;
    
    // Alerta: número de productos que necesitan reposición
    private Long productosStockBajo;
    
    // Facturas que aún no han sido pagadas
    private Long facturasPendientes;
    
    // Suma de ventas del mes actual en euros
    // BigDecimal evita errores de precisión en cálculos monetarios
    private BigDecimal ventasTotalesMes;
    
    // Importe total de facturas pendientes de cobro
    private BigDecimal ventasPendientesCobro;
    
    // Constructor vacío para instanciación por frameworks
    public EstadisticasDTO() {
    }
    
    // Getters y Setters para acceso a las métricas
    public Long getTotalClientes() {
        return totalClientes;
    }
    
    public void setTotalClientes(Long totalClientes) {
        this.totalClientes = totalClientes;
    }
    
    public Long getTotalProductos() {
        return totalProductos;
    }
    
    public void setTotalProductos(Long totalProductos) {
        this.totalProductos = totalProductos;
    }
    
    public Long getTotalPedidos() {
        return totalPedidos;
    }
    
    public void setTotalPedidos(Long totalPedidos) {
        this.totalPedidos = totalPedidos;
    }
    
    public Long getTotalFacturas() {
        return totalFacturas;
    }
    
    public void setTotalFacturas(Long totalFacturas) {
        this.totalFacturas = totalFacturas;
    }
    
    public Long getProductosStockBajo() {
        return productosStockBajo;
    }
    
    public void setProductosStockBajo(Long productosStockBajo) {
        this.productosStockBajo = productosStockBajo;
    }
    
    public Long getFacturasPendientes() {
        return facturasPendientes;
    }
    
    public void setFacturasPendientes(Long facturasPendientes) {
        this.facturasPendientes = facturasPendientes;
    }
    
    public BigDecimal getVentasTotalesMes() {
        return ventasTotalesMes;
    }
    
    public void setVentasTotalesMes(BigDecimal ventasTotalesMes) {
        this.ventasTotalesMes = ventasTotalesMes;
    }
    
    public BigDecimal getVentasPendientesCobro() {
        return ventasPendientesCobro;
    }
    
    public void setVentasPendientesCobro(BigDecimal ventasPendientesCobro) {
        this.ventasPendientesCobro = ventasPendientesCobro;
    }
}
