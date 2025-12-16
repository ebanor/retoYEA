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

// Define este controlador como REST para retornar datos en formato JSON
@RestController
// Establece la ruta base /api/usuarios para todos los endpoints
@RequestMapping("/api/usuarios")
// Agrupa estos endpoints en Swagger bajo la categoría "Usuarios"
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
// Indica en Swagger que todos los endpoints requieren autenticación JWT
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {
    
    // Servicio para la lógica de negocio de usuarios
    @Autowired
    private UsuarioService usuarioService;
    
    // Endpoint GET para que el usuario obtenga su propio perfil
    @GetMapping("/me")
    @Operation(summary = "Obtener perfil del usuario actual")
    public ResponseEntity<UsuarioDTO> obtenerPerfil() {
        // Extrae el email del usuario autenticado del contexto de seguridad
        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        
        // Retorna los datos del usuario actual sin necesidad de conocer su ID
        UsuarioDTO usuarioDTO = usuarioService.obtenerPorEmail(email);
        return ResponseEntity.ok(usuarioDTO);
    }
    
    // Endpoint GET para listar todos los usuarios del sistema
    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }
    
    // Endpoint GET para obtener un usuario específico por ID
    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<UsuarioDTO> obtenerUsuario(@PathVariable Long id) {
        UsuarioDTO usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(usuario);
    }
    
    // Endpoint POST para crear un nuevo usuario en el sistema
    @PostMapping
    @Operation(summary = "Crear nuevo usuario")
    public ResponseEntity<UsuarioDTO> crearUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        // Crea el usuario y cifra automáticamente la contraseña
        UsuarioDTO usuarioCreado = usuarioService.crearNuevoUsuario(usuarioDTO);
        return ResponseEntity.ok(usuarioCreado);
    }
    
    // Endpoint PUT para actualizar todos los datos de un usuario
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO usuarioActualizado = usuarioService.actualizarUsuario(id, usuarioDTO);
        return ResponseEntity.ok(usuarioActualizado);
    }
    
    // Endpoint DELETE para eliminar permanentemente un usuario
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        // Retorna código 204 No Content indicando éxito sin cuerpo
        return ResponseEntity.noContent().build();
    }
    
    // Endpoint PATCH para activar o desactivar un usuario
    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado del usuario")
    public ResponseEntity<UsuarioDTO> cambiarEstado(
            @PathVariable Long id,
            // Booleano para el nuevo estado activo/inactivo
            @RequestParam boolean activo) {
        // Desactivar usuarios es mejor que eliminarlos para mantener auditoría
        UsuarioDTO usuario = usuarioService.cambiarEstado(id, activo);
        return ResponseEntity.ok(usuario);
    }
    
    // Endpoint PATCH para cambiar el rol de un usuario
    @PatchMapping("/{id}/rol")
    @Operation(summary = "Cambiar rol del usuario")
    public ResponseEntity<UsuarioDTO> cambiarRol(
            @PathVariable Long id,
            // String con el nombre del nuevo rol
            @RequestParam String rol) {
        // Convierte el string a enum Role
        Role role = roleStringToRole(rol);
        // Actualiza el rol del usuario en el sistema
        UsuarioDTO usuario = usuarioService.cambiarRol(id, List.of(role));
        return ResponseEntity.ok(usuario);
    }
    
    // Método auxiliar para convertir string a enum Role con validación
    private Role roleStringToRole(String rolString) {
        try {
            // Convierte a mayúsculas para evitar errores de capitalización
            return Role.valueOf(rolString.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Lanza excepción descriptiva si el rol no existe
            throw new IllegalArgumentException("Rol inválido: " + rolString);
        }
    }
}
