package com.mikeldi.reto.entity;

public enum Role {
    ADMIN("ADMIN"),
    COMERCIAL("COMERCIAL"),
    ALMACEN("ALMACEN");
    
    private String value;
    
    Role(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
