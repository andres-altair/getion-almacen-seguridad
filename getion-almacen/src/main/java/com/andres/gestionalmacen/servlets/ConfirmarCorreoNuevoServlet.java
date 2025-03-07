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

/**
 * Servlet que maneja la confirmación de correo electrónico de usuarios.
 * Procesa el token de confirmación y actualiza el estado del usuario.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Validación del token de confirmación</li>
 *   <li>Confirmación del correo electrónico del usuario</li>
 *   <li>Manejo de tokens expirados o inválidos</li>
 *   <li>Redirección según el resultado de la confirmación</li>
 *   <li>Registro detallado de actividades y errores</li>
 * </ul>
 * 
 * @author Andrés
 * @version 1.0
 */
@WebServlet("/confirmarCorreoNuevo")    
public class ConfirmarCorreoNuevoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    /**
     * Maneja las peticiones GET para confirmar el correo electrónico del usuario.
     * 
     * <p>El método realiza las siguientes operaciones:</p>
     * <ol>
     *   <li>Valida la presencia y formato del token</li>
     *   <li>Verifica la validez temporal del token</li>
     *   <li>Extrae el correo electrónico del token</li>
     *   <li>Confirma el correo en la base de datos</li>
     *   <li>Registra la actividad y redirige según el resultado</li>
     * </ol>
     * 
     * @param peticion La petición HTTP que contiene el token de confirmación
     * @param respuesta La respuesta HTTP al cliente
     * @throws ServletException Si ocurre un error en el servlet
     * @throws IOException Si ocurre un error de E/S
     */
    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        String tokenConfirmacion = peticion.getParameter("token");
        
        // Validar presencia del token
        if (tokenConfirmacion == null || tokenConfirmacion.isEmpty()) {
            GestorRegistros.sistemaWarning("Intento de confirmación sin token");
            peticion.getSession().setAttribute("error", "Token de confirmación inválido");
            respuesta.sendRedirect(peticion.getContextPath() + "/acceso");
            return;
        }

        try {
            // Validar token
            if (!EmailUtil.validarToken(tokenConfirmacion)) {
                GestorRegistros.sistemaWarning("Token de confirmación expirado o inválido: " + tokenConfirmacion);
                peticion.getSession().setAttribute("error", 
                    "El enlace de confirmación ha expirado o no es válido. Por favor, solicita uno nuevo.");
                respuesta.sendRedirect(peticion.getContextPath() + "/reenviarConfirmacion");
                return;
            }

            // Procesar confirmación
            String correoElectronico = EmailUtil.obtenerCorreoDeToken(tokenConfirmacion);
            GestorRegistros.sistemaInfo("Procesando confirmación para: " + correoElectronico);
            
            UsuarioServicio servicioUsuario = new UsuarioServicio();
            servicioUsuario.confirmarCorreo(correoElectronico);

            // Registrar éxito
            GestorRegistros.sistemaInfo("Correo confirmado exitosamente: " + correoElectronico);
            peticion.getSession().setAttribute("mensaje", 
                "¡Correo confirmado exitosamente! Ya puedes iniciar sesión.");
            respuesta.sendRedirect(peticion.getContextPath() + "/acceso");
            
        } catch (Exception error) {
            // Registrar error detallado
            GestorRegistros.sistemaError("Error en confirmación de correo: " + error.getMessage());
            GestorRegistros.sistemaError("Detalles del error: " + error.toString());
            
            peticion.getSession().setAttribute("error", 
                "Error al confirmar el correo. Por favor, inténtalo más tarde.");
            respuesta.sendRedirect(peticion.getContextPath() + "/reenviarConfirmacion");
        }
    }
}