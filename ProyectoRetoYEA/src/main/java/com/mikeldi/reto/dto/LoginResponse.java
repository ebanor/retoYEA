package com.mikeldi.reto.dto;

import java.util.Set;

public class LoginResponse {
    private String token;
    private Long id;
    private String nombre;
    private String email;
    private Set<?> roles;
    private String rol;
    
    public LoginResponse() {
    }
    
    public LoginResponse(String token, Long id, String nombre, String email, Set<?> roles) {
        this.token = token;
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.roles = roles;
    }
    
    // Getters y Setters
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
