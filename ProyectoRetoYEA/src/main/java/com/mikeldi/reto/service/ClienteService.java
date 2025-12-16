package com.mikeldi.reto.service;

import com.mikeldi.reto.dto.ClienteDTO;
import com.mikeldi.reto.entity.Cliente;
import com.mikeldi.reto.exception.BadRequestException;
import com.mikeldi.reto.exception.ResourceNotFoundException;
import com.mikeldi.reto.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Marca esta clase como servicio de Spring con lógica de negocio
@Service
public class ClienteService {
    
    // Inyecta el repositorio para acceso a datos de clientes
    @Autowired
    private ClienteRepository clienteRepository;
    
    // Crea un nuevo cliente con validación de NIF único
    // @Transactional asegura que toda la operación se ejecute en una transacción
    @Transactional
    public ClienteDTO crearCliente(ClienteDTO clienteDTO) {
        // Valida que el NIF no esté ya registrado en el sistema
        if (clienteRepository.existsByNif(clienteDTO.getNif())) {
            throw new BadRequestException("Ya existe un cliente con el NIF: " + clienteDTO.getNif());
        }
        
        // Convierte DTO a entidad para persistencia
        Cliente cliente = convertirDTOAEntidad(clienteDTO);
        cliente.setActivo(true);
        
        // Persiste en la base de datos y retorna el cliente con ID generado
        Cliente clienteGuardado = clienteRepository.save(cliente);
        return convertirADTO(clienteGuardado);
    }
    
    // Lista todos los clientes del sistema
    // readOnly=true optimiza la transacción para solo lectura
    @Transactional(readOnly = true)
    public List<ClienteDTO> listarTodos() {
        // Obtiene todos los clientes y los convierte a DTOs
        return clienteRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // Lista solo los clientes activos (no dados de baja)
    @Transactional(readOnly = true)
    public List<ClienteDTO> listarActivos() {
        return clienteRepository.findByActivo(true).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // Obtiene un cliente específico por su ID
    @Transactional(readOnly = true)
    public ClienteDTO obtenerPorId(Long id) {
        // Lanza excepción si no encuentra el cliente
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        return convertirADTO(cliente);
    }
    
    // Busca un cliente por su NIF/CIF único
    @Transactional(readOnly = true)
    public ClienteDTO obtenerPorNif(String nif) {
        Cliente cliente = clienteRepository.findByNif(nif)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "NIF", nif));
        return convertirADTO(cliente);
    }
    
    // Busca clientes cuyo nombre contenga el texto especificado
    @Transactional(readOnly = true)
    public List<ClienteDTO> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // Actualiza los datos de un cliente existente
    @Transactional
    public ClienteDTO actualizarCliente(Long id, ClienteDTO clienteDTO) {
        // Verifica que el cliente existe
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        
        // Valida NIF solo si se está cambiando a uno diferente
        if (!cliente.getNif().equals(clienteDTO.getNif())) {
            if (clienteRepository.existsByNif(clienteDTO.getNif())) {
                throw new BadRequestException("Ya existe un cliente con el NIF: " + clienteDTO.getNif());
            }
            cliente.setNif(clienteDTO.getNif());
        }
        
        // Actualiza todos los campos del cliente
        cliente.setNombre(clienteDTO.getNombre());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTelefono(clienteDTO.getTelefono());
        cliente.setDireccion(clienteDTO.getDireccion());
        cliente.setCodigoPostal(clienteDTO.getCodigoPostal());
        cliente.setCiudad(clienteDTO.getCiudad());
        cliente.setProvincia(clienteDTO.getProvincia());
        
        // Persiste los cambios y retorna el cliente actualizado
        Cliente clienteActualizado = clienteRepository.save(cliente);
        return convertirADTO(clienteActualizado);
    }
    
    // Elimina un cliente permanentemente del sistema
    @Transactional
    public void eliminarCliente(Long id) {
        // Verifica que el cliente existe antes de eliminar
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        clienteRepository.delete(cliente);
    }
    
    // Activa o desactiva un cliente sin eliminarlo
    @Transactional
    public ClienteDTO cambiarEstado(Long id, boolean activo) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        // Cambia el estado sin modificar otros campos
        cliente.setActivo(activo);
        Cliente clienteActualizado = clienteRepository.save(cliente);
        return convertirADTO(clienteActualizado);
    }
    
    // Convierte una entidad Cliente a DTO para transferencia segura
    // Los DTOs evitan exponer la estructura interna de la base de datos
    private ClienteDTO convertirADTO(Cliente cliente) {
        return new ClienteDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getNif(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.getDireccion(),
                cliente.getCodigoPostal(),
                cliente.getCiudad(),
                cliente.getProvincia(),
                cliente.getActivo(),
                cliente.getFechaCreacion()
        );
    }
    
    // Convierte un DTO a entidad Cliente para persistencia
    // Solo mapea campos modificables por el usuario, no ID ni fechas
    private Cliente convertirDTOAEntidad(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setNif(dto.getNif());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        cliente.setCodigoPostal(dto.getCodigoPostal());
        cliente.setCiudad(dto.getCiudad());
        cliente.setProvincia(dto.getProvincia());
        return cliente;
    }
}
