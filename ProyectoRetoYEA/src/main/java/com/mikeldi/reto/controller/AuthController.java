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

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints para autenticación y registro de usuarios")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica un usuario con email y contraseña, retorna un token JWT"
    )
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // Autenticar usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generar token JWT
        String jwt = jwtUtil.generateJwtToken(authentication);
        
        // Obtener detalles del usuario
        Usuario usuario = (Usuario) authentication.getPrincipal();
        
        // Crear respuesta
        LoginResponse response = new LoginResponse(
                jwt,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRoles()
        );
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea un nuevo usuario en el sistema. Solo accesible por administradores"
    )
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody RegistroRequest registroRequest) {
        UsuarioDTO nuevoUsuario = usuarioService.crearUsuario(registroRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }
}
