package com.andres.gestionalmacen.filtro;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.andres.gestionalmacen.utilidades.GestorRegistros;

@WebFilter("*.jsp")
public class filtro implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Log de información sobre la petición
        GestorRegistros.sistemaInfo("\u001B[32m[FILTRO] Nueva petición recibida: " + 
            httpRequest.getRequestURI() + "\u001B[0m");
        
        // Registrar el intento de acceso al JSP
        GestorRegistros.sistemaWarning("\u001B[33mAcceso a JSP bloqueado: " + 
            httpRequest.getRequestURI() + " - IP: " + httpRequest.getRemoteAddr() + "\u001B[0m");
        
        // Log de la redirección
        GestorRegistros.sistemaInfo("\u001B[32m[FILTRO] Redirigiendo a /inicio\u001B[0m");
        
        // SIEMPRE redirigir al servlet de inicio, sin importar si es forward o acceso directo
        httpResponse.sendRedirect(httpRequest.getContextPath() + "/inicio");
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Log de inicialización del filtro
        GestorRegistros.sistemaInfo("\u001B[32m[FILTRO] Filtro de seguridad JSP inicializado\u001B[0m");
    }
    
    @Override
    public void destroy() {
        // Log de destrucción del filtro
        GestorRegistros.sistemaInfo("\u001B[32m[FILTRO] Filtro de seguridad JSP destruido\u001B[0m");
    }
}
