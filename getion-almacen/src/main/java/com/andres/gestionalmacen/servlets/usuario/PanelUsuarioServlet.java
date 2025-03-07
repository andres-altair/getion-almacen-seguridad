package com.andres.gestionalmacen.servlets.usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.andres.gestionalmacen.dtos.UsuarioDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/usuario/panel")
public class PanelUsuarioServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(PanelUsuarioServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
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
