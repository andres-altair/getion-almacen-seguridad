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
 * Servlet que gestiona el acceso de usuarios al sistema.
 * Este servlet maneja la autenticación y el control de acceso de los usuarios,
 * incluyendo el inicio de sesión tradicional y la autenticación con Google.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Validación de credenciales del usuario</li>
 *   <li>Creación y gestión de sesiones</li>
 *   <li>Redirección según el rol del usuario (ADMIN, OPERARIO, USUARIO)</li>
 *   <li>Manejo de errores de autenticación</li>
 * </ul>
 *
 * @author Andrés
 * @version 1.0
 */
@WebServlet("/acceso")
public class AccesoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Servicio para operaciones con la tabla de usuarios.
     */
    private final UsuarioServicio usuarioServicio;

    /**
     * Constructor del servlet.
     * Inicializa el servicio necesario para las operaciones con usuarios.
     */
    public AccesoServlet() {
        super();
        this.usuarioServicio = new UsuarioServicio();
    }

    /**
     * Procesa las peticiones GET a la página de acceso.
     * Si existe una sesión activa, redirige al usuario a su panel correspondiente.
     * Si no hay sesión, muestra la página de acceso.
     *
     * @param peticion La petición HTTP del cliente
     * @param respuesta La respuesta HTTP al cliente
     * @throws ServletException Si ocurre un error en el procesamiento del servlet
     * @throws IOException Si ocurre un error de entrada/salida
     */
    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        
        HttpSession sesion = peticion.getSession(false);
        
        if (sesion != null && sesion.getAttribute("usuario") != null) {
            // Redirigir usuario autenticado a su panel
            UsuarioDto usuario = (UsuarioDto) sesion.getAttribute("usuario");
            redirigirSegunRol(usuario, peticion, respuesta);
        } else {
            // Mostrar página de acceso
            peticion.getRequestDispatcher("/acceso.jsp").forward(peticion, respuesta);
        }
    }

    /**
     * Procesa las peticiones POST para la autenticación de usuarios.
     * Valida las credenciales proporcionadas y establece la sesión si son correctas.
     *
     * @param peticion La petición HTTP del cliente
     * @param respuesta La respuesta HTTP al cliente
     * @throws ServletException Si ocurre un error en el procesamiento del servlet
     * @throws IOException Si ocurre un error de entrada/salida
     */
    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        
        String correoElectronico = peticion.getParameter("correoElectronico");
        String contrasena = peticion.getParameter("contrasena");
        boolean recordar = peticion.getParameter("recordar") != null;

        GestorRegistros.sistemaInfo("Intento de acceso para usuario: " + correoElectronico);

        String contrasenaHasheada = EncriptarUtil.contraseñaHash(contrasena);

        try {
            // Verificar método de autenticación
            UsuarioDto usuarioExistente = usuarioServicio.buscarPorCorreo(correoElectronico);
            
            if (usuarioExistente != null && usuarioExistente.isGoogle()) {
                GestorRegistros.sistemaWarning("Intento de acceso con formulario para cuenta de Google: " + correoElectronico);
                peticion.getSession().setAttribute("error", "Esta cuenta fue registrada con Google. Por favor, use el botón 'Iniciar sesión con Google'.");
                peticion.setAttribute("correoElectronico", correoElectronico);
                peticion.getRequestDispatcher("/acceso.jsp").forward(peticion, respuesta);
                return;
            }

            // Validar credenciales
            UsuarioDto datosUsuario = usuarioServicio.validarCredenciales(correoElectronico, contrasenaHasheada);
            
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

                // Redirigir según rol
                redirigirSegunRol(datosUsuario, peticion, respuesta);
            } else {
                GestorRegistros.sistemaWarning("Intento de acceso fallido para usuario: " + correoElectronico + " - Credenciales incorrectas");
                peticion.getSession().setAttribute("error", "¡Credenciales inválidas! Por favor, verifica tu correo y contraseña.");
                peticion.setAttribute("correoElectronico", correoElectronico);
                peticion.getRequestDispatcher("/acceso.jsp").forward(peticion, respuesta);
            }
        } catch (Exception error) {
            GestorRegistros.sistemaError("Error en el proceso de acceso para usuario " + correoElectronico + ": " + error.getMessage());
            
            String mensajeError = error.getMessage();
            if (mensajeError.contains("500")) {
                mensajeError = "Error en el servidor. Por favor, inténtelo más tarde.";
            }
            peticion.getSession().setAttribute("error", mensajeError);
            peticion.setAttribute("correoElectronico", correoElectronico);
            peticion.getRequestDispatcher("/acceso.jsp").forward(peticion, respuesta);
        }
    }

    /**
     * Redirige al usuario al panel correspondiente según su rol.
     * Los roles pueden ser: ADMIN, OPERARIO o USUARIO (por defecto).
     *
     * @param usuario El usuario autenticado
     * @param peticion La petición HTTP actual
     * @param respuesta La respuesta HTTP para la redirección
     * @throws IOException Si ocurre un error en la redirección
     */
    private void redirigirSegunRol(UsuarioDto usuario, HttpServletRequest peticion, 
            HttpServletResponse respuesta) throws IOException {
        
        String contextPath = peticion.getContextPath();
        
        switch (usuario.getRolId().intValue()) {
            case 1: // Admin
                respuesta.sendRedirect(contextPath + "/admin/panel");
                break;
            case 2: // Gerente
                respuesta.sendRedirect(contextPath + "/gerente/panel");
                break;
            case 3: // Operador
                respuesta.sendRedirect(contextPath + "/operario/panel");
                break;
            case 4: // Usuario
                respuesta.sendRedirect(contextPath + "/usuario/panel");
                break;
            default:
                GestorRegistros.warning(usuario.getId(), "Intento de acceso con rol no válido: " + usuario.getRolId());
                peticion.getSession().setAttribute("error", "Rol no válido");
                peticion.setAttribute("correoElectronico", usuario.getCorreoElectronico());
                try {
                    peticion.getRequestDispatcher("/acceso.jsp").forward(peticion, respuesta);
                } catch (ServletException | IOException e) {
                    GestorRegistros.sistemaError("Error al redirigir al panel de acceso: " + e.getMessage());
                }
                break;
        }
    }
}