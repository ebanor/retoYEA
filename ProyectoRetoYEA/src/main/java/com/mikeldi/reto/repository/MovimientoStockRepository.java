package com.mikeldi.reto.repository;

import com.mikeldi.reto.entity.MovimientoStock;
import com.mikeldi.reto.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

// Marca esta interfaz como componente de repositorio para Spring
@Repository
public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {
    
    // Busca todos los movimientos de un producto específico ordenados por fecha descendente
    // Muestra el historial completo de entradas, salidas y ajustes del producto
    List<MovimientoStock> findByProductoOrderByFechaMovimientoDesc(Producto producto);
    
    // Busca movimientos dentro de un rango de fechas específico
    // Between incluye ambos extremos (inicio y fin) en el resultado
    // Útil para reportes de actividad de almacén por período
    List<MovimientoStock> findByFechaMovimientoBetweenOrderByFechaMovimientoDesc(
        LocalDateTime inicio, LocalDateTime fin);
    
    // Retorna los últimos 20 movimientos del sistema ordenados por fecha descendente
    // Top20 limita el resultado sin necesidad de paginación manual
    // Útil para dashboards que muestran actividad reciente del almacén
    List<MovimientoStock> findTop20ByOrderByFechaMovimientoDesc();
}
