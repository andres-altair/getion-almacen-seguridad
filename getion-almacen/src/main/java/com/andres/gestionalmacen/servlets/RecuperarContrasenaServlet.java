package com.andres.gestionalmacen.servlets;

import java.io.IOException;

import com.andres.gestionalmacen.utilidades.EmailUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/recuperarContrasena")
public class RecuperarContrasenaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        // Mostrar formulario para ingresar email
        try {
            request.getRequestDispatcher("/recuperarContrasena.jsp").forward(request, response);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String email = request.getParameter("email");
        // Generar token y enviar email
        String token = EmailUtil.generarToken(email);
        EmailUtil.enviarCorreoRecuperacionContasena(email, token);
        // Redirigir con mensaje
        request.getSession().setAttribute("mensaje", "Te hemos enviado un correo con instrucciones para restablecer tu contraseña");
        try {
            response.sendRedirect(request.getContextPath() + "/acceso");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
