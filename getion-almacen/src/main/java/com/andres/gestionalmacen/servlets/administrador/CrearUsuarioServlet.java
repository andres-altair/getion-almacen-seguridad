package com.andres.gestionalmacen.servlets.administrador;

import com.andres.gestionalmacen.dtos.CrearUsuDto;
import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.servicios.UsuarioServicio;
import com.andres.gestionalmacen.utilidades.EncriptarUtil;
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

@WebServlet("/admin/usuarios/crear")
@MultipartConfig
public class CrearUsuarioServlet extends HttpServlet {

    private final UsuarioServicio usuarioServicio;

    public CrearUsuarioServlet() {
        this.usuarioServicio = new UsuarioServicio();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Verificar sesión y permisos
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuario") == null) {
                GestorRegistros.sistemaWarning("Intento de crear usuario sin sesión válida desde IP: " 
                    + request.getRemoteAddr());
                response.sendRedirect(request.getContextPath() + "/acceso");
                return;
            }
            
            UsuarioDto adminActual = (UsuarioDto) session.getAttribute("usuario");
            if (adminActual.getRolId() != 1) {
                GestorRegistros.warning(adminActual.getId(), 
                    "Intento no autorizado de crear usuario. Rol actual: " + adminActual.getRolId());
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
                return;
            }

            // Obtener datos del formulario
            String nombreCompleto = request.getParameter("nombreCompleto");
            String correoElectronico = request.getParameter("correoElectronico");
            String movil = request.getParameter("movil");
            String contrasena = request.getParameter("contrasena");
            Long rolId = Long.valueOf(request.getParameter("rolId"));
            
            GestorRegistros.info(adminActual.getId(), 
                "Iniciando creación de usuario: " + correoElectronico + " con rol: " + rolId);

            String contrasenaEncriptada = EncriptarUtil.contraseñaHash(contrasena);

            // Procesar la foto si existe
            Part fotoPart = request.getPart("foto");
            byte[] fotoBytes = null;
            if (fotoPart != null && fotoPart.getSize() > 0) {
                fotoBytes = fotoPart.getInputStream().readAllBytes();
                try {
                    String nombreArchivo = fotoPart.getSubmittedFileName();
                    ImagenUtil.verificarImagen(fotoBytes, nombreArchivo);
                } catch (IllegalArgumentException e) {
                    GestorRegistros.warning(adminActual.getId(), 
                        "Error al procesar foto para usuario " + correoElectronico + ": " + e.getMessage());
                    request.getSession().setAttribute("error", 
                        "La imagen debe tener un formato válido (JPEG, PNG, GIF, BMP, WEBP) y la extensión debe coincidir con el tipo de archivo.");
                    response.sendRedirect(request.getContextPath() + "/admin/usuarios");
                    return;
                }
            }

            // Crear el DTO
            CrearUsuDto nuevoUsuario = new CrearUsuDto();
            nuevoUsuario.setNombreCompleto(nombreCompleto);
            nuevoUsuario.setMovil(movil);
            nuevoUsuario.setCorreoElectronico(correoElectronico);
            nuevoUsuario.setContrasena(contrasenaEncriptada);
            nuevoUsuario.setRolId(rolId);
            nuevoUsuario.setFoto(fotoBytes);
            nuevoUsuario.setCorreoConfirmado(true); // Por defecto, está confirmado
            nuevoUsuario.setGoogle(false); // Los usuarios creados por el admin no son de Google

            // Guardar el usuario
            usuarioServicio.crearUsuario(nuevoUsuario);
            GestorRegistros.info(adminActual.getId(), 
                "Usuario creado exitosamente: " + correoElectronico);

            // Redirigir de vuelta a la lista con mensaje de éxito
            request.getSession().setAttribute("mensaje", "Usuario creado con éxito");
            response.sendRedirect(request.getContextPath() + "/admin/usuarios");

        } catch (Exception e) {
            try {
                UsuarioDto admin = (UsuarioDto) request.getSession().getAttribute("usuario");
                GestorRegistros.error(admin.getId(), 
                    "Error al crear usuario: " + e.getMessage());
            } catch (Exception ex) {
                GestorRegistros.sistemaError("Error al crear usuario - IP: " + request.getRemoteAddr() 
                    + " - Error: " + e.getMessage());
            }
            request.getSession().setAttribute("error", "Error al crear usuario: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/usuarios");
        }
    }
}