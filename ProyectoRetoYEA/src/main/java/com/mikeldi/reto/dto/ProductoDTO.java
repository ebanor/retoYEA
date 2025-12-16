package com.mikeldi.reto.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// DTO que representa un producto del catálogo con validaciones de negocio
public class ProductoDTO {
    
    // Identificador único del producto en la base de datos
    private Long id;
    
    // Nombre comercial del producto con validación de longitud
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;
    
    // Descripción detallada del producto (opcional)
    private String descripcion;
    
    // Precio de venta unitario, debe ser mayor que cero
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0")
    private BigDecimal precio;
    
    // Porcentaje de IVA aplicable (ej: 21 para 21%)
    @NotNull(message = "El IVA es obligatorio")
    @DecimalMin(value = "0", message = "El IVA no puede ser negativo")
    @DecimalMax(value = "100", message = "El IVA no puede ser mayor a 100")
    private BigDecimal iva;
    
    // Cantidad disponible en inventario, no puede ser negativo
    @NotNull(message = "El stock actual es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stockActual;
    
    // Umbral mínimo antes de generar alerta de reposición
    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;
    
    // Categoría para clasificación y filtrado de productos
    private String categoria;
    
    // Indica si el producto está disponible para venta
    private Boolean activo;
    
    // Flag calculado: true cuando stockActual <= stockMinimo
    private Boolean stockBajo;
    
    // Timestamp de cuándo se registró el producto en el sistema
    private LocalDateTime fechaCreacion;
    
    // Constructor vacío para deserialización JSON
    public ProductoDTO() {
    }
    
    // Constructor completo para facilitar creación de objetos en servicios
    public ProductoDTO(Long id, String nombre, String descripcion, BigDecimal precio, 
                       BigDecimal iva, Integer stockActual, Integer stockMinimo, 
                       String categoria, Boolean activo, Boolean stockBajo, LocalDateTime fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.iva = iva;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.categoria = categoria;
        this.activo = activo;
        this.stockBajo = stockBajo;
        this.fechaCreacion = fechaCreacion;
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
    
    public Boolean getStockBajo() {
        return stockBajo;
    }
    
    public void setStockBajo(Boolean stockBajo) {
        this.stockBajo = stockBajo;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
