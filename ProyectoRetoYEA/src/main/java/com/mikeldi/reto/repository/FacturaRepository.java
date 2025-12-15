package com.mikeldi.reto.repository;

import com.mikeldi.reto.entity.Cliente;
import com.mikeldi.reto.entity.EstadoFactura;
import com.mikeldi.reto.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    
    List<Factura> findByClienteOrderByFechaEmisionDesc(Cliente cliente);
    
    List<Factura> findByEstadoOrderByFechaEmisionDesc(EstadoFactura estado);
    
    List<Factura> findByFechaEmisionBetweenOrderByFechaEmisionDesc(LocalDate inicio, LocalDate fin);
    
    List<Factura> findAllByOrderByFechaEmisionDesc();
    
    Long countByEstado(EstadoFactura estado);
}
