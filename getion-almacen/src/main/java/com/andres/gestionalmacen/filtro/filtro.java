package com.andres.gestionalmacen.filtro;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.andres.gestionalmacen.utilidades.GestorRegistros;

/**
 * Filtro que intercepta las solicitudes a archivos JSP.
 * Este filtro se encarga de registrar las solicitudes y bloquear el acceso directo a los JSP,
 * permitiendo solo el acceso a través de forwards desde servlets.
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
     * Este método registra la solicitud y bloquea el acceso directo a los JSP,
     * permitiendo solo el acceso a través de forwards desde servlets.
     * 
     * @param peticion La solicitud recibida
     * @param respuesta La respuesta a la solicitud
     * @param cadena La cadena de filtros
     * @throws IOException Si ocurre un error de entrada/salida
     * @throws ServletException Si ocurre un error en el servlet
     */
    @Override
    public void doFilter(ServletRequest peticion, ServletResponse respuesta, FilterChain cadena)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) peticion;
        HttpServletResponse httpResponse = (HttpServletResponse) respuesta;
        
        // Verificar si es un forward desde un servlet
        String requestDispatcher = (String) httpRequest.getAttribute("jakarta.servlet.forward.request_uri");
        
        if (requestDispatcher != null) {
            // Es un forward desde un servlet, permitir el acceso
            GestorRegistros.sistemaInfo("[FILTRO] Forward detectado desde: " + requestDispatcher);
            cadena.doFilter(peticion, respuesta);
        } else {
            // Es un acceso directo al JSP, bloquearlo
            GestorRegistros.sistemaWarning("Acceso directo a JSP bloqueado: " + 
                httpRequest.getRequestURI() + " - IP: " + httpRequest.getRemoteAddr());
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/inicio");
        }
    }
    
    /**
     * Método que se ejecuta cuando se inicializa el filtro.
     * Este método registra la inicialización del filtro.
     * 
     * @param filtroConfig La configuración del filtro
     * @throws ServletException Si ocurre un error en el servlet
     */
    @Override
    public void init(FilterConfig filtroConfig) throws ServletException {
        // Log de inicialización del filtro
        GestorRegistros.sistemaInfo("[FILTRO] Filtro de seguridad JSP inicializado");
    }
    
    /**
     * Método que se ejecuta cuando se destruye el filtro.
     * Este método registra la destrucción del filtro.
     */
    @Override
    public void destroy() {
        // Log de destrucción del filtro
        GestorRegistros.sistemaInfo("[FILTRO] Filtro de seguridad JSP destruido");
    }
}
