package com.mikeldi.reto.entity;

// Enumeración que define los tipos de movimientos de inventario en el sistema
public enum TipoMovimiento {
    // Entrada de mercancía: incrementa el stock (compras, devoluciones de clientes)
    ENTRADA("ENTRADA"),
    
    // Salida de mercancía: decrementa el stock (mermas, roturas, devoluciones a proveedores)
    SALIDA("SALIDA"),
    
    // Ajuste de inventario: puede incrementar o decrementar (correcciones de inventario físico)
    AJUSTE("AJUSTE"),
    
    // Venta de producto: decrementa el stock automáticamente al emitir factura
    VENTA("VENTA");
    
    // Representación en texto del tipo de movimiento para visualización
    private String valor;
    
    // Constructor que establece el valor textual del tipo de movimiento
    TipoMovimiento(String valor) {
        this.valor = valor;
    }
    
    // Retorna la representación textual del tipo de movimiento
    public String getValor() {
        return valor;
    }
}
