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

@Service
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Transactional
    public ClienteDTO crearCliente(ClienteDTO clienteDTO) {
        // Validar que el NIF no exista
        if (clienteRepository.existsByNif(clienteDTO.getNif())) {
            throw new BadRequestException("Ya existe un cliente con el NIF: " + clienteDTO.getNif());
        }
        
        Cliente cliente = convertirDTOAEntidad(clienteDTO);
        cliente.setActivo(true);
        
        Cliente clienteGuardado = clienteRepository.save(cliente);
        return convertirADTO(clienteGuardado);
    }
    
    @Transactional(readOnly = true)
    public List<ClienteDTO> listarTodos() {
        return clienteRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ClienteDTO> listarActivos() {
        return clienteRepository.findByActivo(true).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ClienteDTO obtenerPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        return convertirADTO(cliente);
    }
    
    @Transactional(readOnly = true)
    public ClienteDTO obtenerPorNif(String nif) {
        Cliente cliente = clienteRepository.findByNif(nif)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "NIF", nif));
        return convertirADTO(cliente);
    }
    
    @Transactional(readOnly = true)
    public List<ClienteDTO> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ClienteDTO actualizarCliente(Long id, ClienteDTO clienteDTO) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        
        // Validar NIF si se está cambiando
        if (!cliente.getNif().equals(clienteDTO.getNif())) {
            if (clienteRepository.existsByNif(clienteDTO.getNif())) {
                throw new BadRequestException("Ya existe un cliente con el NIF: " + clienteDTO.getNif());
            }
            cliente.setNif(clienteDTO.getNif());
        }
        
        // Actualizar datos
        cliente.setNombre(clienteDTO.getNombre());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTelefono(clienteDTO.getTelefono());
        cliente.setDireccion(clienteDTO.getDireccion());
        cliente.setCodigoPostal(clienteDTO.getCodigoPostal());
        cliente.setCiudad(clienteDTO.getCiudad());
        cliente.setProvincia(clienteDTO.getProvincia());
        
        Cliente clienteActualizado = clienteRepository.save(cliente);
        return convertirADTO(clienteActualizado);
    }
    
    @Transactional
    public void eliminarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        clienteRepository.delete(cliente);
    }
    
    @Transactional
    public ClienteDTO cambiarEstado(Long id, boolean activo) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        cliente.setActivo(activo);
        Cliente clienteActualizado = clienteRepository.save(cliente);
        return convertirADTO(clienteActualizado);
    }
    
    // Métodos auxiliares de conversión
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
