package com.mikeldi.reto.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// Marca esta clase como componente de Spring para autodetección
@Component
public class JwtUtil {
    
    // Logger para registrar errores de validación de tokens
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    // Clave secreta para firmar tokens JWT, inyectada desde application.properties
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    
    // Tiempo de expiración del token en milisegundos, inyectado desde configuración
    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;
    
    // Genera la clave de firma HMAC-SHA a partir del secreto configurado
    // Método privado reutilizable para evitar repetir código
    private SecretKey getSigningKey() {
        // Convierte el string del secreto a bytes UTF-8
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        // Crea clave HMAC-SHA apropiada para firmar JWTs
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    // Genera un nuevo token JWT después de un login exitoso
    public String generateJwtToken(Authentication authentication) {
        // Obtiene el UserDetails del usuario autenticado
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        
        // Construye y firma el token JWT
        return Jwts.builder()
                .subject(userPrincipal.getUsername())  // Subject: username (email)
                .issuedAt(new Date())  // Fecha de emisión actual
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))  // Fecha de expiración
                .signWith(getSigningKey())  // Firma con clave secreta
                .compact();  // Serializa a string JWT
    }
    
    // Extrae el username (email) desde un token JWT
    public String getUsernameFromJwtToken(String token) {
        // Parsea el token, verifica firma, y extrae el subject
        return Jwts.parser()
                .verifyWith(getSigningKey())  // Verifica firma con la clave
                .build()
                .parseSignedClaims(token)  // Parsea el token JWT
                .getPayload()  // Obtiene los claims
                .getSubject();  // Extrae el subject (username/email)
    }
    
    // Valida que un token JWT sea correcto, no haya expirado y tenga firma válida
    public boolean validateJwtToken(String authToken) {
        try {
            // Intenta parsear y verificar el token completamente
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(authToken);
            // Si no lanza excepción, el token es válido
            return true;
        } catch (MalformedJwtException e) {
            // Token mal formado (estructura JWT inválida)
            logger.error("Token JWT inválido: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            // Token expirado, usuario debe autenticarse nuevamente
            logger.error("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            // Token con formato o algoritmo no soportado
            logger.error("Token JWT no soportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // Token vacío o null
            logger.error("JWT claims string está vacío: {}", e.getMessage());
        }
        // Cualquier excepción resulta en validación fallida
        return false;
    }
}
