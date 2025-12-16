package com.mikeldi.reto.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Define este controlador como REST para retornar datos en formato JSON
@RestController
// Establece la ruta base /api/health para el endpoint
@RequestMapping("/api/health")
// Agrupa este endpoint en Swagger bajo la categoría "Health Check"
@Tag(name = "Health Check", description = "Endpoints para verificar el estado del sistema")
public class HealthController {
    
    // Endpoint GET público para verificar que el servicio está operativo
    @GetMapping
    @Operation(
        summary = "Verificar estado del sistema",
        description = "Retorna información sobre el estado actual del servicio"
    )
    public ResponseEntity<Map<String, Object>> healthCheck() {
        // Crea un mapa para estructurar la respuesta JSON
        Map<String, Object> response = new HashMap<>();
        // Indica que el servicio está activo y respondiendo
        response.put("status", "UP");
        // Timestamp para saber cuándo se realizó la verificación
        response.put("timestamp", LocalDateTime.now());
        // Nombre del servicio para identificación
        response.put("service", "ProyectoRetoYEA API");
        // Versión actual de la aplicación
        response.put("version", "0.0.1-SNAPSHOT");
        // Base de datos que utiliza el sistema
        response.put("database", "MySQL");
        // Mensaje descriptivo del estado
        response.put("message", "Sistema funcionando correctamente");
        
        // Retorna código 200 OK con la información del sistema
        return ResponseEntity.ok(response);
    }
}
