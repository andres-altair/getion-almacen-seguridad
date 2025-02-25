package com.andres.gestionalmacen.servlets.administrador;

import com.andres.gestionalmacen.dtos.CrearUsuDto;
import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.servicios.UsuarioServicio;
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
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta) throws ServletException, IOException {
        try {
            System.out.println("\n=== ModificarUsuarioServlet.doPost - Iniciando ===");
            
            // Verificar sesión y rol
            HttpSession session = peticion.getSession(false);
            System.out.println("Sesión encontrada: " + (session != null));
            
            if (session == null) {
                System.out.println("No hay sesión activa");
                respuesta.sendRedirect(peticion.getContextPath() + "/acceso.jsp");
                return;
            }

            UsuarioDto usuarioActual = (UsuarioDto) session.getAttribute("usuario");
            System.out.println("Usuario en sesión: " + (usuarioActual != null ? 
                "ID=" + usuarioActual.getId() + ", Nombre=" + usuarioActual.getNombreCompleto() + 
                ", Rol=" + usuarioActual.getRolId() : "null"));
            
            if (usuarioActual == null) {
                System.out.println("No hay usuario en la sesión");
                respuesta.sendRedirect(peticion.getContextPath() + "/acceso.jsp");
                return;
            }
            
            if (usuarioActual.getRolId() != 1) {
                System.out.println("Usuario no es administrador: " + usuarioActual.getRolId());
                respuesta.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
                return;
            }

            System.out.println("Verificación de sesión y rol exitosa");

            System.out.println("Recibiendo petición para modificar usuario");
            // Obtener datos del formulario
            String idParam = peticion.getParameter("id");
            System.out.println("ID recibido: " + idParam);
            
            if (idParam == null || idParam.trim().isEmpty()) {
                throw new ServletException("ID de usuario no proporcionado");
            }
            
            long id = Long.parseLong(idParam);
            String nombreCompleto = peticion.getParameter("nombreCompleto");
            String correoElectronico = peticion.getParameter("correoElectronico");
            String movil = peticion.getParameter("movil");
            Long rolId = Long.parseLong(peticion.getParameter("rolId"));
            
            System.out.println("Datos recibidos:");
            System.out.println("- ID: " + id);
            System.out.println("- Nombre: " + nombreCompleto);
            System.out.println("- Correo: " + correoElectronico);
            System.out.println("- Móvil: " + movil);
            System.out.println("- Rol ID: " + rolId);

            // Crear DTO para actualización
            CrearUsuDto usuarioActualizar = new CrearUsuDto();
            usuarioActualizar.setNombreCompleto(nombreCompleto);
            usuarioActualizar.setCorreoElectronico(correoElectronico);
            usuarioActualizar.setRolId(rolId);
            usuarioActualizar.setMovil(movil);

            // Procesar la foto si existe
            Part fotoPart = peticion.getPart("foto");
            if (fotoPart != null && fotoPart.getSize() > 0) {
                System.out.println("Procesando foto nueva");
                byte[] fotoBytes = fotoPart.getInputStream().readAllBytes();
                try {
                    // Verificar que sea una imagen válida
                    String nombreArchivo = fotoPart.getSubmittedFileName();
                    ImagenUtil.verificarImagen(fotoBytes, nombreArchivo);
                    usuarioActualizar.setFoto(fotoBytes);
                } catch (IllegalArgumentException e) {
                    System.out.println("Error de validación de imagen: " + e.getMessage());
                    peticion.getSession().setAttribute("error", "La imagen debe tener un formato válido (JPEG, PNG, GIF, BMP, WEBP) y la extensión debe coincidir con el tipo de archivo.");
                    respuesta.sendRedirect(peticion.getContextPath() + "/admin/usuarios");
                    return;
                }
            } else {
                System.out.println("No se recibió nueva foto");
            }

            // Actualizar el usuario
            System.out.println("Actualizando usuario en la base de datos...");
            usuarioServicio.actualizarUsuario(id, usuarioActualizar);

            // Redirigir de vuelta a la lista con mensaje de éxito
            System.out.println("Usuario actualizado exitosamente");
            peticion.getSession().setAttribute("mensaje", "Usuario actualizado con éxito");
            respuesta.sendRedirect(peticion.getContextPath() + "/admin/usuarios");

        } catch (IllegalArgumentException e) {
            System.out.println("Error de validación: " + e.getMessage());
            peticion.getSession().setAttribute("error", "Error al validar la imagen: " + e.getMessage());
            respuesta.sendRedirect(peticion.getContextPath() + "/admin/usuarios");
        } catch (Exception e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
            peticion.getSession().setAttribute("error", "Error al actualizar usuario: " + e.getMessage());
            respuesta.sendRedirect(peticion.getContextPath() + "/admin/usuarios");
        }
    }
}