package com.mikeldi.reto.controller;

import com.mikeldi.reto.dto.LoginRequest;
import com.mikeldi.reto.dto.LoginResponse;
import com.mikeldi.reto.dto.RegistroRequest;
import com.mikeldi.reto.dto.UsuarioDTO;
import com.mikeldi.reto.entity.Usuario;
import com.mikeldi.reto.security.JwtUtil;
import com.mikeldi.reto.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

// Define este controlador como REST para retornar datos en formato JSON
@RestController
// Establece la ruta base para todos los endpoints de este controlador
@RequestMapping("/api/auth")
// Agrupa estos endpoints en Swagger bajo la categoría "Autenticación"
@Tag(name = "Autenticación", description = "Endpoints para autenticación y registro de usuarios")
public class AuthController {
    
    // Gestor de autenticación de Spring Security para validar credenciales
    @Autowired
    private AuthenticationManager authenticationManager;
    
    // Servicio de lógica de negocio para operaciones con usuarios
    @Autowired
    private UsuarioService usuarioService;
    
    // Utilidad para generar y validar tokens JWT
    @Autowired
    private JwtUtil jwtUtil;
    
    // Endpoint POST para iniciar sesión
    @PostMapping("/login")
    // Documentación para Swagger
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica un usuario con email y contraseña, retorna un token JWT"
    )
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Crea un token de autenticación con las credenciales recibidas
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            
            // Establece el usuario autenticado en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Genera un token JWT válido para el usuario autenticado
            String jwt = jwtUtil.generateJwtToken(authentication);
            
            // Obtiene los detalles completos del usuario desde el objeto de autenticación
            Usuario usuario = (Usuario) authentication.getPrincipal();
            
            // Extrae el primer rol del usuario para simplificar la respuesta
            String rolPrincipal = "";
            if (usuario.getRoles() != null && !usuario.getRoles().isEmpty()) {
                rolPrincipal = usuario.getRoles().iterator().next().toString();
            }
            
            // Construye el objeto de respuesta con token y datos del usuario
            LoginResponse response = new LoginResponse();
            response.setToken(jwt);
            response.setId(usuario.getId());
            response.setNombre(usuario.getNombre());
            response.setEmail(usuario.getEmail());
            response.setRol(rolPrincipal);
            
            // Retorna respuesta exitosa con código 200 OK
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Si la autenticación falla, retorna código 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    // Endpoint POST para registrar nuevos usuarios
    @PostMapping("/register")
    // Documentación para Swagger
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea un nuevo usuario en el sistema. Solo accesible por administradores"
    )
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody RegistroRequest registroRequest) {
        // Delega la creación del usuario al servicio
        UsuarioDTO nuevoUsuario = usuarioService.crearUsuario(registroRequest);
        // Retorna el usuario creado con código 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }
}
