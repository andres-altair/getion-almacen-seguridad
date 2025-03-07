package com.andres.gestionalmacen.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import com.andres.gestionalmacen.servicios.UsuarioServicio;
import com.andres.gestionalmacen.utilidades.EmailUtil;
import com.andres.gestionalmacen.utilidades.EncriptarUtil;
import com.andres.gestionalmacen.utilidades.GestorRegistros;

@WebServlet("/restablecerContrasena")
public class RestablecerContrasenaServlet extends HttpServlet {
    
    private final UsuarioServicio servicioUsuario;
    
    public RestablecerContrasenaServlet() {
        this.servicioUsuario = new UsuarioServicio();
    }
    
    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        String token = peticion.getParameter("token");
        
        try {
            if (EmailUtil.validarToken(token)) {
                peticion.setAttribute("token", token);
                peticion.getRequestDispatcher("/restablecerContrasena.jsp").forward(peticion, respuesta);
                GestorRegistros.sistemaInfo("Acceso válido a restablecimiento de contraseña con token");
            } else {
                GestorRegistros.sistemaWarning("Intento de acceso con token inválido o expirado");
                peticion.getSession().setAttribute("error", "El enlace ha expirado o no es válido");
                respuesta.sendRedirect(peticion.getContextPath() + "/recuperarContrasena");
            }
        } catch (Exception error) {
            GestorRegistros.sistemaError("Error al procesar solicitud de restablecimiento: " + error.getMessage());
            peticion.getSession().setAttribute("error", 
                "Ha ocurrido un error al procesar tu solicitud. Por favor, inténtalo de nuevo.");
            respuesta.sendRedirect(peticion.getContextPath() + "/recuperarContrasena");
        }
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        String token = peticion.getParameter("token");
        String nuevaContrasena = peticion.getParameter("nuevaContrasena");
        
        try {
            if (EmailUtil.validarToken(token)) {
                String correoElectronico = EmailUtil.obtenerCorreoDeToken(token);
                
                if (correoElectronico != null) {
                    // Encriptar la nueva contraseña
                    String contrasenaEncriptada = EncriptarUtil.contraseñaHash(nuevaContrasena);
                    
                    // Actualizar contraseña en la base de datos
                    servicioUsuario.actualizarContrasena(correoElectronico, contrasenaEncriptada);
                    
                    GestorRegistros.sistemaInfo("Contraseña actualizada exitosamente para: " + correoElectronico);
                    peticion.getSession().setAttribute("mensaje", 
                        "Tu contraseña ha sido actualizada exitosamente. Ya puedes iniciar sesión.");
                } else {
                    GestorRegistros.sistemaWarning("Token válido pero correo no encontrado");
                    peticion.getSession().setAttribute("error", 
                        "No se pudo completar la operación. Por favor, solicita un nuevo enlace.");
                }
            } else {
                GestorRegistros.sistemaWarning("Intento de actualización con token inválido");
                peticion.getSession().setAttribute("error", 
                    "El enlace ha expirado. Por favor, solicita uno nuevo.");
            }
        } catch (Exception error) {
            GestorRegistros.sistemaError("Error al actualizar contraseña: " + error.getMessage());
            peticion.getSession().setAttribute("error", 
                "Ha ocurrido un error al actualizar tu contraseña. Por favor, inténtalo de nuevo.");
        }
        
        respuesta.sendRedirect(peticion.getContextPath() + "/acceso");
    }
}