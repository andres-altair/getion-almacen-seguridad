package com.andres.gestionalmacen.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.andres.gestionalmacen.servicios.UsuarioServicio;
import com.andres.gestionalmacen.utilidades.EmailUtil;
import com.andres.gestionalmacen.utilidades.GestorRegistros;

@WebServlet("/confirmar-correo")
public class ConfirmacionCorreoServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String token = request.getParameter("token");
        
        if (token == null || token.isEmpty()) {
            request.getSession().setAttribute("error", "Token de confirmación inválido");
            response.sendRedirect(request.getContextPath() + "/acceso");
            return;
        }

        try {
            if (!EmailUtil.validarToken(token)) {
                request.getSession().setAttribute("error", 
                    "El enlace de confirmación ha expirado o no es válido. Por favor, solicita uno nuevo.");
                response.sendRedirect(request.getContextPath() + "/reenviar-confirmacion");
                return;
            }

            String email = EmailUtil.getEmailFromToken(token);
            UsuarioServicio usuarioServicio = new UsuarioServicio();
            usuarioServicio.confirmarCorreo(email);

            GestorRegistros.sistemaInfo("Correo confirmado exitosamente: " + email);
            request.getSession().setAttribute("mensaje", 
                "¡Correo confirmado exitosamente! Ya puedes iniciar sesión.");
            response.sendRedirect(request.getContextPath() + "/acceso");
            
        } catch (Exception e) {
            GestorRegistros.sistemaError("Error en confirmación de correo: " + e.getMessage());
            request.getSession().setAttribute("error", 
                "Error al confirmar el correo. Por favor, inténtalo más tarde.");
            response.sendRedirect(request.getContextPath() + "/acceso");
        }
    }
}