package com.andres.gestionalmacen.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.andres.gestionalmacen.dtos.CrearUsuDto;
import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.servicios.UsuarioServicio;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

/**
 * Servlet para manejar el registro inicial de usuarios a través de Google.
 */
@WebServlet("/GoogleRegistroServlet")
public class GoogleRegistroServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(GoogleRegistroServlet.class.getName());
    private static final String CLIENT_ID = "478375949160-lf7nntvl7hnohvdrt2rjct7miph9n2k3.apps.googleusercontent.com"; // Reemplazar con tu Client ID de Google
    
    private UsuarioServicio usuarioServicio;
    private GoogleIdTokenVerifier verifier;
    
    @Override
    public void init() throws ServletException {
        super.init();
        usuarioServicio = new UsuarioServicio();
        
        // Configurar el verificador de tokens de Google
        verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
            .setAudience(Collections.singletonList(CLIENT_ID))
            .build();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/plain;charset=UTF-8");
        
        String idToken = request.getParameter("idToken");
        if (idToken == null || idToken.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Token de ID no proporcionado");
            return;
        }
        
        try {
            // Verificar el token de ID
            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token de ID inválido");
                return;
            }
            
            // Obtener información del usuario
            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            String email = payload.getEmail();
            
            // Verificar que el correo esté verificado
            if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("El correo electrónico de Google no está verificado");
                return;
            }
            
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");
            
            // Verificar si el usuario ya existe
            UsuarioDto usuarioExistente = usuarioServicio.buscarPorCorreo(email);
            if (usuarioExistente != null) {
                if (usuarioExistente.isGoogle()) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.getWriter().write("Ya existe una cuenta de Google con este correo. Por favor, inicie sesión.");
                } else {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.getWriter().write("Ya existe una cuenta con este correo que no es de Google.");
                }
                return;
            }
            
            // Crear nuevo usuario
            CrearUsuDto nuevoUsuario = new CrearUsuDto();
            nuevoUsuario.setNombreCompleto(name);
            nuevoUsuario.setCorreoElectronico(email);
            nuevoUsuario.setGoogle(true);
            nuevoUsuario.setCorreoConfirmado(true);
            nuevoUsuario.setRolId(4L); // Rol por defecto = usuario
            
            // Descargar y establecer la foto de perfil
            if (picture != null && !picture.isEmpty()) {
                try {
                    byte[] fotoBytes = descargarFoto(picture);
                    nuevoUsuario.setFoto(fotoBytes);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "No se pudo descargar la foto de perfil: " + e.getMessage());
                }
            }
            
            try {
                // Guardar el usuario
                CrearUsuDto usuarioCreado = usuarioServicio.crearUsuario(nuevoUsuario);
                
                // Buscar el usuario creado para obtener todos sus datos
                UsuarioDto usuarioCompleto = usuarioServicio.buscarPorCorreo(usuarioCreado.getCorreoElectronico());
                
                // Establecer el usuario en la sesión
                HttpSession session = request.getSession();
                session.setAttribute("usuario", usuarioCompleto);
                session.setAttribute("mensaje", "¡Bienvenido! Tu cuenta ha sido creada exitosamente.");
                
                // Enviar respuesta exitosa
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Registro exitoso");
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al crear usuario: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Error al registrar usuario");
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al registrar usuario de Google: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al registrar usuario");
        }
    }
    
    private byte[] descargarFoto(String imageUrl) throws IOException {
        try (InputStream in = new URL(imageUrl).openStream()) {
            return in.readAllBytes();
        }
    }
}