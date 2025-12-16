package com.mikeldi.reto.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

// DTO para reportes de ventas agregados por fecha
public class ReporteVentasDTO {
    
    // Fecha del reporte (día, semana o mes según agregación)
    private LocalDate fecha;
    
    // Cantidad de pedidos realizados en esta fecha
    private Integer totalPedidos;
    
    // Cantidad de facturas emitidas en esta fecha
    private Integer totalFacturas;
    
    // Importe total de todas las ventas (con IVA incluido)
    private BigDecimal ventasTotales;
    
    // Importe de facturas aún pendientes de cobro
    private BigDecimal ventasPendientes;
    
    // Importe de facturas ya cobradas
    private BigDecimal ventasPagadas;
    
    // Constructor vacío para instanciación
    public ReporteVentasDTO() {
    }
    
    // Constructor completo para facilitar la creación desde consultas SQL
    public ReporteVentasDTO(LocalDate fecha, Integer totalPedidos, Integer totalFacturas, 
                           BigDecimal ventasTotales, BigDecimal ventasPendientes, BigDecimal ventasPagadas) {
        this.fecha = fecha;
        this.totalPedidos = totalPedidos;
        this.totalFacturas = totalFacturas;
        this.ventasTotales = ventasTotales;
        this.ventasPendientes = ventasPendientes;
        this.ventasPagadas = ventasPagadas;
    }
    
    // Getters y Setters para acceso a los datos del reporte
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    public Integer getTotalPedidos() {
        return totalPedidos;
    }
    
    public void setTotalPedidos(Integer totalPedidos) {
        this.totalPedidos = totalPedidos;
    }
    
    public Integer getTotalFacturas() {
        return totalFacturas;
    }
    
    public void setTotalFacturas(Integer totalFacturas) {
        this.totalFacturas = totalFacturas;
    }
    
    public BigDecimal getVentasTotales() {
        return ventasTotales;
    }
    
    public void setVentasTotales(BigDecimal ventasTotales) {
        this.ventasTotales = ventasTotales;
    }
    
    public BigDecimal getVentasPendientes() {
        return ventasPendientes;
    }
    
    public void setVentasPendientes(BigDecimal ventasPendientes) {
        this.ventasPendientes = ventasPendientes;
    }
    
    public BigDecimal getVentasPagadas() {
        return ventasPagadas;
    }
    
    public void setVentasPagadas(BigDecimal ventasPagadas) {
        this.ventasPagadas = ventasPagadas;
    }
}
