package com.mikeldi.reto.repository;

import com.mikeldi.reto.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    Optional<Cliente> findByNif(String nif);
    
    Boolean existsByNif(String nif);
    
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);
    
    List<Cliente> findByActivo(Boolean activo);
    
    List<Cliente> findByCiudad(String ciudad);
    
    List<Cliente> findByProvincia(String provincia);
}
