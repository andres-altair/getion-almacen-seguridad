package com.andres.gestionalmacen.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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

@WebServlet("/GoogleAccesoServlet")
public class GoogleAccesoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(GoogleAccesoServlet.class.getName());
    private static final String CLIENT_ID = "478375949160-lf7nntvl7hnohvdrt2rjct7miph9n2k3.apps.googleusercontent.com";
    
    private UsuarioServicio usuarioServicio;
    private GoogleIdTokenVerifier verifier;
    
    @Override
    public void init() throws ServletException {
        super.init();
        usuarioServicio = new UsuarioServicio();
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
            LOGGER.warning("Token de ID no proporcionado");
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Token de ID no proporcionado");
            return;
        }
        
        try {
            // Verificar el token de ID
            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                LOGGER.warning("Token de ID inválido");
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token de ID inválido");
                return;
            }
            
            // Obtener información del usuario
            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            String email = payload.getEmail();
            
            LOGGER.info("Intentando acceder con correo: " + email);
            
            // Verificar que el correo esté verificado
            if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
                LOGGER.warning("Correo no verificado: " + email);
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "El correo electrónico de Google no está verificado");
                return;
            }
            
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");
            
            // Verificar si el usuario existe
            UsuarioDto usuario = usuarioServicio.buscarPorCorreo(email);
            LOGGER.info("Resultado búsqueda usuario: " + (usuario != null ? "encontrado" : "no encontrado"));
            
            if (usuario == null) {
                LOGGER.warning("Usuario no encontrado para correo: " + email);
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Usuario no encontrado");
                return;
            }
            
            LOGGER.info("Estado usuario - ID: " + usuario.getId() + 
                       ", Google: " + usuario.isGoogle());
            
            // Verificar que sea un usuario de Google
            if (!usuario.isGoogle()) {
                LOGGER.warning("Usuario no es de Google: " + email);
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Esta cuenta no fue creada con Google. Por favor, inicie sesión con su correo y contraseña.");
                return;
            }
            
            // Crear DTO para actualización
            CrearUsuDto usuarioActualizar = new CrearUsuDto();
            usuarioActualizar.setId(usuario.getId());
            usuarioActualizar.setNombreCompleto(name);
            usuarioActualizar.setCorreoElectronico(email);
            usuarioActualizar.setGoogle(true);
            usuarioActualizar.setCorreoConfirmado(true);
            usuarioActualizar.setRolId(usuario.getRolId());
            usuarioActualizar.setMovil(usuario.getMovil());
            
            // Actualizar foto si hay una nueva
            if (picture != null && !picture.isEmpty()) {
                try {
                    byte[] fotoBytes = descargarFoto(picture);
                    usuarioActualizar.setFoto(fotoBytes);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "No se pudo actualizar la foto de perfil: " + e.getMessage());
                }
            }
            
            try {
                // Actualizar usuario
                CrearUsuDto usuarioActualizado = usuarioServicio.actualizarUsuario(usuario.getId(), usuarioActualizar);
                LOGGER.info("Usuario actualizado correctamente");
                
                // Obtener usuario actualizado completo
                UsuarioDto usuarioCompleto = usuarioServicio.buscarPorCorreo(email);
                LOGGER.info("Usuario recuperado después de actualización");
                
                // Establecer el usuario en la sesión
                HttpSession session = request.getSession();
                session.setAttribute("usuario", usuarioCompleto);
                session.setAttribute("mensaje", "¡Bienvenido de nuevo!");
                LOGGER.info("Sesión establecida correctamente");
                
                // Enviar respuesta exitosa
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Inicio de sesión exitoso");
                LOGGER.info("Respuesta de éxito enviada");
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al actualizar usuario: " + e.getMessage());
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al iniciar sesión: " + e.getMessage());
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al procesar inicio de sesión con Google: " + e.getMessage());
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al iniciar sesión: " + e.getMessage());
        }
    }
    
    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.getWriter().write(message);
    }
    
    private byte[] descargarFoto(String imageUrl) throws IOException {
        try (InputStream in = URI.create(imageUrl).toURL().openStream()) {
            return in.readAllBytes();
        }
    }
}
