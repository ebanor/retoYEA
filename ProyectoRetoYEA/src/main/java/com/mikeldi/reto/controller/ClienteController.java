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

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Gestión de clientes y datos fiscales")
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private ExportService exportService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Listar todos los clientes",
        description = "Retorna la lista completa de clientes. Accesible por ADMIN y COMERCIAL"
    )
    public ResponseEntity<List<ClienteDTO>> listarClientes(
            @RequestParam(required = false) String buscar) {
        
        if (buscar != null && !buscar.isEmpty()) {
            List<ClienteDTO> clientes = clienteService.buscarPorNombre(buscar);
            return ResponseEntity.ok(clientes);
        }
        
        List<ClienteDTO> clientes = clienteService.listarTodos();
        return ResponseEntity.ok(clientes);
    }
    
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
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(
        summary = "Crear nuevo cliente",
        description = "Registra un nuevo cliente con sus datos fiscales y de contacto"
    )
    public ResponseEntity<ClienteDTO> crearCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO nuevoCliente = clienteService.crearCliente(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }
    
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
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Eliminar cliente",
        description = "Elimina un cliente del sistema. Solo ADMIN"
    )
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Cambiar estado del cliente",
        description = "Activa o desactiva un cliente. Solo ADMIN"
    )
    public ResponseEntity<ClienteDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo) {
        ClienteDTO cliente = clienteService.cambiarEstado(id, activo);
        return ResponseEntity.ok(cliente);
    }
    
    // ============== EXPORTACIÓN ==============
    
    @GetMapping("/export/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(summary = "Exportar clientes a PDF")
    public ResponseEntity<byte[]> exportarClientesPDF() throws DocumentException {
        List<ClienteDTO> clientes = clienteService.listarTodos();
        byte[] pdfBytes = exportService.exportarClientesPDF(clientes);
        
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=clientes.pdf")
                .body(pdfBytes);
    }
    
    @GetMapping("/export/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMERCIAL')")
    @Operation(summary = "Exportar clientes a CSV")
    public ResponseEntity<String> exportarClientesCSV() throws IOException {
        List<ClienteDTO> clientes = clienteService.listarTodos();
        String csv = exportService.exportarClientesCSV(clientes);
        
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv; charset=utf-8")
                .header("Content-Disposition", "attachment; filename=clientes.csv")
                .body(csv);
    }
}
