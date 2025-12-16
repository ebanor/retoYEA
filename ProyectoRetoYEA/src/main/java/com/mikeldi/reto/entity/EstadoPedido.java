package com.mikeldi.reto.entity;

// Enumeración que define los posibles estados de un pedido en su ciclo de vida
public enum EstadoPedido {
    // Pedido creado pero aún no confirmado el pago
    PENDIENTE("PENDIENTE"),
    
    // Pedido confirmado y pagado, listo para preparar
    PAGADO("PAGADO"),
    
    // Pedido enviado al cliente, en tránsito
    ENVIADO("ENVIADO"),
    
    // Pedido anulado por el cliente o por el sistema
    CANCELADO("CANCELADO");
    
    // Representación en texto del estado para visualización
    private String valor;
    
    // Constructor que establece el valor textual del estado
    EstadoPedido(String valor) {
        this.valor = valor;
    }
    
    // Retorna la representación textual del estado
    public String getValor() {
        return valor;
    }
}
