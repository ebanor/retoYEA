package com.mikeldi.reto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "lineas_pedido")
public class LineaPedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    @NotNull(message = "El producto es obligatorio")
    private Producto producto;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer cantidad;
    
    @NotNull(message = "El precio unitario es obligatorio")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    @NotNull(message = "El IVA es obligatorio")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal iva;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(name = "importe_iva", nullable = false, precision = 10, scale = 2)
    private BigDecimal importeIva;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @PrePersist
    @PreUpdate
    protected void calcularImportes() {
        // Subtotal = precio * cantidad
        subtotal = precioUnitario.multiply(new BigDecimal(cantidad))
                .setScale(2, RoundingMode.HALF_UP);
        
        // Importe IVA = subtotal * (iva / 100)
        importeIva = subtotal.multiply(iva.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
        
        // Total = subtotal + IVA
        total = subtotal.add(importeIva);
    }
    
    // Constructores
    public LineaPedido() {
    }
    
    public LineaPedido(Producto producto, Integer cantidad, BigDecimal precioUnitario, BigDecimal iva) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.iva = iva;
        calcularImportes();
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Pedido getPedido() {
        return pedido;
    }
    
    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
    
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        calcularImportes();
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularImportes();
    }
    
    public BigDecimal getIva() {
        return iva;
    }
    
    public void setIva(BigDecimal iva) {
        this.iva = iva;
        calcularImportes();
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public BigDecimal getImporteIva() {
        return importeIva;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
}
