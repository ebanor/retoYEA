package com.mikeldi.reto.repository;

import com.mikeldi.reto.entity.LineaPedido;
import com.mikeldi.reto.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Marca esta interfaz como componente de repositorio para Spring
@Repository
public interface LineaPedidoRepository extends JpaRepository<LineaPedido, Long> {
    
    // Busca todas las líneas de un pedido específico
    // Retorna la lista completa de productos incluidos en el pedido
    List<LineaPedido> findByPedido(Pedido pedido);
}
