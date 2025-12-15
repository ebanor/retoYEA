package com.mikeldi.reto.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReporteVentasDTO {
    
    private LocalDate fecha;
    private Integer totalPedidos;
    private Integer totalFacturas;
    private BigDecimal ventasTotales;
    private BigDecimal ventasPendientes;
    private BigDecimal ventasPagadas;
    
    public ReporteVentasDTO() {
    }
    
    public ReporteVentasDTO(LocalDate fecha, Integer totalPedidos, Integer totalFacturas, 
                           BigDecimal ventasTotales, BigDecimal ventasPendientes, BigDecimal ventasPagadas) {
        this.fecha = fecha;
        this.totalPedidos = totalPedidos;
        this.totalFacturas = totalFacturas;
        this.ventasTotales = ventasTotales;
        this.ventasPendientes = ventasPendientes;
        this.ventasPagadas = ventasPagadas;
    }
    
    // Getters y Setters
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
