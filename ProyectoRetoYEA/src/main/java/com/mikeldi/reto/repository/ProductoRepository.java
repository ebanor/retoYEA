package com.mikeldi.reto.repository;

import com.mikeldi.reto.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

// Marca esta interfaz como componente de repositorio para Spring
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // Busca productos cuyo nombre contenga el texto especificado (búsqueda parcial)
    // IgnoreCase hace la búsqueda insensible a mayúsculas/minúsculas
    // Útil para implementar buscadores con autocompletado
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    
    // Filtra productos por categoría específica
    // Permite mostrar catálogos organizados por tipo de producto
    List<Producto> findByCategoria(String categoria);
    
    // Combina dos condiciones: filtra por categoría Y estado activo
    // Muestra solo productos disponibles para venta de una categoría específica
    List<Producto> findByCategoriaAndActivo(String categoria, Boolean activo);
    
    // Lista productos según su estado activo/inactivo
    // Útil para mostrar solo productos disponibles en el catálogo público
    List<Producto> findByActivo(Boolean activo);
    
    // Consulta JPQL que encuentra productos que necesitan reposición
    // Compara stock actual con stock mínimo directamente en la base de datos
    @Query("SELECT p FROM Producto p WHERE p.stockActual <= p.stockMinimo")
    List<Producto> findProductosConStockBajo();
    
    // Consulta JPQL que extrae todas las categorías únicas existentes
    // DISTINCT elimina duplicados, ORDER BY ordena alfabéticamente
    // Retorna lista de strings en lugar de entidades Producto
    @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.categoria IS NOT NULL ORDER BY p.categoria")
    List<String> findAllCategorias();
    
    // Busca productos activos con stock menor al mínimo especificado
    // Alternativa más flexible que permite definir el umbral dinámicamente
    List<Producto> findByStockActualLessThanAndActivoTrue(Integer stockMinimo);
}
