package com.mikeldi.reto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Define esta clase como entidad JPA que se mapea a la tabla "lineas_pedido"
@Entity
@Table(name = "lineas_pedido")
public class LineaPedido {
    
    // Clave primaria con autoincremento gestionado por la base de datos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Relación muchos a uno con Pedido: cada línea pertenece a un pedido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;
    
    // Relación muchos a uno con Producto: carga inmediata para mostrar detalles
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    @NotNull(message = "El producto es obligatorio")
    private Producto producto;
    
    // Cantidad de unidades del producto en esta línea
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer cantidad;
    
    // Precio por unidad en el momento de la compra (no cambia si el producto sube)
    @NotNull(message = "El precio unitario es obligatorio")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    // Porcentaje de IVA aplicable al producto (ej: 21 para 21%)
    @NotNull(message = "El IVA es obligatorio")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal iva;
    
    // Importe sin IVA: cantidad × precioUnitario
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    // Importe del IVA calculado sobre el subtotal
    @Column(name = "importe_iva", nullable = false, precision = 10, scale = 2)
    private BigDecimal importeIva;
    
    // Importe total con IVA incluido: subtotal + importeIva
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    // Callback que se ejecuta antes de persistir o actualizar
    // Calcula automáticamente subtotal, IVA y total
    @PrePersist
    @PreUpdate
    protected void calcularImportes() {
        // Calcula subtotal multiplicando precio por cantidad
        subtotal = precioUnitario.multiply(new BigDecimal(cantidad))
                .setScale(2, RoundingMode.HALF_UP);
        
        // Calcula el importe del IVA: subtotal × (porcentaje IVA / 100)
        importeIva = subtotal.multiply(iva.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
        
        // Calcula total sumando subtotal e IVA
        total = subtotal.add(importeIva);
    }
    
    // Constructor vacío requerido por JPA
    public LineaPedido() {
    }
    
    // Constructor con parámetros que calcula importes automáticamente
    public LineaPedido(Producto producto, Integer cantidad, BigDecimal precioUnitario, BigDecimal iva) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.iva = iva;
        // Calcula importes al crear la línea
        calcularImportes();
    }
    
    // Getters y Setters para acceso controlado a los atributos
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
    
    // Setter que recalcula importes al cambiar la cantidad
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        calcularImportes();
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    // Setter que recalcula importes al cambiar el precio
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularImportes();
    }
    
    public BigDecimal getIva() {
        return iva;
    }
    
    // Setter que recalcula importes al cambiar el IVA
    public void setIva(BigDecimal iva) {
        this.iva = iva;
        calcularImportes();
    }
    
    // Getters de campos calculados (solo lectura, sin setters)
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
