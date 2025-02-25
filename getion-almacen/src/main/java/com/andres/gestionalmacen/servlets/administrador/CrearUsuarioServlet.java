package com.andres.gestionalmacen.servlets.administrador;

import com.andres.gestionalmacen.dtos.CrearUsuDto;
import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.servicios.UsuarioServicio;
import com.andres.gestionalmacen.utilidades.EncriptarUtil;
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
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta) throws ServletException, IOException {
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

            
            // Obtener datos del formulario
            String nombreCompleto = peticion.getParameter("nombreCompleto");
            String correoElectronico = peticion.getParameter("correoElectronico");
            String movil = peticion.getParameter("movil");
            String contrasena = peticion.getParameter("contrasena");
            Long rolId = Long.valueOf(peticion.getParameter("rolId"));
            String contrasenaEncriptada = EncriptarUtil.hashPassword(contrasena);

            // Procesar la foto si existe
            Part fotoPart = peticion.getPart("foto");
            byte[] fotoBytes = null;
            if (fotoPart != null && fotoPart.getSize() > 0) {
                fotoBytes = fotoPart.getInputStream().readAllBytes();
                try {
                    // Verificar que sea una imagen válida
                    String nombreArchivo = fotoPart.getSubmittedFileName();
                    ImagenUtil.verificarImagen(fotoBytes, nombreArchivo);
                } catch (IllegalArgumentException e) {
                    System.out.println("Error de validación de imagen: " + e.getMessage());
                    peticion.getSession().setAttribute("error", "La imagen debe tener un formato válido (JPEG, PNG, GIF, BMP, WEBP) y la extensión debe coincidir con el tipo de archivo.");
                    respuesta.sendRedirect(peticion.getContextPath() + "/admin/usuarios");
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

            // Guardar el usuario
            usuarioServicio.crearUsuario(nuevoUsuario);

            // Redirigir de vuelta a la lista con mensaje de éxito
            peticion.getSession().setAttribute("mensaje", "Usuario creado con éxito");
            respuesta.sendRedirect(peticion.getContextPath() + "/admin/usuarios");

        } catch (Exception e) {
            e.printStackTrace();
            peticion.getSession().setAttribute("error", "Error al crear usuario: " + e.getMessage());
            respuesta.sendRedirect(peticion.getContextPath() + "/admin/usuarios");
        }
    }
}