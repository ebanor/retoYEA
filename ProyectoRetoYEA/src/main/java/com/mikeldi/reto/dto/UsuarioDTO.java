package com.mikeldi.reto.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.mikeldi.reto.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// DTO para transferir datos de usuarios entre capas
public class UsuarioDTO {
    
    // Identificador único del usuario en la base de datos
    private Long id;
    
    // Nombre completo del usuario con validación de longitud
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;
    
    // Email único que sirve como identificador de login
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;
    
    // Contraseña: solo se usa al crear/actualizar, nunca se expone al leer
    private String password;
    
    // Lista de roles asignados al usuario (ADMIN, COMERCIAL, ALMACEN)
    private List<Role> roles;
    
    // Indica si el usuario puede acceder al sistema
    private Boolean activo;
    
    // Timestamp de cuándo se registró el usuario
    private LocalDateTime fechaCreacion;
    
    // Constructor vacío para deserialización JSON
    public UsuarioDTO() {
    }
    
    // Constructor para consultas (lectura) sin contraseña
    public UsuarioDTO(Long id, String nombre, String email, List<Role> roles, Boolean activo, LocalDateTime fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.roles = roles;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }
    
    // Constructor para creación de usuarios con contraseña
    public UsuarioDTO(String nombre, String email, String password, List<Role> roles) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.roles = roles;
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public List<Role> getRoles() {
        return roles;
    }
    
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
    
    // Método especial para deserialización JSON que maneja conversión flexible de roles
    // Permite recibir roles como strings ["ADMIN"] o como enums desde el cliente
    @JsonSetter("roles")
    public void setRolesFromJson(Object rolesObj) {
        if (rolesObj instanceof List<?>) {
            List<?> rolesList = (List<?>) rolesObj;
            if (!rolesList.isEmpty()) {
                Object first = rolesList.get(0);
                // Si vienen como strings, convierte a enum Role
                if (first instanceof String) {
                    this.roles = rolesList.stream()
                            .map(obj -> Role.valueOf(((String) obj).toUpperCase()))
                            .collect(Collectors.toList());
                } 
                // Si ya son enums Role, usa directamente
                else if (first instanceof Role) {
                    this.roles = (List<Role>) rolesList;
                }
            }
        }
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
