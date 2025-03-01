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

@WebServlet("/admin/usuarios/eliminar")
public class EliminarUsuarioServlet extends HttpServlet {

    private final UsuarioServicio usuarioServicio;

    public EliminarUsuarioServlet() {
        this.usuarioServicio = new UsuarioServicio();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            GestorRegistros.sistemaWarning("Intento de eliminar usuario sin sesión válida desde IP: " 
                + request.getRemoteAddr());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sesión no válida");
            return;
        }

        UsuarioDto adminActual = (UsuarioDto) session.getAttribute("usuario");
        if (adminActual.getRolId() != 1) {
            GestorRegistros.warning(adminActual.getId(), 
                "Intento no autorizado de eliminar usuario. Rol actual: " + adminActual.getRolId());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
            return;
        }

        try {
            // Obtener y validar el ID
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

            Long id = Long.parseLong(idStr);
            GestorRegistros.info(adminActual.getId(), "Iniciando eliminación del usuario con ID: " + id);

            // Verificar que no se está intentando eliminar al usuario actual
            if (adminActual.getId().equals(id)) {
                GestorRegistros.warning(adminActual.getId(), 
                    "Intento de eliminar la propia cuenta del administrador");
                throw new Exception("No puedes eliminar tu propia cuenta");
            }

            // Intentar eliminar el usuario
            usuarioServicio.eliminarUsuario(id);
            GestorRegistros.info(adminActual.getId(), "Usuario eliminado exitosamente. ID: " + id);

            // Redirigir a la página de gestión de usuarios
            response.sendRedirect(request.getContextPath() + "/admin/usuarios");
            
        } catch (NumberFormatException e) {
            GestorRegistros.error(adminActual.getId(), 
                "Error al parsear ID de usuario para eliminación: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de usuario inválido");
            
        } catch (Exception e) {
            try {
                GestorRegistros.error(adminActual.getId(), 
                    "Error al eliminar usuario: " + e.getMessage());
            } catch (Exception ex) {
                GestorRegistros.sistemaError("Error al eliminar usuario - IP: " + request.getRemoteAddr() 
                    + " - Error: " + e.getMessage());
            }
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al eliminar usuario: " + e.getMessage());
        }
    }
}