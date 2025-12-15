package com.mikeldi.reto.entity;

public enum EstadoFactura {
    PENDIENTE("PENDIENTE"),
    PAGADA("PAGADA"),
    VENCIDA("VENCIDA"),
    CANCELADA("CANCELADA");
    
    private String valor;
    
    EstadoFactura(String valor) {
        this.valor = valor;
    }
    
    public String getValor() {
        return valor;
    }
}
