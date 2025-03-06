package com.andres.gestionalmacen.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.util.Base64;

import com.andres.gestionalmacen.dtos.CrearUsuDto;
import com.andres.gestionalmacen.servicios.UsuarioServicio;
import com.andres.gestionalmacen.utilidades.EmailUtil;
import com.andres.gestionalmacen.utilidades.EncriptarUtil;
import com.andres.gestionalmacen.utilidades.GestorRegistros;
import com.andres.gestionalmacen.utilidades.ImagenUtil;

@WebServlet("/registro")
@MultipartConfig
public class RegistroServlet extends HttpServlet {
    private final UsuarioServicio usuarioServicio;
    
    public RegistroServlet() {
        this.usuarioServicio = new UsuarioServicio();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        GestorRegistros.sistemaInfo("Acceso a página de registro");
        request.getRequestDispatcher("/registro.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Obtener datos del formulario
            String nombreCompleto = request.getParameter("nombreCompleto");
            String correoElectronico = request.getParameter("correoElectronico");
            String movil = request.getParameter("movil");
            String contrasena = request.getParameter("contrasena");
            Long rolId = 4L;

            String contrasenaEncriptada = EncriptarUtil.hashPassword(contrasena);

            // Procesar la foto si existe
            Part fotoPart = request.getPart("foto");
            byte[] fotoBytes = null;
            if (fotoPart != null && fotoPart.getSize() > 0) {
                fotoBytes = fotoPart.getInputStream().readAllBytes();
                try {
                    String nombreArchivo = fotoPart.getSubmittedFileName();
                    ImagenUtil.verificarImagen(fotoBytes, nombreArchivo);
                } catch (IllegalArgumentException e) {
                    GestorRegistros.sistemaWarning("Error al procesar foto para usuario " + correoElectronico + ": " + e.getMessage());
                    request.getSession().setAttribute("error", 
                        "La imagen debe tener un formato válido (JPEG, PNG, GIF, BMP, WEBP) y la extensión debe coincidir con el tipo de archivo.");
                    response.sendRedirect(request.getContextPath() + "/registro");
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
            nuevoUsuario.setCorreoConfirmado(false); // Por defecto, no está confirmado
            nuevoUsuario.setGoogle(false); // Los usuarios registrados no son de Google

            // Guardar el usuario
            usuarioServicio.crearUsuario(nuevoUsuario);

            // Generar y enviar confirmación por correo
            String token = EmailUtil.generarToken(correoElectronico);
            EmailUtil.enviarCorreoConfirmacion(correoElectronico, token);
            
            // Redirigir con mensaje de éxito
            request.getSession().setAttribute("mensaje", "Te hemos enviado un correo de confirmación. Por favor, revisa tu bandeja de entrada.");
            response.sendRedirect(request.getContextPath() + "/acceso");

        } catch (Exception e) {
            GestorRegistros.sistemaError("Error en el registro de usuario - Error: " + e.getMessage());
            request.getSession().setAttribute("error", "Error en el registro: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/registro");
        }
    }
}
