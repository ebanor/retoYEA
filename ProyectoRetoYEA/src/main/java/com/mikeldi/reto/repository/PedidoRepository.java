package com.mikeldi.reto.repository;

import com.mikeldi.reto.entity.Cliente;
import com.mikeldi.reto.entity.EstadoPedido;
import com.mikeldi.reto.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

// Marca esta interfaz como componente de repositorio para Spring
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    // Lista todos los pedidos de un cliente específico ordenados por fecha descendente
    // Muestra el historial de compras más reciente primero
    List<Pedido> findByClienteOrderByFechaPedidoDesc(Cliente cliente);
    
    // Filtra pedidos por estado (PENDIENTE, PAGADO, ENVIADO, CANCELADO)
    // Ordenados por fecha descendente para priorizar los más recientes
    List<Pedido> findByEstadoOrderByFechaPedidoDesc(EstadoPedido estado);
    
    // Busca pedidos realizados dentro de un rango de fechas específico
    // Between incluye ambas fechas límite en el resultado
    // Útil para reportes de ventas mensuales o trimestrales
    List<Pedido> findByFechaPedidoBetweenOrderByFechaPedidoDesc(LocalDateTime inicio, LocalDateTime fin);
    
    // Consulta JPQL personalizada que lista todos los pedidos ordenados por fecha
    // Alternativa explícita a findAllByOrderByFechaPedidoDesc() usando sintaxis JPQL
    @Query("SELECT p FROM Pedido p ORDER BY p.fechaPedido DESC")
    List<Pedido> findAllOrderByFechaPedidoDesc();
    
    // Cuenta cuántos pedidos hay en un estado específico
    // Retorna solo el número sin cargar entidades, optimizando rendimiento
    Long countByEstado(EstadoPedido estado);
}
