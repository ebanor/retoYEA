package com.mikeldi.reto.entity;

// Enumeración que define los roles de usuario disponibles en el sistema
public enum Role {
    // Administrador: acceso completo al sistema, puede gestionar usuarios y configuración
    ADMIN("ADMIN"),
    
    // Comercial: puede gestionar clientes, pedidos y facturas
    COMERCIAL("COMERCIAL"),
    
    // Almacén: puede gestionar productos, stock y movimientos de inventario
    ALMACEN("ALMACEN");
    
    // Representación en texto del rol para visualización
    private String value;
    
    // Constructor que establece el valor textual del rol
    Role(String value) {
        this.value = value;
    }
    
    // Retorna la representación textual del rol
    public String getValue() {
        return value;
    }
}
