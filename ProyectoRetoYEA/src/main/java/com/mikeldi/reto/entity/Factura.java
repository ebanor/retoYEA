package com.mikeldi.reto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Define esta clase como entidad JPA que se mapea a la tabla "facturas"
@Entity
@Table(name = "facturas")
public class Factura {
    
    // Clave primaria con autoincremento gestionado por la base de datos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Número único y secuencial de la factura (ej: FAC-2025-0001)
    @Column(name = "numero_factura", unique = true, nullable = false, length = 20)
    private String numeroFactura;
    
    // Relación muchos a uno con Pedido: una factura se genera desde un pedido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @NotNull(message = "El pedido es obligatorio")
    private Pedido pedido;
    
    // Relación muchos a uno con Cliente: varias facturas pueden pertenecer a un cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "El cliente es obligatorio")
    private Cliente cliente;
    
    // Fecha en que se emite la factura
    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;
    
    // Fecha límite de pago de la factura
    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;
    
    // Estado actual de la factura, se almacena como string en la BD
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoFactura estado = EstadoFactura.PENDIENTE;
    
    // Importe sin IVA con precisión de 2 decimales
    @Column(name = "total_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalBase;
    
    // Importe del IVA aplicado
    @Column(name = "total_iva", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalIva;
    
    // Importe total a pagar (base + IVA)
    @Column(name = "total_final", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalFinal;
    
    // Campo opcional para notas o comentarios adicionales
    @Column(length = 500)
    private String observaciones;
    
    // Timestamp de creación de la factura, no se modifica
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
        // Establece fecha de emisión si no está definida
        if (fechaEmision == null) {
            fechaEmision = LocalDate.now();
        }
        // Genera número de factura automáticamente si no existe
        if (numeroFactura == null) {
            generarNumeroFactura();
        }
    }
    
    // Callback ejecutado antes de cada actualización
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    // Genera un número de factura único con formato FAC-YYYY-XXXX
    private void generarNumeroFactura() {
        int year = LocalDate.now().getYear();
        // Usa timestamp para generar número único (en producción usar secuencia de BD)
        this.numeroFactura = String.format("FAC-%d-%04d", year, System.currentTimeMillis() % 10000);
    }
    
    // Constructor vacío requerido por JPA
    public Factura() {
    }
    
    // Constructor que crea una factura desde un pedido copiando sus datos
    public Factura(Pedido pedido) {
        this.pedido = pedido;
        // Copia el cliente del pedido
        this.cliente = pedido.getCliente();
        // Copia los totales calculados del pedido
        this.totalBase = pedido.getTotalBase();
        this.totalIva = pedido.getTotalIva();
        this.totalFinal = pedido.getTotalFinal();
        // Establece fecha de emisión como hoy
        this.fechaEmision = LocalDate.now();
        // Establece vencimiento a 30 días
        this.fechaVencimiento = LocalDate.now().plusDays(30);
    }
    
    // Getters y Setters para acceso controlado a los atributos
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
