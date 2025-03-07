package com.andres.gestionalmacen.servlets.operario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.utilidades.GestorRegistros;
import com.andres.gestionalmacen.utilidades.ImagenUtil;

/**
 * Servlet que gestiona el panel de control del operario.
 * Este servlet se encarga de mostrar la información relevante para el operario.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Muestra la información del operario logueado</li>
 *   <li>Maneja errores y redirecciona apropiadamente</li>
 * </ul>
 *
 * @author Andrés
 * @version 1.0
 */
@WebServlet("/operario/panel")
public class PanelOperarioServlet extends HttpServlet {
    
    /**
     * Constructor del servlet.
     */
    public PanelOperarioServlet() {
        super();
    }

    /**
     * Maneja las peticiones GET al panel del operario.
     * 
     * <p>Este método:</p>
     * <ul>
     *   <li>Obtiene el usuario de la sesión</li>
     *   <li>Verifica si el usuario es operario</li>
     *   <li>Muestra la información del operario en el JSP</li>
     * </ul>
     *
     * @param peticion La petición HTTP del cliente
     * @param respuesta La respuesta HTTP al cliente
     * @throws ServletException Si ocurre un error en el servlet
     * @throws IOException Si ocurre un error de E/S
     */
    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        
        // Obtener la sesión actual
        HttpSession sesion = peticion.getSession(false);
        
        // Verificar si la sesión es válida
        if (sesion == null || sesion.getAttribute("usuario") == null) {
            GestorRegistros.sistemaWarning("Intento de acceso al panel de operario sin sesión válida desde IP: " 
                + peticion.getRemoteAddr());
            respuesta.sendRedirect(peticion.getContextPath() + "/acceso");
            return;
        }

        // Obtener el usuario logueado
        UsuarioDto usuario = (UsuarioDto) sesion.getAttribute("usuario");
        
        // Verificar si el usuario es operario
        if (usuario.getRolId() != 3) { 
            GestorRegistros.warning(usuario.getId(), 
                "Intento no autorizado de acceso al panel de operario. Rol actual: " + usuario.getRolId());
            respuesta.sendRedirect(peticion.getContextPath() + "/acceso");
            return;
        }

        try {
            // Preparar los datos de manera segura
            String nombreCompleto = usuario.getNombreCompleto();
            String fotoBase64 = null;
            
            // Procesar la foto con MIME type
            if (usuario.getFoto() != null) {
                byte[] fotoConMime = ImagenUtil.asegurarMimeTypeImagen(usuario.getFoto());
                if (fotoConMime != null) {
                    fotoBase64 = new String(fotoConMime, StandardCharsets.UTF_8);
                } else {
                    GestorRegistros.warning(usuario.getId(), "No se pudo procesar la foto de perfil");
                }
            }
            
            // Establecer atributos con valores por defecto si son null
            peticion.setAttribute("usuarioNombre", nombreCompleto != null ? nombreCompleto : "Usuario");
            peticion.setAttribute("usuarioFoto", fotoBase64 != null ? fotoBase64 : "https://via.placeholder.com/32");
            
            // Mostrar panel
            GestorRegistros.info(usuario.getId(), "Panel de operario cargado exitosamente");
            peticion.getRequestDispatcher("/operario/panelOperario.jsp").forward(peticion, respuesta);
            
        } catch (Exception e) {
            GestorRegistros.error(usuario.getId(), 
                "Error al cargar el panel de operario: " + e.getMessage());
            respuesta.sendRedirect(peticion.getContextPath() + "/acceso");
        }
    }
}
