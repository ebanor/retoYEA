package com.mikeldi.reto.exception;

// Excepción personalizada para peticiones mal formadas o datos inválidos (HTTP 400)
public class BadRequestException extends RuntimeException {
    
    // Constructor que recibe un mensaje descriptivo del error
    public BadRequestException(String message) {
        super(message);
    }
}
