package com.mikeldi.reto.config;

import com.mikeldi.reto.entity.Role;
import com.mikeldi.reto.entity.Usuario;
import com.mikeldi.reto.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

// Marca esta clase como componente de Spring para su detección automática
@Component
public class DataInitializer implements CommandLineRunner {
    
    // Inyecta el repositorio para operaciones CRUD de usuarios
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    // Inyecta el encoder para cifrar contraseñas de forma segura
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Método que se ejecuta automáticamente al iniciar la aplicación
    @Override
    public void run(String... args) throws Exception {
        // Verifica si existe el usuario administrador
        if (!usuarioRepository.existsByEmail("admin@mikeldi.com")) {
            // Crea un nuevo objeto Usuario con rol de administrador
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setEmail("admin@mikeldi.com");
            // Cifra la contraseña antes de almacenarla en la base de datos
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(List.of(Role.ADMIN));
            admin.setActivo(true);
            
            // Persiste el usuario en la base de datos
            usuarioRepository.save(admin);
            System.out.println("Usuario administrador creado:");
            System.out.println("   Email: admin@mikeldi.com");
            System.out.println("   Password: admin123");
        }
        
        // Verifica si existe el usuario comercial
        if (!usuarioRepository.existsByEmail("comercial@mikeldi.com")) {
            // Crea un nuevo objeto Usuario con rol comercial
            Usuario comercial = new Usuario();
            comercial.setNombre("Usuario Comercial");
            comercial.setEmail("comercial@mikeldi.com");
            comercial.setPassword(passwordEncoder.encode("comercial123"));
            comercial.setRoles(List.of(Role.COMERCIAL));
            comercial.setActivo(true);
            
            usuarioRepository.save(comercial);
            System.out.println("Usuario comercial creado:");
            System.out.println("   Email: comercial@mikeldi.com");
            System.out.println("   Password: comercial123");
        }
        
        // Verifica si existe el usuario de almacén
        if (!usuarioRepository.existsByEmail("almacen@mikeldi.com")) {
            // Crea un nuevo objeto Usuario con rol de almacén
            Usuario almacen = new Usuario();
            almacen.setNombre("Usuario Almacén");
            almacen.setEmail("almacen@mikeldi.com");
            almacen.setPassword(passwordEncoder.encode("almacen123"));
            almacen.setRoles(List.of(Role.ALMACEN));
            almacen.setActivo(true);
            
            usuarioRepository.save(almacen);
            System.out.println("Usuario almacén creado:");
            System.out.println("   Email: almacen@mikeldi.com");
            System.out.println("   Password: almacen123");
        }
    }
}
