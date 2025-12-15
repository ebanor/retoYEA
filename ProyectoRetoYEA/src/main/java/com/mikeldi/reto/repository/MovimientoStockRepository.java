package com.mikeldi.reto.repository;

import com.mikeldi.reto.entity.MovimientoStock;
import com.mikeldi.reto.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {
    
    List<MovimientoStock> findByProductoOrderByFechaMovimientoDesc(Producto producto);
    
    List<MovimientoStock> findByFechaMovimientoBetweenOrderByFechaMovimientoDesc(
        LocalDateTime inicio, LocalDateTime fin);
    
    List<MovimientoStock> findTop20ByOrderByFechaMovimientoDesc();
}
