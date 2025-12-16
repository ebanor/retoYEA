package com.mikeldi.reto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Define esta clase como entidad JPA que se mapea a la tabla "productos"
@Entity
@Table(name = "productos")
public class Producto {
    
    // Clave primaria con autoincremento gestionado por la base de datos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Nombre comercial del producto con validación de longitud
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;
    
    // Descripción detallada del producto (opcional)
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(length = 500)
    private String descripcion;
    
    // Precio de venta unitario, debe ser mayor que cero
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    // Porcentaje de IVA aplicable (ej: 21 para 21%)
    @NotNull(message = "El IVA es obligatorio")
    @DecimalMin(value = "0", message = "El IVA no puede ser negativo")
    @DecimalMax(value = "100", message = "El IVA no puede ser mayor a 100")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal iva;
    
    // Cantidad disponible en inventario, inicializada a 0
    @NotNull(message = "El stock actual es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual = 0;
    
    // Umbral mínimo que activa alertas de reposición
    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo = 0;
    
    // Categoría para clasificación y filtrado de productos
    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    @Column(length = 50)
    private String categoria;
    
    // Indica si el producto está disponible para venta, por defecto true
    @Column(nullable = false)
    private Boolean activo = true;
    
    // Timestamp de creación del producto, inmutable
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    // Timestamp de última actualización
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Callback ejecutado antes de persistir por primera vez
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }
    
    // Callback ejecutado antes de cada actualización
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    // Constructor vacío requerido por JPA
    public Producto() {
    }
    
    // Constructor con campos mínimos para crear productos rápidamente
    public Producto(String nombre, BigDecimal precio, BigDecimal iva) {
        this.nombre = nombre;
        this.precio = precio;
        this.iva = iva;
    }
    
    // Método auxiliar que verifica si el stock está por debajo del mínimo
    // @Transient indica que no se persiste en la base de datos
    @Transient
    public boolean isStockBajo() {
        return stockActual <= stockMinimo;
    }
    
    // Getters y Setters para acceso controlado a los atributos
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public BigDecimal getPrecio() {
        return precio;
    }
    
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    
    public BigDecimal getIva() {
        return iva;
    }
    
    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }
    
    public Integer getStockActual() {
        return stockActual;
    }
    
    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }
    
    public Integer getStockMinimo() {
        return stockMinimo;
    }
    
    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
