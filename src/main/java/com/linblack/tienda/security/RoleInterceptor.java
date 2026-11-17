package com.linblack.tienda.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod method = (HandlerMethod) handler;
        RequiresRole requiresRole = method.getMethodAnnotation(RequiresRole.class);

        // Si el método no tiene la anotación, dejamos pasar
        if (requiresRole == null) {
            return true; 
        }

        Long rolId = (Long) request.getAttribute("rolId");
        
        // Si el filtro no puso el rol, no está autorizado (401)
        if (rolId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"No autorizado\"}");
            return false;
        }

        // Revisamos si el rol del usuario está en la lista permitida
        boolean hasPermission = false;
        for (long role : requiresRole.value()) {
            if (role == rolId) {
                hasPermission = true;
                break;
            }
        }

        // Si no tiene el rol, devolvemos 403 Forbidden
        if (!hasPermission) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"No tienes permiso para acceder a este recurso\"}");
            return false;
        }

        return true;
    }
}
