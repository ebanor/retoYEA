package com.mikeldi.reto.security;

import com.mikeldi.reto.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Marca esta clase como componente de Spring para autodetección
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Proveedor de utilidades para generar y validar tokens JWT
    @Autowired
    private JwtTokenProvider tokenProvider;

    // Servicio que carga usuarios desde la base de datos
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    // Método que se ejecuta una vez por cada petición HTTP
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Extrae el token JWT del header Authorization de la petición
            String jwt = getJwtFromRequest(request);

            // Verifica que el token existe y es válido
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // Extrae el email del usuario desde el token JWT
                String email = tokenProvider.getEmailFromToken(jwt);

                // Carga el usuario completo desde la base de datos con sus roles actualizados
                // Esto asegura que los cambios de roles se reflejen inmediatamente
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                
                // Crea el objeto de autenticación con los roles del usuario
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,  // Principal: el usuario autenticado
                                null,  // Credentials: null porque JWT ya validó identidad
                                userDetails.getAuthorities()  // Authorities: roles del usuario
                        );
                // Añade detalles adicionales de la petición HTTP
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establece la autenticación en el contexto de seguridad de Spring
                // A partir de aquí, la petición está autenticada
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // Registra errores sin interrumpir el flujo de la petición
            logger.error("No se pudo establecer la autenticación del usuario", ex);
        }

        // Continúa con el siguiente filtro en la cadena
        // Si no hay token o es inválido, la petición continúa sin autenticación
        filterChain.doFilter(request, response);
    }

    // Método auxiliar que extrae el token JWT del header Authorization
    private String getJwtFromRequest(HttpServletRequest request) {
        // Lee el header Authorization de la petición
        String bearerToken = request.getHeader("Authorization");
        // Verifica que existe y tiene el formato "Bearer <token>"
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // Retorna solo el token, removiendo el prefijo "Bearer "
            return bearerToken.substring(7);
        }
        return null;
    }
}
