package com.mikeldi.reto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

// DTO para transferencia de datos de clientes entre capas de la aplicación
public class ClienteDTO {
    
    // Identificador único del cliente en la base de datos
    private Long id;
    
    // Valida que el nombre no esté vacío y tenga longitud apropiada
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;
    
    // Valida formato de NIF/CIF español con expresión regular
    // Acepta formato DNI (12345678A) o CIF (A1234567B)
    @NotBlank(message = "El NIF/CIF es obligatorio")
    @Pattern(regexp = "^[0-9]{8}[A-Z]$|^[A-Z][0-9]{7}[A-Z]$", 
             message = "Formato de NIF/CIF inválido (ej: 12345678A o A1234567B)")
    private String nif;
    
    // Valida que el email tenga formato correcto
    @Email(message = "Email inválido")
    private String email;
    
    // Campos opcionales para información de contacto y ubicación
    private String telefono;
    private String direccion;
    private String codigoPostal;
    private String ciudad;
    private String provincia;
    
    // Indica si el cliente está activo o dado de baja
    private Boolean activo;
    
    // Timestamp de cuándo se registró el cliente en el sistema
    private LocalDateTime fechaCreacion;
    
    // Constructor vacío requerido por frameworks como Jackson para deserialización JSON
    public ClienteDTO() {
    }
    
    // Constructor con todos los parámetros para facilitar la creación de objetos completos
    public ClienteDTO(Long id, String nombre, String nif, String email, String telefono, 
                      String direccion, String codigoPostal, String ciudad, String provincia, 
                      Boolean activo, LocalDateTime fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.nif = nif;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.codigoPostal = codigoPostal;
        this.ciudad = ciudad;
        this.provincia = provincia;
        this.activo = activo;
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
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
