package com.andres.gestionalmacen.filtro;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.andres.gestionalmacen.utilidades.GestorRegistros;

@WebFilter(urlPatterns = "*.jsp")
public class filtro implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
            
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String uri = httpRequest.getRequestURI();
        
        // Verificar si es un forward desde un servlet
        String requestDispatcher = (String) httpRequest.getAttribute("jakarta.servlet.forward.request_uri");
        
        if (requestDispatcher != null) {
            // Si es un forward, permitir el acceso
            GestorRegistros.sistemaInfo("[FILTRO] Forward permitido desde: " + requestDispatcher + " a: " + uri);
            chain.doFilter(request, response);
        } else {
            // Si es acceso directo a JSP, redirigir a inicio
            GestorRegistros.sistemaWarning("[FILTRO] Acceso directo a JSP bloqueado: " + uri);
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/inicio");
        }
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        GestorRegistros.sistemaInfo("[FILTRO] Filtro JSP inicializado");
    }

    @Override
    public void destroy() {
        GestorRegistros.sistemaInfo("[FILTRO] Filtro JSP destruido");
    }
}
