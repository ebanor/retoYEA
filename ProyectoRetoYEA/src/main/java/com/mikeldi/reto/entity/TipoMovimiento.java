package com.mikeldi.reto.entity;

public enum TipoMovimiento {
    ENTRADA("ENTRADA"),
    SALIDA("SALIDA"),
    AJUSTE("AJUSTE"),
    VENTA("VENTA");
    
    private String valor;
    
    TipoMovimiento(String valor) {
        this.valor = valor;
    }
    
    public String getValor() {
        return valor;
    }
}
