package com.andres.gestionalmacen.filtro;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.andres.gestionalmacen.utilidades.GestorRegistros;

/**
 * Filtro de seguridad para la aplicación de gestión de almacén.
 * Controla el acceso a las páginas protegidas verificando la existencia
 * de una sesión válida del usuario. Este filtro se aplica a todas las rutas
 * bajo el directorio /jsp/.
 *
 * @author Andrés
 * @version 1.0
 * @since 1.0
 */
@WebFilter(urlPatterns = "*.jsp")
public class FiltroSeguridad implements Filter {
    
    private static final String URI_ACCESO = "/inicio";
    private static final String ATRIBUTO_REENVIO = "jakarta.servlet.forward.request_uri";

    /**
     * Inicializa el filtro.
     * 
     * @param filterConfig Configuración del filtro
     * @throws ServletException Si ocurre un error durante la inicialización
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        GestorRegistros.sistemaInfo("[FILTRO] Filtro JSP inicializado");
    }

    /**
     * Procesa la petición HTTP aplicando la lógica de filtrado.
     * Verifica la existencia de una reenvío válido y redirige a la página de inicio
     * si no existe un reenvío activo.
     *
     * @param request Petición del cliente
     * @param response Respuesta al cliente
     * @param chain Cadena de filtros
     * @throws IOException Si ocurre un error de E/S
     * @throws ServletException Si ocurre un error en el procesamiento del servlet
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest peticionHttp = (HttpServletRequest) request;
        HttpServletResponse respuestaHttp = (HttpServletResponse) response;
        String uri = peticionHttp.getRequestURI();
        
        // Verificar si es un reenvío desde un servlet
        String reenvio = (String) peticionHttp.getAttribute(ATRIBUTO_REENVIO);
        
        if (reenvio != null) {
            // Si es un reenvío, permitir el acceso
            GestorRegistros.sistemaInfo("[FILTRO] Reenvío permitido desde: " + reenvio + " a: " + uri);
            chain.doFilter(request, response);
        } else {
            // Si es acceso directo a JSP, redirigir a inicio
            GestorRegistros.sistemaWarning("[FILTRO] Acceso directo a JSP bloqueado: " + uri);
            respuestaHttp.sendRedirect(peticionHttp.getContextPath() + URI_ACCESO);
        }
    }

    /**
     * Destruye el filtro liberando los recursos utilizados.
     */
    @Override
    public void destroy() {
        GestorRegistros.sistemaInfo("[FILTRO] Filtro JSP destruido");
    }
}
