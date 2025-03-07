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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            GestorRegistros.sistemaWarning(LEGACY_DO_HEAD + "Intento de acceso al panel sin sesión válida");
            LOGGER.warn("Intento de acceso al panel sin sesión válida");
            response.sendRedirect(request.getContextPath() + "/acceso");
            return;
        }

        UsuarioDto usuario = (UsuarioDto) session.getAttribute("usuario");
        request.setAttribute("usuarioNombre", usuario.getNombreCompleto());
        request.setAttribute("usuarioFoto", usuario.getFoto());

        // Redirigir al JSP
        request.getRequestDispatcher("/usuario/panelUsuario.jsp").forward(request, response);
    }
}
