package com.mikeldi.reto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

// Define esta clase como entidad JPA que se mapea a la tabla "movimientos_stock"
@Entity
@Table(name = "movimientos_stock")
public class MovimientoStock {
    
    // Clave primaria con autoincremento gestionado por la base de datos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Relación muchos a uno con Producto: el producto afectado por este movimiento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    @NotNull(message = "El producto es obligatorio")
    private Producto producto;
    
    // Tipo de movimiento: ENTRADA, SALIDA o AJUSTE
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimiento tipo;
    
    // Cantidad de unidades afectadas en el movimiento
    @NotNull(message = "La cantidad es obligatoria")
    @Column(nullable = false)
    private Integer cantidad;
    
    // Stock antes de aplicar el movimiento (para auditoría)
    @Column(name = "stock_anterior", nullable = false)
    private Integer stockAnterior;
    
    // Stock después de aplicar el movimiento
    @Column(name = "stock_nuevo", nullable = false)
    private Integer stockNuevo;
    
    // Usuario que realizó el movimiento (para trazabilidad)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    // Pedido asociado si el movimiento es automático por venta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;
    
    // Descripción o justificación del movimiento
    @Column(length = 500)
    private String motivo;
    
    // Timestamp inmutable del momento exacto del movimiento
    @Column(name = "fecha_movimiento", nullable = false, updatable = false)
    private LocalDateTime fechaMovimiento;
    
    // Callback que establece automáticamente la fecha al crear el registro
    @PrePersist
    protected void onCreate() {
        fechaMovimiento = LocalDateTime.now();
    }
    
    // Constructor vacío requerido por JPA
    public MovimientoStock() {
    }
    
    // Constructor que crea un movimiento y calcula automáticamente el nuevo stock
    public MovimientoStock(Producto producto, TipoMovimiento tipo, Integer cantidad, String motivo, Usuario usuario) {
        this.producto = producto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        // Captura el stock actual antes del movimiento
        this.stockAnterior = producto.getStockActual();
        this.motivo = motivo;
        this.usuario = usuario;
        
        // Calcula el nuevo stock según el tipo de movimiento
        // ENTRADA y AJUSTE incrementan, SALIDA decrementa
        if (tipo == TipoMovimiento.ENTRADA || tipo == TipoMovimiento.AJUSTE) {
            this.stockNuevo = stockAnterior + cantidad;
        } else {
            this.stockNuevo = stockAnterior - cantidad;
        }
    }
    
    // Getters y Setters para acceso controlado a los atributos
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
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
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public Pedido getPedido() {
        return pedido;
    }
    
    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
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
}
