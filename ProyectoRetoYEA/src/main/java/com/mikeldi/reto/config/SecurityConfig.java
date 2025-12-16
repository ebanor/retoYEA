package com.mikeldi.reto.config;

import com.mikeldi.reto.service.CustomUserDetailsService;
import com.mikeldi.reto.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Marca esta clase como configuración de Spring
@Configuration
// Habilita la seguridad web de Spring Security
@EnableWebSecurity
// Habilita anotaciones de seguridad en métodos como @PreAuthorize
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    // Servicio personalizado para cargar detalles de usuarios desde la base de datos
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    // Filtro personalizado que valida tokens JWT en cada petición
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    // Define el algoritmo de cifrado para contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt es un algoritmo seguro con salt automático
        return new BCryptPasswordEncoder();
    }
    
    // Configura el proveedor de autenticación con acceso a datos
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // Establece el servicio para cargar usuarios
        authProvider.setUserDetailsService(userDetailsService);
        // Establece el encoder para validar contraseñas
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    // Gestor de autenticación usado en el proceso de login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    // Configura la cadena de filtros de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilita CSRF porque usamos JWT (stateless)
            .csrf(csrf -> csrf.disable())
            // Configura sesiones como stateless ya que JWT maneja el estado
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Define las reglas de autorización para cada ruta
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas sin autenticación requerida
                .requestMatchers(
                    "/api/auth/**",          // Endpoints de autenticación y registro
                    "/api/health",           // Health check
                    "/swagger-ui/**",        // Documentación Swagger
                    "/api-docs/**",          // API docs
                    "/v3/api-docs/**",       // OpenAPI v3
                    "/swagger-ui.html",      // Interfaz Swagger
                    "/css/**",               // Recursos estáticos CSS
                    "/js/**",                // Recursos estáticos JS
                    "/images/**",            // Imágenes
                    "/favicon.ico"           // Icono del sitio
                ).permitAll()
                // Rutas web accesibles, la validación JWT se maneja en el filtro
                .requestMatchers("/web/**").permitAll()
                // Todas las rutas API requieren autenticación JWT válida
                .requestMatchers("/api/**").authenticated()
                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )
            // Registra el proveedor de autenticación personalizado
            .authenticationProvider(authenticationProvider())
            // Añade el filtro JWT antes del filtro de autenticación estándar
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
