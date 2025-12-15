package com.mikeldi.reto.controller;

import com.mikeldi.reto.dto.UsuarioDTO;
import com.mikeldi.reto.entity.Role;
import com.mikeldi.reto.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping("/me")
    @Operation(summary = "Obtener perfil del usuario actual")
    public ResponseEntity<UsuarioDTO> obtenerPerfil() {
        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        
        UsuarioDTO usuarioDTO = usuarioService.obtenerPorEmail(email);
        return ResponseEntity.ok(usuarioDTO);
    }
    
    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<UsuarioDTO> obtenerUsuario(@PathVariable Long id) {
        UsuarioDTO usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(usuario);
    }
    
    @PostMapping
    @Operation(summary = "Crear nuevo usuario")
    public ResponseEntity<UsuarioDTO> crearUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO usuarioCreado = usuarioService.crearNuevoUsuario(usuarioDTO);
        return ResponseEntity.ok(usuarioCreado);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO usuarioActualizado = usuarioService.actualizarUsuario(id, usuarioDTO);
        return ResponseEntity.ok(usuarioActualizado);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado del usuario")
    public ResponseEntity<UsuarioDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo) {
        UsuarioDTO usuario = usuarioService.cambiarEstado(id, activo);
        return ResponseEntity.ok(usuario);
    }
    
    @PatchMapping("/{id}/rol")
    @Operation(summary = "Cambiar rol del usuario")
    public ResponseEntity<UsuarioDTO> cambiarRol(
            @PathVariable Long id,
            @RequestParam String rol) {
        Role role = roleStringToRole(rol);
        UsuarioDTO usuario = usuarioService.cambiarRol(id, List.of(role));
        return ResponseEntity.ok(usuario);
    }
    
    private Role roleStringToRole(String rolString) {
        try {
            return Role.valueOf(rolString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol inválido: " + rolString);
        }
    }
}
