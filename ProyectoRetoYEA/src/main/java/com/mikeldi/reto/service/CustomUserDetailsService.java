package com.mikeldi.reto.service;

import com.mikeldi.reto.entity.Usuario;
import com.mikeldi.reto.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Marca esta clase como servicio de Spring
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Inyecta el repositorio para acceso a datos de usuarios
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Implementa el método requerido por Spring Security para cargar usuarios
    // Este método se llama automáticamente durante el proceso de autenticación
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca el usuario en la base de datos por su email
        // La entidad Usuario implementa UserDetails, por lo que puede retornarse directamente
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
        
        // Retorna el usuario que Spring Security usará para autenticación y autorización
        return usuario;
    }
}
