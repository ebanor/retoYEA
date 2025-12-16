package com.mikeldi.reto.service;

import com.mikeldi.reto.dto.RegistroRequest;
import com.mikeldi.reto.dto.UsuarioDTO;
import com.mikeldi.reto.entity.Role;
import com.mikeldi.reto.entity.Usuario;
import com.mikeldi.reto.exception.BadRequestException;
import com.mikeldi.reto.exception.ResourceNotFoundException;
import com.mikeldi.reto.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Servicio que gestiona usuarios del sistema, autenticación y control de acceso
@Service
public class UsuarioService {
    
    // Inyecta repositorio para operaciones CRUD de usuarios
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    // Inyecta encoder de Spring Security para hash seguro de contraseñas
    // Usa BCrypt por defecto, protegiendo contraseñas con salt aleatorio
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Crea un nuevo usuario desde el formulario de registro público
    @Transactional
    public UsuarioDTO crearUsuario(RegistroRequest request) {
        // Valida que el email no esté ya registrado en el sistema
        // Previene duplicados y conflictos en el login
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }
        
        // Crea la entidad usuario con los datos del request
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        // Encripta la contraseña antes de guardar usando BCrypt
        // Nunca se almacenan contraseñas en texto plano
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Asigna roles al usuario nuevo
        // Si no se especifican, asigna COMERCIAL como rol por defecto
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            usuario.setRoles(List.of(Role.COMERCIAL));
        } else {
            usuario.setRoles(request.getRoles());
        }
        
        // Todos los usuarios nuevos se crean como activos
        usuario.setActivo(true);
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        return convertirADTO(usuarioGuardado);
    }
    
    // Crea un usuario desde el panel de administración con DTO completo
    @Transactional
    public UsuarioDTO crearNuevoUsuario(UsuarioDTO usuarioDTO) {
        // Valida unicidad del email antes de crear
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }
        
        // Valida que se proporcione contraseña para usuarios nuevos
        // La contraseña es obligatoria en la creación pero opcional en actualización
        if (usuarioDTO.getPassword() == null || usuarioDTO.getPassword().isEmpty()) {
            throw new BadRequestException("La contraseña es obligatoria para crear nuevos usuarios");
        }
        
        // Construye el usuario con los datos del DTO
        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setEmail(usuarioDTO.getEmail());
        // Hash de la contraseña usando BCrypt
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        
        // Asigna roles especificados o COMERCIAL por defecto
        if (usuarioDTO.getRoles() != null && !usuarioDTO.getRoles().isEmpty()) {
            usuario.setRoles(usuarioDTO.getRoles());
        } else {
            usuario.setRoles(List.of(Role.COMERCIAL));
        }
        
        // Permite especificar estado inicial o usa activo por defecto
        usuario.setActivo(usuarioDTO.getActivo() != null ? usuarioDTO.getActivo() : true);
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        return convertirADTO(usuarioGuardado);
    }
    
    // Lista todos los usuarios del sistema incluyendo inactivos
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // Obtiene un usuario específico por su ID
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        return convertirADTO(usuario);
    }
    
    // Busca un usuario por su email (usado en autenticación)
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorEmail(String email) {
        // Spring Security usa este método para cargar datos del usuario al hacer login
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));
        return convertirADTO(usuario);
    }
    
    // Actualiza datos de un usuario existente con validaciones
    @Transactional
    public UsuarioDTO actualizarUsuario(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        // Actualiza nombre solo si se proporciona un valor válido
        if (usuarioDTO.getNombre() != null && !usuarioDTO.getNombre().isEmpty()) {
            usuario.setNombre(usuarioDTO.getNombre());
        }
        
        // Valida y actualiza email verificando que no esté duplicado
        // Solo valida si el email es diferente al actual
        if (usuarioDTO.getEmail() != null && !usuarioDTO.getEmail().isEmpty() && 
            !usuarioDTO.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
                throw new BadRequestException("El email ya está registrado");
            }
            usuario.setEmail(usuarioDTO.getEmail());
        }
        
        // Actualiza contraseña SOLO si se proporciona explícitamente
        // Permite actualizaciones sin cambiar la contraseña actual
        if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        }
        
        // Actualiza roles si se proporcionan nuevos
        if (usuarioDTO.getRoles() != null && !usuarioDTO.getRoles().isEmpty()) {
            usuario.setRoles(usuarioDTO.getRoles());
        }
        
        // Actualiza estado activo/inactivo si se especifica
        if (usuarioDTO.getActivo() != null) {
            usuario.setActivo(usuarioDTO.getActivo());
        }
        
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        
        return convertirADTO(usuarioActualizado);
    }
    
    // Elimina permanentemente un usuario del sistema
    @Transactional
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        // Eliminación física (mejor práctica: usar cambiarEstado para soft delete)
        usuarioRepository.delete(usuario);
    }
    
    // Activa o desactiva un usuario sin eliminarlo (soft delete)
    @Transactional
    public UsuarioDTO cambiarEstado(Long id, boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        // Cambia el estado preservando el histórico de operaciones
        // Usuarios inactivos no pueden autenticarse
        usuario.setActivo(activo);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        
        return convertirADTO(usuarioActualizado);
    }
    
    // Cambia los roles de un usuario modificando sus permisos
    @Transactional
    public UsuarioDTO cambiarRol(Long id, List<Role> roles) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        // Actualiza roles para cambiar permisos de acceso
        // Ejemplos: ADMIN, COMERCIAL, ALMACEN
        usuario.setRoles(roles);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        
        return convertirADTO(usuarioActualizado);
    }
    
    // Convierte entidad Usuario a DTO excluyendo la contraseña
    // Nunca expone el hash de contraseña en respuestas API
    private UsuarioDTO convertirADTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRoles(),
                usuario.getActivo(),
                usuario.getFechaCreacion()
        );
    }
}
