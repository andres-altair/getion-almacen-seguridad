package com.andres.gestionalmacen.filtro;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.andres.gestionalmacen.utilidades.GestorRegistros;

/**
 * Filtro que intercepta las solicitudes a archivos JSP.
 * Este filtro se encarga de registrar las solicitudes y redirigir a los usuarios
 * a la página de inicio, bloqueando el acceso directo a los JSP.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Registro de información sobre las solicitudes recibidas</li>
 *   <li>Bloqueo de acceso a archivos JSP</li>
 *   <li>Redirección a la página de inicio</li>
 * </ul>
 * 
 * <p>Según [875eb101-5aa8-4067-87e7-39617e3a474a], esta clase maneja el registro
 * de eventos relacionados con las solicitudes a los JSP.</p>
 * 
 * @author Andrés
 * @version 1.0
 */
@WebFilter("*.jsp")
public class filtro implements Filter {
    
    /**
     * Método que se ejecuta cuando se recibe una solicitud.
     * Este método registra la solicitud y redirige al usuario a la página de inicio.
     * 
     * @param request La solicitud recibida
     * @param response La respuesta a la solicitud
     * @param chain La cadena de filtros
     * @throws IOException Si ocurre un error de entrada/salida
     * @throws ServletException Si ocurre un error en el servlet
     */
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
    
    /**
     * Método que se ejecuta cuando se inicializa el filtro.
     * Este método registra la inicialización del filtro.
     * 
     * @param filterConfig La configuración del filtro
     * @throws ServletException Si ocurre un error en el servlet
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Log de inicialización del filtro
        GestorRegistros.sistemaInfo("\u001B[32m[FILTRO] Filtro de seguridad JSP inicializado\u001B[0m");
    }
    
    /**
     * Método que se ejecuta cuando se destruye el filtro.
     * Este método registra la destrucción del filtro.
     */
    @Override
    public void destroy() {
        // Log de destrucción del filtro
        GestorRegistros.sistemaInfo("\u001B[32m[FILTRO] Filtro de seguridad JSP destruido\u001B[0m");
    }
}
