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

@WebServlet("/reenviarConfirmacion")
public class ReenviarConfirmacionServlet extends HttpServlet {
    
    private UsuarioServicio servicioUsuario;
    
    @Override
    public void init() throws ServletException {
        super.init();
        servicioUsuario = new UsuarioServicio();
    }
    
    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        peticion.getRequestDispatcher("/reenviarConfirmacion.jsp").forward(peticion, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        String correoElectronico = peticion.getParameter("correoElectronico");
        
        try {
            // Verificar si el usuario existe según el patrón establecido
            UsuarioDto correoUsuario = servicioUsuario.buscarPorCorreo(correoElectronico);
            
            if (correoUsuario == null ) {
                GestorRegistros.sistemaWarning("Intento de reenvío para correo no existente: " + correoElectronico);
                peticion.getSession().setAttribute("error", 
                    "No existe una cuenta con ese correo electrónico.");
                respuesta.sendRedirect(peticion.getContextPath() + "/reenviarConfirmacion");
                return;
            }
            
            // Reenviar correo usando el método existente que maneja tokens
            EmailUtil.reenviarCorreoConfirmacion(correoElectronico);
            
            GestorRegistros.sistemaInfo("Correo de confirmación reenviado a: " + correoElectronico);
            peticion.getSession().setAttribute("mensaje", 
                "Se ha enviado un nuevo correo de confirmación. Por favor, revisa tu bandeja de entrada.");
            
        } catch (Exception error) {
            GestorRegistros.sistemaError("Error al reenviar confirmación: " + error.getMessage());
            peticion.getSession().setAttribute("error", 
                "No se pudo reenviar el correo de confirmación. Por favor, inténtalo más tarde.");
        }
        
        respuesta.sendRedirect(peticion.getContextPath() + "/acceso");
    }
}