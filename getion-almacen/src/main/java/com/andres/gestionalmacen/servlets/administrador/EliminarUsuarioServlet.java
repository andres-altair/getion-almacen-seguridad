package com.andres.gestionalmacen.servlets.administrador;

import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.servicios.UsuarioServicio;
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
        
        System.out.println("\n=== Procesando solicitud de eliminación de usuario ===");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            System.err.println("Error: Sesión no válida");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sesión no válida");
            return;
        }

        try {
            // Obtener y validar el ID
            String idStr = request.getParameter("id");
            String confirmacionId = request.getParameter("confirmacionId");
            
            if (idStr == null || idStr.trim().isEmpty()) {
                throw new Exception("ID de usuario no proporcionado");
            }
            
            if (confirmacionId == null || !confirmacionId.equals(idStr)) {
                throw new Exception("El ID de confirmación no coincide");
            }

            Long id = Long.parseLong(idStr);
            System.out.println("ID a eliminar: " + id);

            // Verificar que no se está intentando eliminar al usuario actual
            UsuarioDto usuarioActual = (UsuarioDto) session.getAttribute("usuario");
            if (usuarioActual.getId().equals(id)) {
                throw new Exception("No puedes eliminar tu propia cuenta");
            }

            // Intentar eliminar el usuario
            usuarioServicio.eliminarUsuario(id);

            // Si llegamos aquí, la eliminación fue exitosa
            // Redirigir a la página de gestión de usuarios
            response.sendRedirect(request.getContextPath() + "/admin/usuarios");
            
        } catch (NumberFormatException e) {
            System.err.println("Error: ID de usuario inválido - " + e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de usuario inválido");
            
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al eliminar usuario: " + e.getMessage());
        }
    }
}