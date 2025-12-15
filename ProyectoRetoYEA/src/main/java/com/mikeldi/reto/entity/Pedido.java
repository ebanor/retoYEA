package com.mikeldi.reto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "El cliente es obligatorio")
    private Cliente cliente;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull(message = "El usuario es obligatorio")
    private Usuario usuario;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;
    
    @Column(name = "fecha_pedido", nullable = false)
    private LocalDateTime fechaPedido;
    
    @Column(name = "total_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalBase = BigDecimal.ZERO;
    
    @Column(name = "total_iva", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalIva = BigDecimal.ZERO;
    
    @Column(name = "total_final", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalFinal = BigDecimal.ZERO;
    
    @Column(length = 500)
    private String observaciones;
    
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineaPedido> lineas = new ArrayList<>();
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (fechaPedido == null) {
            fechaPedido = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    // Método para añadir línea de pedido
    public void addLinea(LineaPedido linea) {
        lineas.add(linea);
        linea.setPedido(this);
        calcularTotales();
    }
    
    // Método para remover línea de pedido
    public void removeLinea(LineaPedido linea) {
        lineas.remove(linea);
        linea.setPedido(null);
        calcularTotales();
    }
    
    // Calcular totales automáticamente
    public void calcularTotales() {
        totalBase = BigDecimal.ZERO;
        totalIva = BigDecimal.ZERO;
        
        for (LineaPedido linea : lineas) {
            totalBase = totalBase.add(linea.getSubtotal());
            totalIva = totalIva.add(linea.getImporteIva());
        }
        
        totalFinal = totalBase.add(totalIva);
    }
    
    // Constructores
    public Pedido() {
    }
    
    public Pedido(Cliente cliente, Usuario usuario) {
        this.cliente = cliente;
        this.usuario = usuario;
        this.estado = EstadoPedido.PENDIENTE;
        this.fechaPedido = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
    
    public List<LineaPedido> getLineas() {
        return lineas;
    }
    
    public void setLineas(List<LineaPedido> lineas) {
        this.lineas = lineas;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
