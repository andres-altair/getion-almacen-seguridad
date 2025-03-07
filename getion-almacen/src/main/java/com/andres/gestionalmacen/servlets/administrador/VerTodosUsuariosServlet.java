package com.andres.gestionalmacen.servlets.administrador;

import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.servicios.UsuarioServicio;
import com.andres.gestionalmacen.utilidades.GestorRegistros;
import com.andres.gestionalmacen.utilidades.ImagenUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet que maneja la visualización de todos los usuarios.
 * Este servlet verifica la sesión del administrador y obtiene la lista de usuarios
 * desde el sistema.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Verificación de sesión y permisos del administrador</li>
 *   <li>Obtención de la lista de usuarios</li>
 *   <li>Registro de eventos relacionados con la gestión de usuarios</li>
 * </ul>
 * 
 * <p>Según [875eb101-5aa8-4067-87e7-39617e3a474a], esta clase maneja el registro
 * de eventos relacionados con la visualización de usuarios.</p>
 * 
 * @author Andrés
 * @version 1.0
 */
@WebServlet("/admin/usuarios")
public class VerTodosUsuariosServlet extends HttpServlet {

    private final UsuarioServicio usuarioServicio;

    /**
     * Constructor que inicializa el servicio de usuarios.
     */
    public VerTodosUsuariosServlet() {
        this.usuarioServicio = new UsuarioServicio();
    }

    /**
     * Método que maneja la solicitud GET para visualizar la lista de usuarios.
     * 
     * @param peticion  La solicitud HTTP.
     * @param respuesta La respuesta HTTP.
     * @throws ServletException Si ocurre un error en la ejecución del servlet.
     * @throws IOException      Si ocurre un error en la lectura o escritura de la solicitud o respuesta.
     */
    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) throws ServletException, IOException {
        try {
            // Verificar sesión y rol (código existente)
            HttpSession session = peticion.getSession(false);
            
            if (session == null || session.getAttribute("usuario") == null) {
                GestorRegistros.sistemaWarning("Intento de acceso a gestión de usuarios sin sesión válida desde IP: " 
                    + peticion.getRemoteAddr());
                respuesta.sendRedirect(peticion.getContextPath() + "/acceso");
                return;
            }
            
            UsuarioDto usuarioActual = (UsuarioDto) session.getAttribute("usuario");
            
            if (usuarioActual.getRolId() != 1) {
                GestorRegistros.warning(usuarioActual.getId(), 
                    "Intento de acceso no autorizado a gestión de usuarios. Rol actual: " + usuarioActual.getRolId());
                respuesta.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
                return;
            }
            
            // Log de acceso exitoso
            GestorRegistros.info(usuarioActual.getId(), "Acceso a la gestión de usuarios");

            // Obtener y mostrar lista de usuarios
            List<UsuarioDto> usuarios = usuarioServicio.obtenerUsuarios();
            if (usuarios != null) {
                // Crear un mapa para almacenar las fotos convertidas
                Map<Long, String> fotosBase64 = new HashMap<>();
                
                for (UsuarioDto user : usuarios) {
                    if (user.getFoto() != null) {
                        byte[] fotoConMime = ImagenUtil.asegurarMimeTypeImagen(user.getFoto());
                        if (fotoConMime != null) {
                            String fotoBase64 = new String(fotoConMime, StandardCharsets.UTF_8);
                            fotosBase64.put(user.getId(), fotoBase64);
                        }
                    }
                }
                peticion.setAttribute("fotosBase64", fotosBase64);
                peticion.setAttribute("usuarios", usuarios);
            } else {
                GestorRegistros.warning(usuarioActual.getId(), "No se encontraron usuarios en el sistema");
            }

            peticion.getRequestDispatcher("/admin/gestionUsuarios.jsp").forward(peticion, respuesta);

        } catch (Exception e) {
            // Obtener el ID del usuario si está disponible
            Long userId = null;
            try {
                HttpSession session = peticion.getSession(false);
                if (session != null && session.getAttribute("usuario") != null) {
                    userId = ((UsuarioDto) session.getAttribute("usuario")).getId();
                }
            } catch (Exception ex) {
                // Si hay error al obtener el usuario, se registrará como error del sistema
            }

            // Registrar el error
            if (userId != null) {
                GestorRegistros.error(userId, "Error al cargar la gestión de usuarios: " + e.getMessage());
            } else {
                GestorRegistros.sistemaError("Error al cargar la gestión de usuarios: " + e.getMessage() 
                    + " - IP: " + peticion.getRemoteAddr());
            }

            // Redirigir a una página de error o al panel de administración
            respuesta.sendRedirect(peticion.getContextPath() + "/admin/panel");
        }
    }
}