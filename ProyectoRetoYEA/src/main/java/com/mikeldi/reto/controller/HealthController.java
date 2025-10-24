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

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health Check", description = "Endpoints para verificar el estado del sistema")
public class HealthController {
    
    @GetMapping
    @Operation(
        summary = "Verificar estado del sistema",
        description = "Retorna información sobre el estado actual del servicio"
    )
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "ProyectoRetoYEA API");
        response.put("version", "0.0.1-SNAPSHOT");
        response.put("database", "MySQL");
        response.put("message", "Sistema funcionando correctamente");
        
        return ResponseEntity.ok(response);
    }
}
