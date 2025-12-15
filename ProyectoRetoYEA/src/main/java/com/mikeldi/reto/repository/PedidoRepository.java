package com.mikeldi.reto.repository;

import com.mikeldi.reto.entity.Cliente;
import com.mikeldi.reto.entity.EstadoPedido;
import com.mikeldi.reto.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    List<Pedido> findByClienteOrderByFechaPedidoDesc(Cliente cliente);
    
    List<Pedido> findByEstadoOrderByFechaPedidoDesc(EstadoPedido estado);
    
    List<Pedido> findByFechaPedidoBetweenOrderByFechaPedidoDesc(LocalDateTime inicio, LocalDateTime fin);
    
    @Query("SELECT p FROM Pedido p ORDER BY p.fechaPedido DESC")
    List<Pedido> findAllOrderByFechaPedidoDesc();
    
    Long countByEstado(EstadoPedido estado);
}
