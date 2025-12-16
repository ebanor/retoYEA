package com.mikeldi.reto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

// Define esta clase como entidad JPA que se mapea a la tabla "clientes"
@Entity
@Table(name = "clientes")
public class Cliente {
    
    // Clave primaria con autoincremento gestionado por la base de datos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Nombre del cliente: campo obligatorio con límite de caracteres
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;
    
    // NIF/CIF: identificador fiscal único con validación de formato español
    @NotBlank(message = "El NIF/CIF es obligatorio")
    @Pattern(regexp = "^[0-9]{8}[A-Z]$|^[A-Z][0-9]{7}[A-Z]$", 
             message = "Formato de NIF/CIF inválido")
    @Column(nullable = false, unique = true, length = 9)
    private String nif;
    
    // Email de contacto con validación de formato
    @Email(message = "Email inválido")
    @Column(length = 100)
    private String email;
    
    // Teléfono de contacto (opcional)
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Column(length = 20)
    private String telefono;
    
    // Dirección postal completa del cliente
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    @Column(length = 200)
    private String direccion;
    
    // Código postal con nombre de columna personalizado en BD
    @Size(max = 10, message = "El código postal no puede exceder 10 caracteres")
    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;
    
    // Ciudad donde reside el cliente
    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    @Column(length = 100)
    private String ciudad;
    
    // Provincia para organización territorial
    @Size(max = 100, message = "La provincia no puede exceder 100 caracteres")
    @Column(length = 100)
    private String provincia;
    
    // Indica si el cliente está activo o dado de baja, por defecto true
    @Column(nullable = false)
    private Boolean activo = true;
    
    // Timestamp de creación, no se actualiza después
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    // Timestamp de última modificación, se actualiza automáticamente
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Callback que se ejecuta antes de persistir por primera vez
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }
    
    // Callback que se ejecuta antes de cada actualización
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    // Constructor vacío requerido por JPA para instanciación
    public Cliente() {
    }
    
    // Constructor con campos mínimos para facilitar creación de objetos
    public Cliente(String nombre, String nif, String email) {
        this.nombre = nombre;
        this.nif = nif;
        this.email = email;
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
    
    public String getNif() {
        return nif;
    }
    
    public void setNif(String nif) {
        this.nif = nif;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public String getCodigoPostal() {
        return codigoPostal;
    }
    
    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }
    
    public String getCiudad() {
        return ciudad;
    }
    
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
    
    public String getProvincia() {
        return provincia;
    }
    
    public void setProvincia(String provincia) {
        this.provincia = provincia;
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
