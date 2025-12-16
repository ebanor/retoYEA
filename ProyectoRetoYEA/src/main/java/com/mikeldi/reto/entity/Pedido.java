package com.mikeldi.reto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Define esta clase como entidad JPA que se mapea a la tabla "pedidos"
@Entity
@Table(name = "pedidos")
public class Pedido {
    
    // Clave primaria con autoincremento gestionado por la base de datos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Relación muchos a uno con Cliente: el cliente que realiza el pedido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "El cliente es obligatorio")
    private Cliente cliente;
    
    // Relación muchos a uno con Usuario: el comercial que creó el pedido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull(message = "El usuario es obligatorio")
    private Usuario usuario;
    
    // Estado actual del pedido, por defecto PENDIENTE al crear
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;
    
    // Fecha y hora en que se realizó el pedido
    @Column(name = "fecha_pedido", nullable = false)
    private LocalDateTime fechaPedido;
    
    // Total sin IVA sumando todas las líneas
    @Column(name = "total_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalBase = BigDecimal.ZERO;
    
    // Total de IVA sumando todas las líneas
    @Column(name = "total_iva", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalIva = BigDecimal.ZERO;
    
    // Total final a pagar (base + IVA)
    @Column(name = "total_final", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalFinal = BigDecimal.ZERO;
    
    // Campo opcional para notas o instrucciones especiales
    @Column(length = 500)
    private String observaciones;
    
    // Relación uno a muchos con LineaPedido: lista de productos en el pedido
    // CascadeType.ALL propaga operaciones a las líneas, orphanRemoval elimina líneas huérfanas
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineaPedido> lineas = new ArrayList<>();
    
    // Timestamp de creación del pedido, inmutable
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
        // Establece fecha del pedido si no está definida
        if (fechaPedido == null) {
            fechaPedido = LocalDateTime.now();
        }
    }
    
    // Callback ejecutado antes de cada actualización
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    // Método para añadir línea de pedido manteniendo la bidireccionalidad
    public void addLinea(LineaPedido linea) {
        lineas.add(linea);
        // Establece la referencia inversa para mantener consistencia
        linea.setPedido(this);
        // Recalcula totales automáticamente
        calcularTotales();
    }
    
    // Método para remover línea de pedido manteniendo la bidireccionalidad
    public void removeLinea(LineaPedido linea) {
        lineas.remove(linea);
        // Elimina la referencia inversa
        linea.setPedido(null);
        // Recalcula totales automáticamente
        calcularTotales();
    }
    
    // Calcula totales sumando los valores de todas las líneas de pedido
    public void calcularTotales() {
        // Reinicia los totales a cero
        totalBase = BigDecimal.ZERO;
        totalIva = BigDecimal.ZERO;
        
        // Suma el subtotal e IVA de cada línea
        for (LineaPedido linea : lineas) {
            totalBase = totalBase.add(linea.getSubtotal());
            totalIva = totalIva.add(linea.getImporteIva());
        }
        
        // Calcula el total final sumando base e IVA
        totalFinal = totalBase.add(totalIva);
    }
    
    // Constructor vacío requerido por JPA
    public Pedido() {
    }
    
    // Constructor con parámetros mínimos para crear un pedido nuevo
    public Pedido(Cliente cliente, Usuario usuario) {
        this.cliente = cliente;
        this.usuario = usuario;
        this.estado = EstadoPedido.PENDIENTE;
        this.fechaPedido = LocalDateTime.now();
    }
    
    // Getters y Setters para acceso controlado a los atributos
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
