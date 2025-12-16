package com.mikeldi.reto.controller;

import com.itextpdf.text.DocumentException;
import com.mikeldi.reto.dto.ClienteDTO;
import com.mikeldi.reto.service.ClienteService;
import com.mikeldi.reto.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

// Define este controlador como REST para retornar datos en formato JSON
@RestController
// Establece la ruta base /api/clientes para todos los endpoints
@RequestMapping("/api/clientes")
// Agrupa estos endpoints en Swagger bajo la categoría "Clientes"
@Tag(name = "Clientes", description = "Gestión de clientes y datos fiscales")
// Indica en Swagger que todos los endpoints requieren autenticación JWT
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {
    
    // Servicio para la lógica de negocio de clientes
    @Autowired
    private ClienteService clienteService;
    
    // Servicio para exportar datos a diferentes formatos
    @Autowired
    private ExportService exportService;
    
    // Endpoint GET para listar clientes con búsqueda opcional
    @GetMapping
    // Solo usuarios ADMIN y COMERCIAL pueden acceder
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Listar todos los clientes",
        description = "Retorna la lista completa de clientes. Accesible por ADMIN y COMERCIAL"
    )
    public ResponseEntity<List<ClienteDTO>> listarClientes(
            // Parámetro opcional para búsqueda por nombre
            @RequestParam(required = false) String buscar) {
        
        // Si se proporciona término de búsqueda, filtra los resultados
        if (buscar != null && !buscar.isEmpty()) {
            List<ClienteDTO> clientes = clienteService.buscarPorNombre(buscar);
            return ResponseEntity.ok(clientes);
        }
        
        // Si no hay búsqueda, retorna todos los clientes
        List<ClienteDTO> clientes = clienteService.listarTodos();
        return ResponseEntity.ok(clientes);
    }
    
    // Endpoint GET para listar solo clientes activos
    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Listar clientes activos",
        description = "Retorna solo los clientes activos"
    )
    public ResponseEntity<List<ClienteDTO>> listarClientesActivos() {
        List<ClienteDTO> clientes = clienteService.listarActivos();
        return ResponseEntity.ok(clientes);
    }
    
    // Endpoint GET para obtener un cliente específico por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Obtener cliente por ID",
        description = "Retorna los datos completos de un cliente específico"
    )
    public ResponseEntity<ClienteDTO> obtenerCliente(@PathVariable Long id) {
        ClienteDTO cliente = clienteService.obtenerPorId(id);
        return ResponseEntity.ok(cliente);
    }
    
    // Endpoint GET para obtener un cliente por su NIF/CIF
    @GetMapping("/nif/{nif}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Obtener cliente por NIF",
        description = "Retorna los datos de un cliente buscando por su NIF/CIF"
    )
    public ResponseEntity<ClienteDTO> obtenerClientePorNif(@PathVariable String nif) {
        ClienteDTO cliente = clienteService.obtenerPorNif(nif);
        return ResponseEntity.ok(cliente);
    }
    
    // Endpoint POST para crear un nuevo cliente
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Crear nuevo cliente",
        description = "Registra un nuevo cliente con sus datos fiscales y de contacto"
    )
    public ResponseEntity<ClienteDTO> crearCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        // Delega la creación al servicio
        ClienteDTO nuevoCliente = clienteService.crearCliente(clienteDTO);
        // Retorna código 201 Created con el cliente creado
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }
    
    // Endpoint PUT para actualizar todos los datos de un cliente
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Actualizar cliente",
        description = "Actualiza los datos de un cliente existente"
    )
    public ResponseEntity<ClienteDTO> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO clienteActualizado = clienteService.actualizarCliente(id, clienteDTO);
        return ResponseEntity.ok(clienteActualizado);
    }
    
    // Endpoint DELETE para eliminar un cliente permanentemente
    @DeleteMapping("/{id}")
    // Solo ADMIN puede eliminar clientes
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Eliminar cliente",
        description = "Elimina un cliente del sistema. Solo ADMIN"
    )
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        // Retorna código 204 No Content indicando éxito sin cuerpo
        return ResponseEntity.noContent().build();
    }
    
    // Endpoint PATCH para cambiar solo el estado activo/inactivo
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Cambiar estado del cliente",
        description = "Activa o desactiva un cliente. Solo ADMIN"
    )
    public ResponseEntity<ClienteDTO> cambiarEstado(
            @PathVariable Long id,
            // Parámetro booleano para el nuevo estado
            @RequestParam boolean activo) {
        ClienteDTO cliente = clienteService.cambiarEstado(id, activo);
        return ResponseEntity.ok(cliente);
    }
    
    // Endpoint GET para exportar la lista de clientes a formato PDF
    @GetMapping("/export/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(summary = "Exportar clientes a PDF")
    public ResponseEntity<byte[]> exportarClientesPDF() throws DocumentException {
        // Obtiene todos los clientes
        List<ClienteDTO> clientes = clienteService.listarTodos();
        // Genera el PDF como array de bytes
        byte[] pdfBytes = exportService.exportarClientesPDF(clientes);
        
        // Configura headers para descarga de archivo PDF
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=clientes.pdf")
                .body(pdfBytes);
    }
    
    // Endpoint GET para exportar la lista de clientes a formato CSV
    @GetMapping("/export/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(summary = "Exportar clientes a CSV")
    public ResponseEntity<String> exportarClientesCSV() throws IOException {
        // Obtiene todos los clientes
        List<ClienteDTO> clientes = clienteService.listarTodos();
        // Genera el CSV como String
        String csv = exportService.exportarClientesCSV(clientes);
        
        // Configura headers para descarga de archivo CSV con UTF-8
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv; charset=utf-8")
                .header("Content-Disposition", "attachment; filename=clientes.csv")
                .body(csv);
    }
}
