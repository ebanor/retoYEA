package com.mikeldi.reto.repository;

import com.mikeldi.reto.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Marca esta interfaz como componente de repositorio para Spring
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    // Busca un cliente por su NIF/CIF único
    // Retorna Optional para manejar casos donde no existe
    Optional<Cliente> findByNif(String nif);
    
    // Verifica si existe un cliente con el NIF especificado
    // Útil para validaciones antes de crear nuevos clientes
    Boolean existsByNif(String nif);
    
    // Busca clientes cuyo nombre contenga el texto especificado (búsqueda parcial)
    // IgnoreCase hace la búsqueda insensible a mayúsculas/minúsculas
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);
    
    // Lista todos los clientes según su estado activo/inactivo
    List<Cliente> findByActivo(Boolean activo);
    
    // Filtra clientes por ciudad para análisis geográfico
    List<Cliente> findByCiudad(String ciudad);
    
    // Filtra clientes por provincia para análisis regional
    List<Cliente> findByProvincia(String provincia);
}
