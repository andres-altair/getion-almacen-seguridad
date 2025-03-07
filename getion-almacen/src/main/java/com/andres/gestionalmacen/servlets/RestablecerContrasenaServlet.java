package com.andres.gestionalmacen.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import com.andres.gestionalmacen.servicios.UsuarioServicio;
import com.andres.gestionalmacen.utilidades.EmailUtil;

@WebServlet("/restablecerContrasena")
public class RestablecerContrasenaServlet extends HttpServlet {
    
    private UsuarioServicio usuarioServicio = new UsuarioServicio();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getParameter("token");
        if (EmailUtil.validarToken(token)) {
            request.setAttribute("token", token);
            try {
                request.getRequestDispatcher("/restablecerContrasena.jsp").forward(request, response);
            } catch (ServletException | IOException e) {
               
                e.printStackTrace();
            }
        } else {
            request.getSession().setAttribute("error", "El enlace ha expirado o no es válido");
            try {
                response.sendRedirect(request.getContextPath() + "/recuperarContrasena");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getParameter("token");
        String nuevaContrasena = request.getParameter("nuevaContrasena");
        
        if (EmailUtil.validarToken(token)) {
            String correoElectronico = EmailUtil.obtenerCorreoDeToken(token);
            // Actualizar contraseña en la base de datos
            usuarioServicio.actualizarContrasena(correoElectronico, nuevaContrasena);
            
            request.getSession().setAttribute("mensaje", "Contraseña actualizada exitosamente");
            try {
                response.sendRedirect(request.getContextPath() + "/acceso");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}