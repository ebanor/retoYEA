package com.mikeldi.reto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// Define esta clase como entidad JPA que implementa UserDetails de Spring Security
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {
    
    // Clave primaria con autoincremento gestionado por la base de datos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Nombre completo del usuario con validación de longitud
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false)
    private String nombre;
    
    // Email único que sirve como identificador de login
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    @Column(nullable = false, unique = true)
    private String email;
    
    // Contraseña cifrada con BCrypt, nunca se almacena en texto plano
    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String password;
    
    // Lista de roles del usuario almacenada en tabla separada usuario_roles
    // FetchType.EAGER carga roles inmediatamente para Spring Security
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "role")
    private List<Role> roles;
    
    // Indica si el usuario puede acceder al sistema, por defecto true
    @Column(nullable = false)
    private Boolean activo = true;
    
    // Timestamp de creación del usuario, inmutable
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
    }
    
    // Callback ejecutado antes de cada actualización
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    // Constructor vacío requerido por JPA
    public Usuario() {
    }
    
    // Constructor con parámetros para crear usuarios con datos completos
    public Usuario(String nombre, String email, String password, List<Role> roles) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
    
    // Convierte los roles a GrantedAuthority para Spring Security
    // Añade prefijo "ROLE_" requerido por las anotaciones @PreAuthorize
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getValue()))
                .collect(Collectors.toList());
    }
    
    // Retorna el email como identificador de usuario para Spring Security
    @Override
    public String getUsername() {
        return email;
    }
    
    // Indica si la cuenta ha expirado, siempre true (no implementado)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    // Indica si la cuenta está bloqueada, usa el campo activo
    @Override
    public boolean isAccountNonLocked() {
        return activo;
    }
    
    // Indica si las credenciales han expirado, siempre true (no implementado)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    // Indica si el usuario está habilitado, usa el campo activo
    @Override
    public boolean isEnabled() {
        return activo;
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
