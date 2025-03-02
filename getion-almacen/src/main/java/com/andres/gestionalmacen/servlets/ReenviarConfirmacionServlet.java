package com.andres.gestionalmacen.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.andres.gestionalmacen.utilidades.EmailUtil;
import com.andres.gestionalmacen.utilidades.GestorRegistros;

@WebServlet("/reenviar-confirmacion")
public class ReenviarConfirmacionServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/reenviar-confirmacion.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String email = request.getParameter("email");
        
        try {
            EmailUtil.reenviarCorreoConfirmacion(email);
            request.getSession().setAttribute("mensaje", 
                "Se ha enviado un nuevo correo de confirmación. Por favor, revisa tu bandeja de entrada.");
            GestorRegistros.sistemaInfo("Correo de confirmación reenviado a: " + email);
        } catch (Exception e) {
            GestorRegistros.sistemaError("Error al reenviar confirmación: " + e.getMessage());
            request.getSession().setAttribute("error", 
                "No se pudo reenviar el correo de confirmación. Por favor, inténtalo más tarde.");
        }
        
        response.sendRedirect(request.getContextPath() + "/acceso");
    }
}