package com.andres.gestionalmacen.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.servicios.UsuarioServicio;
import com.andres.gestionalmacen.utilidades.EncriptarUtil;
import com.andres.gestionalmacen.utilidades.GestorRegistros;

/**
 * Servlet que maneja el proceso de inicio de sesión de usuarios.
 * Implementa la lógica de autenticación, validación de credenciales y redirección según el rol.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Validación de credenciales contra la base de datos</li>
 *   <li>Manejo de sesiones y cookies para "recordar usuario"</li>
 *   <li>Redirección basada en roles de usuario</li>
 *   <li>Validación de método de autenticación (normal vs Google)</li>
 *   <li>Registro detallado de actividades y errores</li>
 * </ul>
 * 
 * @author Andrés
 * @version 1.0
 */
@WebServlet("/acceso")
public class AccesoServlet extends HttpServlet {

    /**
     * Maneja las peticiones GET mostrando el formulario de inicio de sesión.
     * 
     * @param peticion La petición HTTP del cliente
     * @param respuesta La respuesta HTTP al cliente
     * @throws ServletException Si ocurre un error en el servlet
     * @throws IOException Si ocurre un error de E/S
     */
    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) throws ServletException, IOException {
        GestorRegistros.sistemaInfo("Acceso a página de acceso");
        peticion.getRequestDispatcher("/acceso.jsp").forward(peticion, respuesta);
    }

    /**
     * Procesa los intentos de inicio de sesión verificando las credenciales y gestionando la sesión.
     * 
     * <p>El método realiza las siguientes operaciones:</p>
     * <ol>
     *   <li>Valida el método de autenticación (normal vs Google)</li>
     *   <li>Verifica las credenciales del usuario</li>
     *   <li>Gestiona la sesión y cookies si se solicita "recordar usuario"</li>
     *   <li>Redirige al panel correspondiente según el rol del usuario</li>
     * </ol>
     * 
     * @param peticion La petición HTTP que contiene las credenciales
     * @param respuesta La respuesta HTTP al cliente
     * @throws ServletException Si ocurre un error en el servlet
     * @throws IOException Si ocurre un error de E/S
     */
    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta) throws ServletException, IOException {
        String correoElectronico = peticion.getParameter("correoElectronico");
        String contrasena = peticion.getParameter("contrasena");
        boolean recordar = peticion.getParameter("recordar") != null;

        GestorRegistros.sistemaInfo("Intento de acceso para usuario: " + correoElectronico);

        String contrasenaHasheada = EncriptarUtil.contraseñaHash(contrasena);
        UsuarioServicio servicioUsuario = new UsuarioServicio();

        try {
            // Verificar método de autenticación
            UsuarioDto usuarioExistente = servicioUsuario.buscarPorCorreo(correoElectronico);
            
            if (usuarioExistente != null && usuarioExistente.isGoogle()) {
                GestorRegistros.sistemaWarning("Intento de acceso con formulario para cuenta de Google: " + correoElectronico);
                peticion.setAttribute("error", "Esta cuenta fue registrada con Google. Por favor, use el botón 'Iniciar sesión con Google'.");
                peticion.setAttribute("correoElectronico", correoElectronico);
                peticion.getRequestDispatcher("/acceso.jsp").forward(peticion, respuesta);
                return;
            }

            // Validar credenciales
            UsuarioDto datosUsuario = servicioUsuario.validarCredenciales(correoElectronico, contrasenaHasheada);
            
            if (datosUsuario != null) {
                GestorRegistros.info(datosUsuario.getId(), "Acceso exitoso al sistema");
                GestorRegistros.sistemaInfo("Usuario con ID: " + datosUsuario.getId() + " accedió exitosamente. Rol: " + datosUsuario.getRolId());
                
                // Gestionar sesión y cookies
                HttpSession sesion = peticion.getSession();
                sesion.setAttribute("usuario", datosUsuario);

                if (recordar) {
                    Cookie galleta = new Cookie("usuario", correoElectronico);
                    galleta.setMaxAge(60 * 60 * 24 * 30); // 30 días
                    respuesta.addCookie(galleta);
                    GestorRegistros.info(datosUsuario.getId(), "Se ha activado la opción 'recordar usuario'");
                }

                // Redirección según rol
                String destino;
                switch (datosUsuario.getRolId().intValue()) {
                    case 1: // Admin
                        destino = "/admin/panel";
                        break;
                    case 2: // Gerente
                        destino = "/gerente/panel";
                        break;
                    case 3: // Operador
                        destino = "/operario/panel";
                        break;
                    case 4: // Usuario
                        destino = "/usuario/panel";
                        break;
                    default:
                        GestorRegistros.warning(datosUsuario.getId(), "Intento de acceso con rol no válido: " + datosUsuario.getRolId());
                        peticion.setAttribute("error", "Rol no válido");
                        peticion.getRequestDispatcher("/acceso.jsp").forward(peticion, respuesta);
                        return;
                }
                GestorRegistros.info(datosUsuario.getId(), "Redirigiendo a: " + destino);
                respuesta.sendRedirect(peticion.getContextPath() + destino);
                
            } else {
                GestorRegistros.sistemaWarning("Intento de acceso fallido para usuario: " + correoElectronico + " - Credenciales incorrectas");
                peticion.setAttribute("error", "¡Credenciales inválidas! Por favor, verifica tu correo y contraseña.");
                peticion.getRequestDispatcher("/acceso.jsp").forward(peticion, respuesta);
            }
        } catch (Exception error) {
            GestorRegistros.sistemaError("Error en el proceso de acceso para usuario " + correoElectronico + ": " + error.getMessage());
            
            String mensajeError = error.getMessage();
            if (mensajeError.contains("500")) {
                mensajeError = "Error en el servidor. Por favor, inténtelo más tarde.";
            }
            peticion.setAttribute("error", mensajeError);
            peticion.setAttribute("correoElectronico", correoElectronico);
            peticion.getRequestDispatcher("/acceso.jsp").forward(peticion, respuesta);
        }
    }
}