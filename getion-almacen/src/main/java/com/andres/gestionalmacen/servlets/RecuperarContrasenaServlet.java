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

/**
 * Servlet que maneja el proceso de recuperación de contraseña.
 * Gestiona las solicitudes de recuperación, verifica usuarios y envía correos de recuperación.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Verificación de existencia de usuario</li>
 *   <li>Validación de método de autenticación (normal vs Google)</li>
 *   <li>Generación de tokens de recuperación</li>
 *   <li>Envío de correos de recuperación</li>
 *   <li>Registro detallado de actividades</li>
 * </ul>
 * 
 * <p>Según [875eb101-5aa8-4067-87e7-39617e3a474a], verifica el campo 'google'
 * para prevenir recuperación de contraseña en cuentas de Google.</p>
 * 
 * <p>Según [35176471-70ce-4b89-92e3-77ccfc940534], utiliza buscarPorCorreo
 * de manera eficiente para verificar usuarios.</p>
 * 
 * @author Andrés
 * @version 1.0
 */
@WebServlet("/recuperarContrasena")
public class RecuperarContrasenaServlet extends HttpServlet {
    
    private UsuarioServicio servicioUsuario;
    
    /**
     * Inicializa el servlet configurando el servicio de usuario.
     * 
     * @throws ServletException Si ocurre un error durante la inicialización
     */
    @Override
    public void init() throws ServletException {
        super.init();
        servicioUsuario = new UsuarioServicio();
        GestorRegistros.sistemaInfo("RecuperarContrasenaServlet inicializado correctamente");
    }
    
    /**
     * Maneja las peticiones GET mostrando el formulario de recuperación.
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
            GestorRegistros.sistemaInfo("Acceso a formulario de recuperación de contraseña");
            peticion.getRequestDispatcher("/recuperarContrasena.jsp").forward(peticion, respuesta);
        } catch (Exception error) {
            GestorRegistros.sistemaError("Error al mostrar formulario de recuperación: " + error.getMessage());
            throw error;
        }
    }
    
    /**
     * Procesa las solicitudes de recuperación de contraseña.
     * Verifica el usuario, genera y envía el token de recuperación.
     * 
     * @param peticion La petición HTTP que contiene el correo electrónico
     * @param respuesta La respuesta HTTP al cliente
     * @throws ServletException Si ocurre un error en el servlet
     * @throws IOException Si ocurre un error de E/S
     */
    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        String correoElectronico = peticion.getParameter("correoElectronico");
        
        GestorRegistros.sistemaInfo("Solicitud de recuperación para: " + correoElectronico);
        
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
            
            GestorRegistros.sistemaInfo("Correo de recuperación enviado exitosamente a: " + correoElectronico);
            GestorRegistros.info(correoUsuario.getId(), "Solicitud de recuperación de contraseña procesada");
            
            peticion.getSession().setAttribute("mensaje", 
                "Se ha enviado un enlace de recuperación a tu correo electrónico.");
            respuesta.sendRedirect(peticion.getContextPath() + "/acceso");
            
        } catch (Exception error) {
            GestorRegistros.sistemaError("Error en recuperación de contraseña para " + 
                correoElectronico + ": " + error.getMessage());
            peticion.setAttribute("error", 
                "Ha ocurrido un error al procesar tu solicitud. Por favor, inténtalo más tarde.");
            peticion.getRequestDispatcher("/recuperarContrasena.jsp").forward(peticion, respuesta);
        }
    }
}
