package com.andres.gestionalmacen.servlets.administrador;

import com.andres.gestionalmacen.dtos.CrearUsuDto;
import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.servicios.UsuarioServicio;
import com.andres.gestionalmacen.utilidades.GestorRegistros;
import com.andres.gestionalmacen.utilidades.ImagenUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;

@WebServlet("/admin/usuarios/modificar")
@MultipartConfig
public class ModificarUsuarioServlet extends HttpServlet {

    private final UsuarioServicio usuarioServicio;

    public ModificarUsuarioServlet() {
        this.usuarioServicio = new UsuarioServicio();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Verificar sesión y rol
            HttpSession session = request.getSession(false);
            if (session == null) {
                GestorRegistros.sistemaWarning("Intento de modificar usuario sin sesión válida desde IP: " 
                    + request.getRemoteAddr());
                response.sendRedirect(request.getContextPath() + "/acceso.jsp");
                return;
            }

            UsuarioDto adminActual = (UsuarioDto) session.getAttribute("usuario");
            if (adminActual == null) {
                GestorRegistros.sistemaWarning("Intento de modificar usuario sin usuario en sesión desde IP: " 
                    + request.getRemoteAddr());
                response.sendRedirect(request.getContextPath() + "/acceso.jsp");
                return;
            }
            
            if (adminActual.getRolId() != 1) {
                GestorRegistros.warning(adminActual.getId(), 
                    "Intento no autorizado de modificar usuario. Rol actual: " + adminActual.getRolId());
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
                return;
            }

            // Obtener datos del formulario
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                GestorRegistros.warning(adminActual.getId(), "Intento de modificar usuario sin proporcionar ID");
                throw new ServletException("ID de usuario no proporcionado");
            }
            
            long id = Long.parseLong(idParam);
            String nombreCompleto = request.getParameter("nombreCompleto");
            String correoElectronico = request.getParameter("correoElectronico");
            String movil = request.getParameter("movil");
            Long rolId = Long.parseLong(request.getParameter("rolId"));

            // Crear DTO para actualización
            CrearUsuDto usuarioActualizar = new CrearUsuDto();
            usuarioActualizar.setNombreCompleto(nombreCompleto);
            usuarioActualizar.setCorreoElectronico(correoElectronico);
            usuarioActualizar.setRolId(rolId);
            usuarioActualizar.setMovil(movil);

            // Procesar la foto si existe
            Part fotoPart = request.getPart("foto");
            if (fotoPart != null && fotoPart.getSize() > 0) {
                byte[] fotoBytes = fotoPart.getInputStream().readAllBytes();
                try {
                    String nombreArchivo = fotoPart.getSubmittedFileName();
                    ImagenUtil.verificarImagen(fotoBytes, nombreArchivo);
                    usuarioActualizar.setFoto(fotoBytes);
                } catch (IllegalArgumentException e) {
                    GestorRegistros.warning(adminActual.getId(), 
                        "Error al procesar foto para usuario " + id + ": " + e.getMessage());
                    request.getSession().setAttribute("error", 
                        "La imagen debe tener un formato válido (JPEG, PNG, GIF, BMP, WEBP) y la extensión debe coincidir con el tipo de archivo.");
                    response.sendRedirect(request.getContextPath() + "/admin/usuarios");
                    return;
                }
            }

            // Actualizar el usuario
            usuarioServicio.actualizarUsuario(id, usuarioActualizar);
            GestorRegistros.info(adminActual.getId(), "Usuario modificado exitosamente. ID: " + id );

            // Redirigir de vuelta a la lista con mensaje de éxito
            request.getSession().setAttribute("mensaje", "Usuario actualizado con éxito");
            response.sendRedirect(request.getContextPath() + "/admin/usuarios");

        } catch (IllegalArgumentException e) {
            try {
                UsuarioDto admin = (UsuarioDto) request.getSession().getAttribute("usuario");
                GestorRegistros.error(admin.getId(), 
                    "Error de validación al modificar usuario: " + e.getMessage());
            } catch (Exception ex) {
                GestorRegistros.sistemaError("Error de validación al modificar usuario - IP: " 
                    + request.getRemoteAddr());
            }
            request.getSession().setAttribute("error", "Error al validar la imagen: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/usuarios");
        } catch (Exception e) {
            try {
                UsuarioDto admin = (UsuarioDto) request.getSession().getAttribute("usuario");
                GestorRegistros.error(admin.getId(), 
                    "Error al modificar usuario: " + e.getMessage());
            } catch (Exception ex) {
                GestorRegistros.sistemaError("Error al modificar usuario - IP: " + request.getRemoteAddr() 
                    + " - Error: " + e.getMessage());
            }
            request.getSession().setAttribute("error", "Error al actualizar usuario: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/usuarios");
        }
    }
}