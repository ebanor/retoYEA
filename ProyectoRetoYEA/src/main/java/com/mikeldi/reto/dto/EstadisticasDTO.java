package com.mikeldi.reto.dto;

import java.math.BigDecimal;

public class EstadisticasDTO {
    
    private Long totalClientes;
    private Long totalProductos;
    private Long totalPedidos;
    private Long totalFacturas;
    private Long productosStockBajo;
    private Long facturasPendientes;
    private BigDecimal ventasTotalesMes;
    private BigDecimal ventasPendientesCobro;
    
    public EstadisticasDTO() {
    }
    
    // Getters y Setters
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
