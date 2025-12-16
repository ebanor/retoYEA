package com.mikeldi.reto.repository;

import com.mikeldi.reto.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Marca esta interfaz como componente de repositorio para Spring
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Busca un usuario por su email único
    // Retorna Optional para manejar casos donde el usuario no existe
    // Usado principalmente en el proceso de autenticación por Spring Security
    Optional<Usuario> findByEmail(String email);
    
    // Verifica si existe un usuario con el email especificado
    // Útil para validaciones durante el registro de nuevos usuarios
    // Evita duplicados sin cargar la entidad completa
    Boolean existsByEmail(String email);
}
