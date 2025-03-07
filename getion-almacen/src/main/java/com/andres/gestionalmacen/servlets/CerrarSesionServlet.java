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

/**
 * Servlet que maneja el proceso de cierre de sesión de usuarios.
 * Se encarga de invalidar la sesión actual y registrar la actividad.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Invalidación segura de la sesión del usuario</li>
 *   <li>Registro de la actividad de cierre de sesión</li>
 *   <li>Redirección a la página de inicio</li>
 *   <li>Manejo de casos donde no existe sesión</li>
 * </ul>
 * 
 * @author Andrés
 * @version 1.0
 */
@WebServlet("/cerrarSesion")
public class CerrarSesionServlet extends HttpServlet {
    
    /**
     * Maneja las peticiones GET para cerrar la sesión del usuario.
     * 
     * <p>El método realiza las siguientes operaciones:</p>
     * <ol>
     *   <li>Obtiene la sesión actual sin crear una nueva</li>
     *   <li>Verifica la existencia de la sesión y del usuario</li>
     *   <li>Registra el cierre de sesión en los logs</li>
     *   <li>Invalida la sesión</li>
     *   <li>Redirige al usuario a la página de inicio</li>
     * </ol>
     * 
     * @param peticion La petición HTTP del cliente
     * @param respuesta La respuesta HTTP al cliente
     * @throws ServletException Si ocurre un error en el servlet
     * @throws IOException Si ocurre un error de E/S
     */
    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        try {
            // Obtener la sesión actual, sin crear una nueva si no existe
            HttpSession sesion = peticion.getSession(false);
            
            // Verificar si la sesión existe y si hay un usuario en la sesión
            if (sesion != null && sesion.getAttribute("usuario") != null) {
                // Obtener el usuario de la sesión
                UsuarioDto usuario = (UsuarioDto) sesion.getAttribute("usuario");
                
                // Registrar el cierre de sesión en los logs
                GestorRegistros.info(usuario.getId(), "Cierre de sesión exitoso");
                GestorRegistros.sistemaInfo("Usuario con ID: " + usuario.getId() + " cerró sesión correctamente");
                
                // Invalidar la sesión
                sesion.invalidate();
            } else {
                // Registrar intento de cierre de sesión sin sesión activa
                GestorRegistros.sistemaWarning("Intento de cierre de sesión sin sesión activa");
            }
            
            // Redirigir al servlet de inicio
            respuesta.sendRedirect(peticion.getContextPath() + "/inicio");
            
        } catch (Exception error) {
            // Registrar cualquier error inesperado
            GestorRegistros.sistemaError("Error durante el cierre de sesión: " + error.getMessage());
            respuesta.sendRedirect(peticion.getContextPath() + "/inicio");
        }
    }
}