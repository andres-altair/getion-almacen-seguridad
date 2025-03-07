package com.andres.gestionalmacen.servlets.usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.utilidades.GestorRegistros;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Servlet que maneja el panel del usuario.
 * Este servlet verifica la sesión del usuario y carga la información
 * del usuario en el panel.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Verificación de sesión del usuario</li>
 *   <li>Carga de datos del usuario para mostrar en el panel</li>
 * </ul>
 * 
 * <p>Según [875eb101-5aa8-4067-87e7-39617e3a474a], esta clase maneja el registro
 * de eventos relacionados con el acceso al panel del usuario.</p>
 * 
 * @author Andrés
 * @version 1.0
 */
@WebServlet("/usuario/panel")
public class PanelUsuarioServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(PanelUsuarioServlet.class);
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        GestorRegistros.sistemaInfo("PanelUsuarioServlet: Iniciando procesamiento de petición GET");
        GestorRegistros.sistemaInfo("PanelUsuarioServlet: URI solicitada: " + request.getRequestURI());
        
        // Obtener la sesión sin crear una nueva si no existe
        HttpSession session = request.getSession(false);
        
        // Verificar si hay una sesión activa
        if (session == null) {
            GestorRegistros.sistemaWarning("PanelUsuarioServlet: No hay sesión activa");
            response.sendRedirect(request.getContextPath() + "/acceso");
            return;
        }

        // Obtener el usuario de la sesión
        UsuarioDto usuario = (UsuarioDto) session.getAttribute("usuario");
        if (usuario == null) {
            GestorRegistros.sistemaWarning("PanelUsuarioServlet: No hay usuario en la sesión");
            response.sendRedirect(request.getContextPath() + "/acceso");
            return;
        }

        // Log de información del usuario
        GestorRegistros.sistemaInfo("PanelUsuarioServlet: Usuario encontrado - ID: " + usuario.getId() + 
            ", Rol: " + usuario.getRolId() + ", Nombre: " + usuario.getNombreCompleto());

        // Verificar el rol del usuario (4 = Usuario)
        if (usuario.getRolId() != 4) {
            GestorRegistros.sistemaWarning("PanelUsuarioServlet: Usuario con rol incorrecto: " + usuario.getRolId());
            session.setAttribute("error", "No tienes permiso para acceder a esta página");
            response.sendRedirect(request.getContextPath() + "/acceso");
            return;
        }

        // Establecer atributos para el JSP
        request.setAttribute("usuarioNombre", usuario.getNombreCompleto());
        request.setAttribute("usuarioFoto", usuario.getFoto());

        GestorRegistros.sistemaInfo("PanelUsuarioServlet: Preparando forward al JSP");
        
        try {
            // Forward al JSP
            request.getRequestDispatcher("/usuario/panelUsuario.jsp").forward(request, response);
            GestorRegistros.sistemaInfo("PanelUsuarioServlet: Forward completado exitosamente");
        } catch (Exception e) {
            GestorRegistros.sistemaError("PanelUsuarioServlet: Error al hacer forward: " + e.getMessage());
            throw new ServletException("Error al cargar el panel de usuario", e);
        }
    }
}
