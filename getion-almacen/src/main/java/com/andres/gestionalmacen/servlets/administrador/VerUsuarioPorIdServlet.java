package com.andres.gestionalmacen.servlets.administrador;

import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.servicios.UsuarioServicio;
import com.andres.gestionalmacen.utilidades.GestorRegistros;
import com.andres.gestionalmacen.utilidades.ImagenUtil;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/admin/verUsuarioId")
public class VerUsuarioPorIdServlet extends HttpServlet {

    private final UsuarioServicio usuarioServicio;

    public VerUsuarioPorIdServlet() {
        this.usuarioServicio = new UsuarioServicio();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Verificar sesión y permisos
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuario") == null) {
                GestorRegistros.sistemaWarning("Intento de ver usuario específico sin sesión válida desde IP: " 
                    + request.getRemoteAddr());
                response.sendRedirect(request.getContextPath() + "/acceso");
                return;
            }

            UsuarioDto adminActual = (UsuarioDto) session.getAttribute("usuario");
            if (adminActual.getRolId() != 1) {
                GestorRegistros.warning(adminActual.getId(), 
                    "Intento no autorizado de ver usuario específico. Rol actual: " + adminActual.getRolId());
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
                return;
            }

            // Obtener ID del usuario a ver
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                GestorRegistros.warning(adminActual.getId(), "Intento de ver usuario sin proporcionar ID");
                response.sendRedirect(request.getContextPath() + "/admin/usuarios");
                return;
            }

            Long userId = Long.parseLong(idStr);
            GestorRegistros.info(adminActual.getId(), "Acceso a detalles del usuario con ID: " + userId);

            // Obtener datos del usuario
            UsuarioDto usuario = usuarioServicio.obtenerUsuarioPorId(userId);

            if (usuario != null) {
                GestorRegistros.debug(adminActual.getId(), "Datos del usuario " + userId + " cargados correctamente");
                
                // Procesar foto si existe
                if (usuario.getFoto() != null) {
                    byte[] fotoConMime = ImagenUtil.asegurarMimeTypeImagen(usuario.getFoto());
                    if (fotoConMime != null) {
                        String fotoStr = new String(fotoConMime, StandardCharsets.UTF_8);
                        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder()
                            .add("id", usuario.getId())
                            .add("nombreCompleto", usuario.getNombreCompleto())
                            .add("correoElectronico", usuario.getCorreoElectronico())
                            .add("movil", usuario.getMovil() != null ? usuario.getMovil() : "")
                            .add("rolId", usuario.getRolId())
                            .add("foto", fotoStr);
                        JsonObject jsonUsuario = jsonBuilder.build();
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(jsonUsuario.toString());
                        GestorRegistros.debug(adminActual.getId(), "Foto del usuario " + userId + " procesada correctamente");
                    } else {
                        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder()
                            .add("id", usuario.getId())
                            .add("nombreCompleto", usuario.getNombreCompleto())
                            .add("correoElectronico", usuario.getCorreoElectronico())
                            .add("movil", usuario.getMovil() != null ? usuario.getMovil() : "")
                            .add("rolId", usuario.getRolId())
                            .addNull("foto");
                        JsonObject jsonUsuario = jsonBuilder.build();
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(jsonUsuario.toString());
                        GestorRegistros.warning(adminActual.getId(), "No se pudo procesar la foto del usuario " + userId);
                    }
                } else {
                    JsonObjectBuilder jsonBuilder = Json.createObjectBuilder()
                        .add("id", usuario.getId())
                        .add("nombreCompleto", usuario.getNombreCompleto())
                        .add("correoElectronico", usuario.getCorreoElectronico())
                        .add("movil", usuario.getMovil() != null ? usuario.getMovil() : "")
                        .add("rolId", usuario.getRolId())
                        .addNull("foto");
                    JsonObject jsonUsuario = jsonBuilder.build();
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(jsonUsuario.toString());
                }

            } else {
                GestorRegistros.warning(adminActual.getId(), "Usuario con ID " + userId + " no encontrado");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Usuario no encontrado");
            }

        } catch (NumberFormatException e) {
            // Error al parsear el ID
            try {
                UsuarioDto admin = (UsuarioDto) request.getSession().getAttribute("usuario");
                GestorRegistros.error(admin.getId(), "Error al parsear ID de usuario: " + e.getMessage());
            } catch (Exception ex) {
                GestorRegistros.sistemaError("Error al parsear ID de usuario - IP: " + request.getRemoteAddr());
            }
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de usuario inválido");
            
        } catch (Exception e) {
            // Error general
            try {
                UsuarioDto admin = (UsuarioDto) request.getSession().getAttribute("usuario");
                GestorRegistros.error(admin.getId(), "Error al ver detalles de usuario: " + e.getMessage());
            } catch (Exception ex) {
                GestorRegistros.sistemaError("Error al ver detalles de usuario: " + e.getMessage() 
                    + " - IP: " + request.getRemoteAddr());
            }
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener datos del usuario");
        }
    }
}