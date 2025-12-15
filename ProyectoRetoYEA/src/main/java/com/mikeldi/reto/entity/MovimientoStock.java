package com.mikeldi.reto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_stock")
public class MovimientoStock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    @NotNull(message = "El producto es obligatorio")
    private Producto producto;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimiento tipo;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(name = "stock_anterior", nullable = false)
    private Integer stockAnterior;
    
    @Column(name = "stock_nuevo", nullable = false)
    private Integer stockNuevo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;
    
    @Column(length = 500)
    private String motivo;
    
    @Column(name = "fecha_movimiento", nullable = false, updatable = false)
    private LocalDateTime fechaMovimiento;
    
    @PrePersist
    protected void onCreate() {
        fechaMovimiento = LocalDateTime.now();
    }
    
    // Constructores
    public MovimientoStock() {
    }
    
    public MovimientoStock(Producto producto, TipoMovimiento tipo, Integer cantidad, String motivo, Usuario usuario) {
        this.producto = producto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.stockAnterior = producto.getStockActual();
        this.motivo = motivo;
        this.usuario = usuario;
        
        // Calcular nuevo stock
        if (tipo == TipoMovimiento.ENTRADA || tipo == TipoMovimiento.AJUSTE) {
            this.stockNuevo = stockAnterior + cantidad;
        } else {
            this.stockNuevo = stockAnterior - cantidad;
        }
    }
    
    // Getters y Setters
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
