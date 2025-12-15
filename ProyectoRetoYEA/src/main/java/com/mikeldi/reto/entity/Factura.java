package com.mikeldi.reto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "facturas")
public class Factura {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_factura", unique = true, nullable = false, length = 20)
    private String numeroFactura;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @NotNull(message = "El pedido es obligatorio")
    private Pedido pedido;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "El cliente es obligatorio")
    private Cliente cliente;
    
    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;
    
    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoFactura estado = EstadoFactura.PENDIENTE;
    
    @Column(name = "total_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalBase;
    
    @Column(name = "total_iva", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalIva;
    
    @Column(name = "total_final", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalFinal;
    
    @Column(length = 500)
    private String observaciones;
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (fechaEmision == null) {
            fechaEmision = LocalDate.now();
        }
        if (numeroFactura == null) {
            generarNumeroFactura();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    private void generarNumeroFactura() {
        // Formato: FAC-YYYY-0001
        int year = LocalDate.now().getYear();
        this.numeroFactura = String.format("FAC-%d-%04d", year, System.currentTimeMillis() % 10000);
    }
    
    // Constructores
    public Factura() {
    }
    
    public Factura(Pedido pedido) {
        this.pedido = pedido;
        this.cliente = pedido.getCliente();
        this.totalBase = pedido.getTotalBase();
        this.totalIva = pedido.getTotalIva();
        this.totalFinal = pedido.getTotalFinal();
        this.fechaEmision = LocalDate.now();
        this.fechaVencimiento = LocalDate.now().plusDays(30); // 30 días de vencimiento
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNumeroFactura() {
        return numeroFactura;
    }
    
    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }
    
    public Pedido getPedido() {
        return pedido;
    }
    
    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public LocalDate getFechaEmision() {
        return fechaEmision;
    }
    
    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
    
    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }
    
    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
    
    public EstadoFactura getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoFactura estado) {
        this.estado = estado;
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
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
