package com.andres.gestionalmacen.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.andres.gestionalmacen.servicios.UsuarioServicio;
import com.andres.gestionalmacen.utilidades.EmailUtil;
import com.andres.gestionalmacen.utilidades.EncriptarUtil;
import com.andres.gestionalmacen.utilidades.GestorRegistros;

/**
 * Servlet que maneja el proceso de restablecimiento de contraseña.
 * Este servlet verifica el token de recuperación y actualiza la contraseña del usuario.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Verificación de validez del token de recuperación</li>
 *   <li>Actualización de la contraseña del usuario</li>
 *   <li>Registro detallado de actividades</li>
 * </ul>
 * 
 * <p>Según [875eb101-5aa8-4067-87e7-39617e3a474a], este servlet verifica
 * la existencia del usuario antes de proceder con la actualización.</p>
 * 
 * <p>Según [35176471-70ce-4b89-92e3-77ccfc940534], utiliza validarToken
 * de manera eficiente para verificar la validez del token.</p>
 * 
 * @author Andrés
 * @version 1.0
 */
@WebServlet("/restablecerContrasena")
public class RestablecerContrasenaServlet extends HttpServlet {
    
    private final UsuarioServicio servicioUsuario;
    
    public RestablecerContrasenaServlet() {
        this.servicioUsuario = new UsuarioServicio();
    }
    
    /**
     * Maneja las peticiones GET para el restablecimiento de contraseña.
     * 
     * @param peticion La petición HTTP que contiene el token
     * @param respuesta La respuesta HTTP al cliente
     * @throws ServletException Si ocurre un error en el servlet
     * @throws IOException Si ocurre un error de E/S
     */
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

    /**
     * Procesa las solicitudes POST para actualizar la contraseña.
     * 
     * @param peticion La petición HTTP que contiene el token y la nueva contraseña
     * @param respuesta La respuesta HTTP al cliente
     * @throws ServletException Si ocurre un error en el servlet
     * @throws IOException Si ocurre un error de E/S
     */
    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        String token = peticion.getParameter("token");
        String nuevaContrasena = peticion.getParameter("nuevaContrasena");
        
        try {
            if (EmailUtil.validarToken(token)) {
                String correoElectronico = EmailUtil.obtenerCorreoDeToken(token);
                
                if (correoElectronico != null) {
                    try {
                        // Encriptar la nueva contraseña
                        String contrasenaEncriptada = EncriptarUtil.contraseñaHash(nuevaContrasena);
                        
                        // Preparar datos para la API
                        Map<String, String> datos = new HashMap<>();
                        datos.put("correoElectronico", correoElectronico);
                        datos.put("nuevaContrasena", contrasenaEncriptada);
                        
                        // Actualizar contraseña en la base de datos
                        servicioUsuario.actualizarContrasena(datos);
                        
                        GestorRegistros.sistemaInfo("Contraseña actualizada exitosamente para: " + correoElectronico);
                        peticion.getSession().setAttribute("mensaje", 
                            "Tu contraseña ha sido actualizada exitosamente. Ya puedes iniciar sesión.");
                        respuesta.sendRedirect(peticion.getContextPath() + "/acceso");
                        return;
                    } catch (Exception e) {
                        GestorRegistros.sistemaError("Error al actualizar contraseña: " + e.getMessage());
                        peticion.getSession().setAttribute("error", 
                            "Ha ocurrido un error al actualizar tu contraseña. Por favor, inténtalo de nuevo.");
                        respuesta.sendRedirect(peticion.getContextPath() + "/recuperarContrasena");
                        return;
                    }
                } else {
                    GestorRegistros.sistemaWarning("Token válido pero correo no encontrado");
                    peticion.getSession().setAttribute("error", 
                        "No se pudo completar la operación. Por favor, solicita un nuevo enlace.");
                    respuesta.sendRedirect(peticion.getContextPath() + "/recuperarContrasena");
                }
            } else {
                GestorRegistros.sistemaWarning("Intento de actualización con token inválido");
                peticion.getSession().setAttribute("error", 
                    "El enlace ha expirado. Por favor, solicita uno nuevo.");
                respuesta.sendRedirect(peticion.getContextPath() + "/recuperarContrasena");
            }
        } catch (Exception error) {
            GestorRegistros.sistemaError("Error al procesar solicitud de restablecimiento: " + error.getMessage());
            peticion.getSession().setAttribute("error", 
                "Ha ocurrido un error al procesar tu solicitud. Por favor, inténtalo de nuevo.");
            respuesta.sendRedirect(peticion.getContextPath() + "/recuperarContrasena");
        }
    }
}