package com.mikeldi.reto.repository;

import com.mikeldi.reto.entity.Cliente;
import com.mikeldi.reto.entity.EstadoFactura;
import com.mikeldi.reto.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Marca esta interfaz como componente de repositorio para Spring
@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    
    // Busca una factura por su número único generado automáticamente
    // Útil para consultas directas con el número de factura visible al usuario
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    
    // Lista todas las facturas de un cliente específico ordenadas por fecha descendente
    // Muestra el historial de facturación más reciente primero
    List<Factura> findByClienteOrderByFechaEmisionDesc(Cliente cliente);
    
    // Filtra facturas por estado (PENDIENTE, PAGADA, VENCIDA, CANCELADA)
    // Ordenadas por fecha descendente para priorizar las más recientes
    List<Factura> findByEstadoOrderByFechaEmisionDesc(EstadoFactura estado);
    
    // Busca facturas emitidas dentro de un rango de fechas específico
    // Between incluye ambas fechas límite en el resultado
    // Útil para reportes mensuales o trimestrales
    List<Factura> findByFechaEmisionBetweenOrderByFechaEmisionDesc(LocalDate inicio, LocalDate fin);
    
    // Lista todas las facturas del sistema ordenadas por fecha descendente
    // Las más recientes aparecen primero en listados generales
    List<Factura> findAllByOrderByFechaEmisionDesc();
    
    // Cuenta cuántas facturas hay en un estado específico
    // Retorna solo el número sin cargar entidades, optimizando rendimiento
    Long countByEstado(EstadoFactura estado);
}
