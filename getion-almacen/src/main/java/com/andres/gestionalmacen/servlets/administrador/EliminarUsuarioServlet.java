package com.andres.gestionalmacen.servlets.administrador;

import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.servicios.UsuarioServicio;
import com.andres.gestionalmacen.utilidades.GestorRegistros;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet que maneja la eliminación de usuarios.
 * Este servlet verifica la sesión del administrador y elimina un usuario del sistema.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Verificación de sesión y permisos del administrador</li>
 *   <li>Validación del ID del usuario a eliminar</li>
 *   <li>Eliminación del usuario en el sistema</li>
 * </ul>
 * 
 * <p>Según [875eb101-5aa8-4067-87e7-39617e3a474a], esta clase maneja el registro
 * de eventos relacionados con la eliminación de usuarios.</p>
 * 
 * @author Andrés
 * @version 1.0
 */
@WebServlet("/admin/usuarios/eliminar")
public class EliminarUsuarioServlet extends HttpServlet {

    private final UsuarioServicio usuarioServicio;

    /**
     * Constructor que inicializa el servicio de usuarios.
     */
    public EliminarUsuarioServlet() {
        this.usuarioServicio = new UsuarioServicio();
    }

    /**
     * Método que maneja la petición POST para eliminar un usuario.
     * 
     * @param request  objeto que contiene la petición HTTP
     * @param response objeto que contiene la respuesta HTTP
     * @throws ServletException si ocurre un error en la ejecución del servlet
     * @throws IOException      si ocurre un error en la lectura o escritura de la petición o respuesta
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Obtener la sesión del usuario
        HttpSession session = request.getSession(false);
        
        // Verificar si la sesión es válida
        if (session == null || session.getAttribute("usuario") == null) {
            GestorRegistros.sistemaWarning("Intento de eliminar usuario sin sesión válida desde IP: " 
                + request.getRemoteAddr());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sesión no válida");
            return;
        }

        // Obtener el usuario actual
        UsuarioDto adminActual = (UsuarioDto) session.getAttribute("usuario");
        
        // Verificar si el usuario tiene permisos de administrador
        if (adminActual.getRolId() != 1) {
            GestorRegistros.warning(adminActual.getId(), 
                "Intento no autorizado de eliminar usuario. Rol actual: " + adminActual.getRolId());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
            return;
        }

        try {
            // Obtener y validar el ID del usuario a eliminar
            String idStr = request.getParameter("id");
            String confirmacionId = request.getParameter("confirmacionId");
            
            if (idStr == null || idStr.trim().isEmpty()) {
                GestorRegistros.warning(adminActual.getId(), "Intento de eliminación sin proporcionar ID");
                throw new Exception("ID de usuario no proporcionado");
            }
            
            if (confirmacionId == null || !confirmacionId.equals(idStr)) {
                GestorRegistros.warning(adminActual.getId(), 
                    "ID de confirmación no coincide para eliminación. ID: " + idStr);
                throw new Exception("El ID de confirmación no coincide");
            }

            // Convertir el ID a Long
            Long id = Long.parseLong(idStr);
            
            // Registrar el inicio de la eliminación del usuario
            GestorRegistros.info(adminActual.getId(), "Iniciando eliminación del usuario con ID: " + id);

            // Verificar que no se está intentando eliminar al usuario actual
            if (adminActual.getId().equals(id)) {
                GestorRegistros.warning(adminActual.getId(), 
                    "Intento de eliminar la propia cuenta del administrador");
                throw new Exception("No puedes eliminar tu propia cuenta");
            }

            // Intentar eliminar el usuario
            usuarioServicio.eliminarUsuario(id);
            
            // Registrar la eliminación exitosa del usuario
            GestorRegistros.info(adminActual.getId(), "Usuario eliminado exitosamente. ID: " + id);

            // Redirigir a la página de gestión de usuarios
            response.sendRedirect(request.getContextPath() + "/admin/usuarios");
            
        } catch (NumberFormatException e) {
            // Registrar el error al parsear el ID del usuario
            GestorRegistros.error(adminActual.getId(), 
                "Error al parsear ID de usuario para eliminación: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de usuario inválido");
            
        } catch (Exception e) {
            try {
                // Registrar el error al eliminar el usuario
                GestorRegistros.error(adminActual.getId(), 
                    "Error al eliminar usuario: " + e.getMessage());
            } catch (Exception ex) {
                // Registrar el error en el sistema
                GestorRegistros.sistemaError("Error al eliminar usuario - IP: " + request.getRemoteAddr() 
                    + " - Error: " + e.getMessage());
            }
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al eliminar usuario: " + e.getMessage());
        }
    }
}