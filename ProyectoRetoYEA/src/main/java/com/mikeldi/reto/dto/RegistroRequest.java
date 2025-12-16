package com.mikeldi.reto.dto;

import com.mikeldi.reto.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

// DTO para encapsular los datos necesarios al registrar un nuevo usuario
public class RegistroRequest {
    
    // Nombre completo del usuario a crear
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;
    
    // Email único que servirá como identificador de login
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;
    
    // Contraseña en texto plano que será cifrada antes de almacenarla
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    // Lista de roles asignados al nuevo usuario (ADMIN, COMERCIAL, ALMACEN)
    private List<Role> roles;
    
    // Constructor vacío para deserialización JSON
    public RegistroRequest() {
    }
    
    // Getters y Setters para acceso a los atributos
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
}
