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
        
        // Registrar el intento de acceso al JSP
        GestorRegistros.sistemaWarning("\u001B[33mAcceso a JSP bloqueado: " + 
            httpRequest.getRequestURI() + " - IP: " + httpRequest.getRemoteAddr() + "\u001B[0m");
        
        // SIEMPRE redirigir al servlet de inicio, sin importar si es forward o acceso directo
        httpResponse.sendRedirect(httpRequest.getContextPath() + "/inicio");
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Método de inicialización
    }
    
    @Override
    public void destroy() {
        // Método de destrucción
    }
}
