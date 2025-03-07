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

@WebServlet("/confirmarCorreo")
public class ConfirmacionCorreoServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        String tokenConfirmacion = peticion.getParameter("token");
        
        if (tokenConfirmacion == null || tokenConfirmacion.isEmpty()) {
            peticion.getSession().setAttribute("error", "Token de confirmación inválido");
            respuesta.sendRedirect(peticion.getContextPath() + "/acceso");
            return;
        }

        try {
            if (!EmailUtil.validarToken(tokenConfirmacion)) {
                peticion.getSession().setAttribute("error", 
                    "El enlace de confirmación ha expirado o no es válido. Por favor, solicita uno nuevo.");
                respuesta.sendRedirect(peticion.getContextPath() + "/reenviarConfirmacion");
                return;
            }

            String correoElectronico = EmailUtil.obtenerCorreoDeToken(tokenConfirmacion);
            UsuarioServicio servicioUsuario = new UsuarioServicio();
            servicioUsuario.confirmarCorreo(correoElectronico);

            GestorRegistros.sistemaInfo("Correo confirmado exitosamente: " + correoElectronico);
            peticion.getSession().setAttribute("mensaje", 
                "¡Correo confirmado exitosamente! Ya puedes iniciar sesión.");
            respuesta.sendRedirect(peticion.getContextPath() + "/acceso");
            
        } catch (Exception error) {
            GestorRegistros.sistemaError("Error en confirmación de correo: " + error.getMessage());
            peticion.getSession().setAttribute("error", 
                "Error al confirmar el correo. Por favor, inténtalo más tarde.");
            respuesta.sendRedirect(peticion.getContextPath() + "/reenviarConfirmacion");
        }
    }
}