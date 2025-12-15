package com.mikeldi.reto.dto;

import com.mikeldi.reto.entity.TipoMovimiento;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class MovimientoStockDTO {
    
    private Long id;
    
    @NotNull(message = "El producto es obligatorio")
    private Long productoId;
    private String productoNombre;
    
    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimiento tipo;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
    
    private Integer stockAnterior;
    private Integer stockNuevo;
    
    private Long usuarioId;
    private String usuarioNombre;
    
    private Long pedidoId;
    private String motivo;
    
    private LocalDateTime fechaMovimiento;
    
    // Constructores
    public MovimientoStockDTO() {
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getProductoId() {
        return productoId;
    }
    
    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }
    
    public String getProductoNombre() {
        return productoNombre;
    }
    
    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }
    
    public TipoMovimiento getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoMovimiento tipo) {
        this.tipo = tipo;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    
    public Integer getStockAnterior() {
        return stockAnterior;
    }
    
    public void setStockAnterior(Integer stockAnterior) {
        this.stockAnterior = stockAnterior;
    }
    
    public Integer getStockNuevo() {
        return stockNuevo;
    }
    
    public void setStockNuevo(Integer stockNuevo) {
        this.stockNuevo = stockNuevo;
    }
    
    public Long getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    public String getUsuarioNombre() {
        return usuarioNombre;
    }
    
    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }
    
    public Long getPedidoId() {
        return pedidoId;
    }
    
    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }
    
    public String getMotivo() {
        return motivo;
    }
    
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
    
    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }
    
    public void setFechaMovimiento(LocalDateTime fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }
}
