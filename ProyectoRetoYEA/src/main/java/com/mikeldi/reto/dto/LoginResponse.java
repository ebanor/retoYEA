package com.mikeldi.reto.dto;

import java.util.Set;

// DTO que encapsula la respuesta exitosa de un inicio de sesión
public class LoginResponse {
    
    // Token JWT que el cliente debe incluir en futuras peticiones
    private String token;
    
    // Identificador único del usuario autenticado
    private Long id;
    
    // Nombre completo del usuario para mostrar en la interfaz
    private String nombre;
    
    // Email del usuario autenticado
    private String email;
    
    // Conjunto de roles del usuario (puede tener múltiples)
    private Set<?> roles;
    
    // Rol principal simplificado como string para la interfaz
    private String rol;
    
    // Constructor vacío para instanciación
    public LoginResponse() {
    }
    
    // Constructor con parámetros principales para facilitar creación
    public LoginResponse(String token, Long id, String nombre, String email, Set<?> roles) {
        this.token = token;
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.roles = roles;
    }
    
    // Getters y Setters para acceso a los atributos
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
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
    
    public Set<?> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<?> roles) {
        this.roles = roles;
    }
    
    public String getRol() {
        return rol;
    }
    
    public void setRol(String rol) {
        this.rol = rol;
    }
}
