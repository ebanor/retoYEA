package com.mikeldi.reto.exception;

// Excepción personalizada para recursos no encontrados en la base de datos (HTTP 404)
public class ResourceNotFoundException extends RuntimeException {
    
    // Constructor simple que recibe un mensaje descriptivo del error
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    // Constructor conveniente que formatea automáticamente el mensaje con parámetros
    // Ejemplo: ResourceNotFoundException("Cliente", "id", 5) → "Cliente no encontrado con id: '5'"
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s no encontrado con %s: '%s'", resource, field, value));
    }
}
