package com.mikeldi.reto.dto;

import com.mikeldi.reto.entity.EstadoPedido;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoDTO {
    
    private Long id;
    
    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;
    private String clienteNombre;
    
    private Long usuarioId;
    private String usuarioNombre;
    
    private EstadoPedido estado;
    private LocalDateTime fechaPedido;
    private BigDecimal totalBase;
    private BigDecimal totalIva;
    private BigDecimal totalFinal;
    private String observaciones;
    
    private List<LineaPedidoDTO> lineas;
    
    // Constructores
    public PedidoDTO() {
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }
    
    public String getClienteNombre() {
        return clienteNombre;
    }
    
    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
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
    
    public EstadoPedido getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }
    
    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }
    
    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }
    
    public BigDecimal getTotalBase() {
        return totalBase;
    }
    
    public void setTotalBase(BigDecimal totalBase) {
        this.totalBase = totalBase;
    }
    
    public BigDecimal getTotalIva() {
        return totalIva;
    }
    
    public void setTotalIva(BigDecimal totalIva) {
        this.totalIva = totalIva;
    }
    
    public BigDecimal getTotalFinal() {
        return totalFinal;
    }
    
    public void setTotalFinal(BigDecimal totalFinal) {
        this.totalFinal = totalFinal;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public List<LineaPedidoDTO> getLineas() {
        return lineas;
    }
    
    public void setLineas(List<LineaPedidoDTO> lineas) {
        this.lineas = lineas;
    }
}
