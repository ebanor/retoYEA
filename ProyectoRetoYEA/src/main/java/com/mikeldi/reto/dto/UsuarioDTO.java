package com.mikeldi.reto.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.mikeldi.reto.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class UsuarioDTO {
    
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;
    
    private String password;
    
    private List<Role> roles;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    
    public UsuarioDTO() {
    }
    
    public UsuarioDTO(Long id, String nombre, String email, List<Role> roles, Boolean activo, LocalDateTime fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.roles = roles;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }
    
    public UsuarioDTO(String nombre, String email, String password, List<Role> roles) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
    
    // Getters y Setters
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
    
    // NUEVO: Método especial para Jackson que convierte strings a Role
    @JsonSetter("roles")
    public void setRolesFromJson(Object rolesObj) {
        if (rolesObj instanceof List<?>) {
            List<?> rolesList = (List<?>) rolesObj;
            if (!rolesList.isEmpty()) {
                Object first = rolesList.get(0);
                if (first instanceof String) {
                    // Si son strings, convertir a Role
                    this.roles = rolesList.stream()
                            .map(obj -> Role.valueOf(((String) obj).toUpperCase()))
                            .collect(Collectors.toList());
                } else if (first instanceof Role) {
                    // Si ya son Role, usar directamente
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
