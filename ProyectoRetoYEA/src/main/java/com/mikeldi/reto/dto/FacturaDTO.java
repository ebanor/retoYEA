package com.mikeldi.reto.dto;

import com.mikeldi.reto.entity.EstadoFactura;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

// DTO para transferir datos de facturas entre capas
public class FacturaDTO {
    
    // Identificador único de la factura en la base de datos
    private Long id;
    
    // Número secuencial generado automáticamente (ej: FACT-2025-001)
    private String numeroFactura;
    
    // Referencia al pedido desde el cual se emite esta factura
    @NotNull(message = "El pedido es obligatorio")
    private Long pedidoId;
    
    // Datos del cliente para mostrar en el DTO sin necesidad de cargar la entidad completa
    private Long clienteId;
    private String clienteNombre;
    
    // Fecha en que se emite la factura
    private LocalDate fechaEmision;
    
    // Fecha límite de pago (normalmente 30-60 días después de emisión)
    private LocalDate fechaVencimiento;
    
    // Estado actual: PENDIENTE, PAGADA, VENCIDA, CANCELADA
    private EstadoFactura estado;
    
    // Importe sin IVA aplicado
    private BigDecimal totalBase;
    
    // Importe del IVA calculado sobre la base imponible
    private BigDecimal totalIva;
    
    // Importe total a pagar (base + IVA)
    private BigDecimal totalFinal;
    
    // Campo opcional para notas o comentarios adicionales
    private String observaciones;
    
    // Constructor vacío requerido para deserialización JSON
    public FacturaDTO() {
    }
    
    // Getters y Setters para acceso controlado a los atributos
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNumeroFactura() {
        return numeroFactura;
    }
    
    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }
    
    public Long getPedidoId() {
        return pedidoId;
    }
    
    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
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
    
    public LocalDate getFechaEmision() {
        return fechaEmision;
    }
    
    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
    
    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }
    
    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
    
    public EstadoFactura getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoFactura estado) {
        this.estado = estado;
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
}
