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

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional
    public UsuarioDTO crearUsuario(RegistroRequest request) {
        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }
        
        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Asignar roles (si no se especifican, asignar COMERCIAL por defecto)
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            usuario.setRoles(List.of(Role.COMERCIAL));
        } else {
            usuario.setRoles(request.getRoles());
        }
        
        usuario.setActivo(true);
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        return convertirADTO(usuarioGuardado);
    }
    
    @Transactional
    public UsuarioDTO crearNuevoUsuario(UsuarioDTO usuarioDTO) {
        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }
        
        // Validar que se proporcione contraseña
        if (usuarioDTO.getPassword() == null || usuarioDTO.getPassword().isEmpty()) {
            throw new BadRequestException("La contraseña es obligatoria para crear nuevos usuarios");
        }
        
        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        
        // Asignar roles
        if (usuarioDTO.getRoles() != null && !usuarioDTO.getRoles().isEmpty()) {
            usuario.setRoles(usuarioDTO.getRoles());
        } else {
            usuario.setRoles(List.of(Role.COMERCIAL));
        }
        
        usuario.setActivo(usuarioDTO.getActivo() != null ? usuarioDTO.getActivo() : true);
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        return convertirADTO(usuarioGuardado);
    }
    
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        return convertirADTO(usuario);
    }
    
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));
        return convertirADTO(usuario);
    }
    
    @Transactional
    public UsuarioDTO actualizarUsuario(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        // Actualizar nombre
        if (usuarioDTO.getNombre() != null && !usuarioDTO.getNombre().isEmpty()) {
            usuario.setNombre(usuarioDTO.getNombre());
        }
        
        // Validar y actualizar email
        if (usuarioDTO.getEmail() != null && !usuarioDTO.getEmail().isEmpty() && 
            !usuarioDTO.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
                throw new BadRequestException("El email ya está registrado");
            }
            usuario.setEmail(usuarioDTO.getEmail());
        }
        
        // Actualizar contraseña SOLO si se proporciona y no está vacía
        if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        }
        
        // Actualizar roles
        if (usuarioDTO.getRoles() != null && !usuarioDTO.getRoles().isEmpty()) {
            usuario.setRoles(usuarioDTO.getRoles());
        }
        
        // Actualizar estado
        if (usuarioDTO.getActivo() != null) {
            usuario.setActivo(usuarioDTO.getActivo());
        }
        
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        
        return convertirADTO(usuarioActualizado);
    }
    
    @Transactional
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        usuarioRepository.delete(usuario);
    }
    
    @Transactional
    public UsuarioDTO cambiarEstado(Long id, boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        usuario.setActivo(activo);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        
        return convertirADTO(usuarioActualizado);
    }
    
    @Transactional
    public UsuarioDTO cambiarRol(Long id, List<Role> roles) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        usuario.setRoles(roles);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        
        return convertirADTO(usuarioActualizado);
    }
    
    // Método auxiliar para convertir entidad a DTO
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
