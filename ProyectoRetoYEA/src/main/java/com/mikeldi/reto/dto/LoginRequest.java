package com.mikeldi.reto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// DTO que encapsula las credenciales de inicio de sesión
public class LoginRequest {
    
    // Email del usuario que intenta autenticarse
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;
    
    // Contraseña en texto plano (se cifrará al validar contra la BD)
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    
    // Constructor vacío para deserialización JSON
    public LoginRequest() {
    }
    
    // Getter para obtener el email proporcionado
    public String getEmail() {
        return email;
    }
    
    // Setter para establecer el email desde la petición
    public void setEmail(String email) {
        this.email = email;
    }
    
    // Getter para obtener la contraseña proporcionada
    public String getPassword() {
        return password;
    }
    
    // Setter para establecer la contraseña desde la petición
    public void setPassword(String password) {
        this.password = password;
    }
}
