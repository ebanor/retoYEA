package com.mikeldi.reto.entity;

public enum EstadoPedido {
    PENDIENTE("PENDIENTE"),
    PAGADO("PAGADO"),
    ENVIADO("ENVIADO"),
    CANCELADO("CANCELADO");
    
    private String valor;
    
    EstadoPedido(String valor) {
        this.valor = valor;
    }
    
    public String getValor() {
        return valor;
    }
}
