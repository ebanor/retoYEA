package com.mikeldi.reto.config;

import com.mikeldi.reto.entity.Role;
import com.mikeldi.reto.entity.Usuario;
import com.mikeldi.reto.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Crear usuario administrador por defecto si no existe
        if (!usuarioRepository.existsByEmail("admin@mikeldi.com")) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setEmail("admin@mikeldi.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(List.of(Role.ADMIN));
            admin.setActivo(true);
            
            usuarioRepository.save(admin);
            System.out.println("✅ Usuario administrador creado:");
            System.out.println("   Email: admin@mikeldi.com");
            System.out.println("   Password: admin123");
        }
        
        // Crear usuario comercial por defecto si no existe
        if (!usuarioRepository.existsByEmail("comercial@mikeldi.com")) {
            Usuario comercial = new Usuario();
            comercial.setNombre("Usuario Comercial");
            comercial.setEmail("comercial@mikeldi.com");
            comercial.setPassword(passwordEncoder.encode("comercial123"));
            comercial.setRoles(List.of(Role.COMERCIAL));
            comercial.setActivo(true);
            
            usuarioRepository.save(comercial);
            System.out.println("✅ Usuario comercial creado:");
            System.out.println("   Email: comercial@mikeldi.com");
            System.out.println("   Password: comercial123");
        }
        
        // Crear usuario almacén por defecto si no existe
        if (!usuarioRepository.existsByEmail("almacen@mikeldi.com")) {
            Usuario almacen = new Usuario();
            almacen.setNombre("Usuario Almacén");
            almacen.setEmail("almacen@mikeldi.com");
            almacen.setPassword(passwordEncoder.encode("almacen123"));
            almacen.setRoles(List.of(Role.ALMACEN));
            almacen.setActivo(true);
            
            usuarioRepository.save(almacen);
            System.out.println("✅ Usuario almacén creado:");
            System.out.println("   Email: almacen@mikeldi.com");
            System.out.println("   Password: almacen123");
        }
    }
}
