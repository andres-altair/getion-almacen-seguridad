package com.andres.gestionalmacen.servlets.administrador;

import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.servicios.UsuarioServicio;
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

@WebServlet("/admin/usuarios/verId")
public class VerUsuarioPorIdServlet extends HttpServlet {

    private final UsuarioServicio usuarioServicio;

    public VerUsuarioPorIdServlet() {
        this.usuarioServicio = new UsuarioServicio();
    }

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) throws ServletException, IOException {
        try {

             // Verificar sesión y rol (código existente)
            HttpSession session = peticion.getSession(false);
            System.out.println("Sesión: " + (session != null ? "existe" : "no existe"));
            
            if (session == null || session.getAttribute("usuario") == null) {
                System.out.println("GestionUsuariosServlet.doGet - No hay sesión activa o usuario no encontrado en sesión");
                respuesta.sendRedirect(peticion.getContextPath() + "/acceso.jsp");
                return;
            }
            
            UsuarioDto usuarioActual = (UsuarioDto) session.getAttribute("usuario");
            System.out.println("Usuario en sesión: " + usuarioActual.getNombreCompleto());
            System.out.println("Rol del usuario: " + usuarioActual.getRolId());
            
            if (usuarioActual.getRolId() != 1) {
                System.out.println("GestionUsuariosServlet.doGet - Usuario no es admin: " + usuarioActual.getRolId());
                respuesta.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
                return;
            }
            // Validar que el ID no esté vacío
            String usuarioIdStr = peticion.getParameter("usuarioId");
            System.out.println("ID recibido en el servlet: " + usuarioIdStr); // Agregar este log

            System.out.println("Servlet VerUsuarioPorId llamado");
            System.out.println("Parámetros recibidos: " + peticion.getParameterMap().keySet());
            System.out.println("ID recibido en el servlet: " + peticion.getParameter("usuarioId"));
            
            if (usuarioIdStr == null || usuarioIdStr.trim().isEmpty()) {
                System.err.println("ID de usuario vacío o nulo");
                respuesta.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de usuario no proporcionado");
                return;
            }

            try {
                Long userId = Long.parseLong(usuarioIdStr.trim());
                UsuarioDto usuario = usuarioServicio.obtenerUsuarioPorId(userId);

                if (usuario != null) {
                    respuesta.setContentType("application/json");
                    respuesta.setCharacterEncoding("UTF-8");

                    JsonObjectBuilder jsonBuilder = Json.createObjectBuilder()
                        .add("id", usuario.getId())
                        .add("nombreCompleto", usuario.getNombreCompleto())
                        .add("correoElectronico", usuario.getCorreoElectronico())
                        .add("movil", usuario.getMovil() != null ? usuario.getMovil() : "")
                        .add("rolId", usuario.getRolId());

                    // Procesar la foto con MIME type
                    if (usuario.getFoto() != null) {
                        byte[] fotoConMime = ImagenUtil.asegurarMimeTypeImagen(usuario.getFoto());
                        if (fotoConMime != null) {
                            String fotoBase64 = new String(fotoConMime, StandardCharsets.UTF_8);
                            jsonBuilder.add("foto", fotoBase64);
                        }
                    } else {
                        jsonBuilder.addNull("foto");
                    }

                    JsonObject jsonUsuario = jsonBuilder.build();
                    respuesta.getWriter().write(jsonUsuario.toString());
                } else {
                    respuesta.sendError(HttpServletResponse.SC_NOT_FOUND, "Usuario no encontrado");
                }
            } catch (NumberFormatException e) {
                System.err.println("Error al convertir ID: " + usuarioIdStr);
                respuesta.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de usuario inválido");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error en VerUsuarioPorIdServlet: " + e.getMessage());
            respuesta.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener datos del usuario");
        }
    }
}