package com.mikeldi.reto.entity;

// Enumeración que define los posibles estados de una factura en el sistema
public enum EstadoFactura {
    // Factura emitida pero aún no cobrada
    PENDIENTE("PENDIENTE"),
    
    // Factura cobrada completamente
    PAGADA("PAGADA"),
    
    // Factura que ha superado su fecha de vencimiento sin ser pagada
    VENCIDA("VENCIDA"),
    
    // Factura anulada o cancelada
    CANCELADA("CANCELADA");
    
    // Representación en texto del estado para visualización
    private String valor;
    
    // Constructor que establece el valor textual del estado
    EstadoFactura(String valor) {
        this.valor = valor;
    }
    
    // Retorna la representación textual del estado
    public String getValor() {
        return valor;
    }
}
