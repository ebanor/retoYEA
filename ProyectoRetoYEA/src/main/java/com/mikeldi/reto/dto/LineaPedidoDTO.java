package com.mikeldi.reto.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

// DTO que representa una línea individual dentro de un pedido
public class LineaPedidoDTO {
    
    // Identificador único de la línea de pedido
    private Long id;
    
    // Referencia al producto que se está comprando
    @NotNull(message = "El producto es obligatorio")
    private Long productoId;
    
    // Nombre del producto para mostrar sin cargar la entidad completa
    private String productoNombre;
    
    // Cantidad de unidades del producto en esta línea
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
    
    // Precio por unidad del producto en el momento de la compra
    @NotNull(message = "El precio unitario es obligatorio")
    private BigDecimal precioUnitario;
    
    // Porcentaje de IVA aplicable (ej: 21% se guarda como 21)
    @NotNull(message = "El IVA es obligatorio")
    private BigDecimal iva;
    
    // Subtotal sin IVA: cantidad × precioUnitario
    private BigDecimal subtotal;
    
    // Importe del IVA calculado: subtotal × (iva / 100)
    private BigDecimal importeIva;
    
    // Total con IVA incluido: subtotal + importeIva
    private BigDecimal total;
    
    // Constructor vacío para deserialización JSON
    public LineaPedidoDTO() {
    }
    
    // Getters y Setters para acceso controlado a los atributos
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
