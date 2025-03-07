package com.andres.gestionalmacen.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.servicios.UsuarioServicio;
import com.andres.gestionalmacen.utilidades.EmailUtil;
import com.andres.gestionalmacen.utilidades.GestorRegistros;

@WebServlet("/recuperarContrasena")
public class RecuperarContrasenaServlet extends HttpServlet {
    
    private UsuarioServicio servicioUsuario;
    
    @Override
    public void init() throws ServletException {
        super.init();
        servicioUsuario = new UsuarioServicio();
    }
    
    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        peticion.getRequestDispatcher("/recuperarContrasena.jsp").forward(peticion, respuesta);
    }
    
    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        String correoElectronico = peticion.getParameter("correoElectronico");
        
        try {
            // Verificar si el usuario existe y no es de Google
            UsuarioDto correoUsuario = servicioUsuario.buscarPorCorreo(correoElectronico);
            
            if (correoUsuario == null) {
                GestorRegistros.sistemaWarning("Intento de recuperación para correo no existente: " + correoElectronico);
                peticion.setAttribute("error", "No existe una cuenta con ese correo electrónico.");
                peticion.getRequestDispatcher("/recuperarContrasena.jsp").forward(peticion, respuesta);
                return;
            }
            
            // Verificar si es usuario de Google
            if (correoUsuario.isGoogle()) {
                GestorRegistros.sistemaWarning("Intento de recuperación para cuenta de Google: " + correoElectronico);
                peticion.setAttribute("error", "Las cuentas de Google deben usar el botón 'Iniciar sesión con Google'.");
                peticion.getRequestDispatcher("/recuperarContrasena.jsp").forward(peticion, respuesta);
                return;
            }
            
            // Generar token y enviar correo
            String tokenRecuperacion = EmailUtil.generarToken(correoElectronico);
            EmailUtil.enviarCorreoRecuperacionContasena(correoElectronico, tokenRecuperacion);
            
            GestorRegistros.sistemaInfo("Correo de recuperación enviado a: " + correoElectronico);
            peticion.getSession().setAttribute("mensaje", 
                "Se ha enviado un enlace de recuperación a tu correo electrónico.");
            respuesta.sendRedirect(peticion.getContextPath() + "/acceso");
            
        } catch (Exception error) {
            GestorRegistros.sistemaError("Error en recuperación de contraseña: " + error.getMessage());
            peticion.setAttribute("error", 
                "Ha ocurrido un error al procesar tu solicitud. Por favor, inténtalo más tarde.");
            peticion.getRequestDispatcher("/recuperarContrasena.jsp").forward(peticion, respuesta);
        }
    }
}
