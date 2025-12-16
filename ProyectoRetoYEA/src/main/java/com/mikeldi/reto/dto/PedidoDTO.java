package com.mikeldi.reto.dto;

import com.mikeldi.reto.entity.EstadoPedido;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// DTO que representa un pedido completo con su cabecera y líneas
public class PedidoDTO {
    
    // Identificador único del pedido en la base de datos
    private Long id;
    
    // Referencia al cliente que realiza el pedido
    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;
    
    // Nombre del cliente para mostrar en listados sin consultas adicionales
    private String clienteNombre;
    
    // Usuario comercial que creó el pedido para comisiones y auditoría
    private Long usuarioId;
    private String usuarioNombre;
    
    // Estado actual: PENDIENTE, PAGADO, ENVIADO, CANCELADO
    private EstadoPedido estado;
    
    // Timestamp de cuándo se creó el pedido
    private LocalDateTime fechaPedido;
    
    // Importe total sin IVA de todas las líneas
    private BigDecimal totalBase;
    
    // Suma de todos los IVA de las líneas
    private BigDecimal totalIva;
    
    // Importe final a pagar (base + IVA)
    private BigDecimal totalFinal;
    
    // Campo opcional para notas o instrucciones especiales
    private String observaciones;
    
    // Lista de líneas de pedido con los productos, cantidades y precios
    private List<LineaPedidoDTO> lineas;
    
    // Constructor vacío para deserialización JSON
    public PedidoDTO() {
    }
    
    // Getters y Setters para acceso controlado a los atributos
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
