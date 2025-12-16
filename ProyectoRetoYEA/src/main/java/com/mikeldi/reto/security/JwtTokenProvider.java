package com.mikeldi.reto.security;

import com.mikeldi.reto.entity.Usuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

// Marca esta clase como componente de Spring para autodetección
@Component
public class JwtTokenProvider {

    // Clave secreta para firmar tokens JWT, inyectada desde application.properties
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    // Tiempo de expiración del token en milisegundos, inyectado desde configuración
    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    // Genera un nuevo token JWT después de un login exitoso
    public String generateToken(Authentication authentication) {
        // Obtiene el email del usuario autenticado
        String email = authentication.getName();
        // Fecha actual como momento de emisión
        Date now = new Date();
        // Calcula fecha de expiración sumando tiempo configurado
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        // Extrae los roles del usuario y los convierte a lista de strings
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Genera clave de firma HMAC-SHA usando el secreto configurado
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        // Construye y firma el token JWT con todos los claims
        return Jwts.builder()
                .subject(email)  // Subject: email del usuario
                .claim("roles", roles)  // Claim personalizado con roles del usuario
                .issuedAt(now)  // Fecha de emisión
                .expiration(expiryDate)  // Fecha de expiración
                .signWith(key)  // Firma con clave secreta
                .compact();  // Serializa a string JWT
    }

    // Extrae el email del usuario desde un token JWT
    public String getEmailFromToken(String token) {
        // Genera la clave de verificación usando el secreto
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        // Parsea y verifica el token, extrayendo los claims
        Claims claims = Jwts.parser()
                .verifyWith(key)  // Verifica firma con la clave
                .build()
                .parseSignedClaims(token)  // Parsea el token
                .getPayload();  // Obtiene los claims

        // Retorna el subject del token (el email)
        return claims.getSubject();
    }
    
    // Extrae la lista de roles desde un token JWT
    @SuppressWarnings("unchecked")  // Suprime warning de cast sin verificar tipo
    public List<String> getRolesFromToken(String token) {
        // Genera la clave de verificación usando el secreto
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        // Parsea y verifica el token, extrayendo los claims
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // Extrae el claim "roles" y lo castea a List<String>
        return (List<String>) claims.get("roles");
    }

    // Valida que un token JWT sea correcto y no haya expirado
    public boolean validateToken(String authToken) {
        try {
            // Genera la clave de verificación usando el secreto
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            // Intenta parsear y verificar el token
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(authToken);
            // Si no lanza excepción, el token es válido
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // Captura cualquier error de JWT (expirado, firma inválida, malformado)
            // Token inválido
        }
        return false;
    }
}
