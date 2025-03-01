package com.andres.gestionalmacen.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.utilidades.GestorRegistros;

@WebServlet("/cerrarSesion")
public class CerrarSesionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Obtener la sesión actual, sin crear una nueva si no existe
        HttpSession sesion = request.getSession(false);
        
        // Verificar si la sesión existe y si hay un usuario en la sesión
        if (sesion != null && sesion.getAttribute("usuario") != null) {
            // Obtener el usuario de la sesión
            UsuarioDto usuario = (UsuarioDto) sesion.getAttribute("usuario");
            
            // Registrar el cierre de sesión en los logs
            GestorRegistros.info(usuario.getId(), "Cierre de sesión");
            
            // Invalidar la sesión
            sesion.invalidate();
        }
        
        // Redirigir al servlet de inicio
        response.sendRedirect(request.getContextPath() + "/inicio"); 
    }
}