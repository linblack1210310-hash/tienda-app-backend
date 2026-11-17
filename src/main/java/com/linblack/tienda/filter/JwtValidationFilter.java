
        package com.linblack.tienda.filter;

import com.linblack.tienda.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component; // <-- IMPORTANTE
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // <-- AGREGA ESTO
public class JwtValidationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtValidationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        
        System.out.println("========================================");
        System.out.println("--- ENTRANDO AL FILTRO: " + path + " ---");

        if (path.startsWith("/api/v1/tienda/auth/") || 
           (path.startsWith("/api/v1/tienda/products") && request.getMethod().equals("GET"))) {
            System.out.println("--- ES RUTA PUBLICA, DEJANDO PASAR ---");
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        System.out.println("--- HEADER RECIBIDO: [" + authHeader + "] ---");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("--- ERROR: TOKEN AUSENTE O MAL FORMADO ---");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Token ausente o formato inválido\", \"data\": null}");
            return;
        }

        String token = authHeader.substring(7); 
        System.out.println("--- TOKEN EXTRAIDO: " + token.substring(0, Math.min(token.length(), 20)) + "... ---");
        
        if (!jwtService.isTokenValid(token)) {
            System.out.println("--- ERROR: TOKEN INVÁLIDO O EXPIRADO ---");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Token inválido o expirado\", \"data\": null}");
            return;
        }

        request.setAttribute("userId", jwtService.extractUserId(token));
        request.setAttribute("rolId", jwtService.extractRolId(token));
        request.setAttribute("username", jwtService.extractSubject(token));

        System.out.println("--- TOKEN VALIDO, PASANDO AL CONTROLADOR ---");
        System.out.println("========================================");

        filterChain.doFilter(request, response);
    }
}