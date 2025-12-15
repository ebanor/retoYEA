package com.mikeldi.reto.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class LineaPedidoDTO {
    
    private Long id;
    
    @NotNull(message = "El producto es obligatorio")
    private Long productoId;
    private String productoNombre;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
    
    @NotNull(message = "El precio unitario es obligatorio")
    private BigDecimal precioUnitario;
    
    @NotNull(message = "El IVA es obligatorio")
    private BigDecimal iva;
    
    private BigDecimal subtotal;
    private BigDecimal importeIva;
    private BigDecimal total;
    
    // Constructores
    public LineaPedidoDTO() {
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
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    public BigDecimal getIva() {
        return iva;
    }
    
    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getImporteIva() {
        return importeIva;
    }
    
    public void setImporteIva(BigDecimal importeIva) {
        this.importeIva = importeIva;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
