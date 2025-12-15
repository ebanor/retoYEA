package com.mikeldi.reto.repository;

import com.mikeldi.reto.entity.LineaPedido;
import com.mikeldi.reto.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineaPedidoRepository extends JpaRepository<LineaPedido, Long> {
    
    List<LineaPedido> findByPedido(Pedido pedido);
}
