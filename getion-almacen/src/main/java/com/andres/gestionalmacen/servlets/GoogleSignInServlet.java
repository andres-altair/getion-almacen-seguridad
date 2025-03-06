package com.andres.gestionalmacen.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import com.andres.gestionalmacen.servicios.UsuarioServicio;
import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.dtos.CrearUsuDto;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;
import java.net.HttpURLConnection;

@WebServlet("/google-signin")
public class GoogleSignInServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(GoogleSignInServlet.class.getName());
    private static final String CLIENT_ID = "478375949160-lf7nntvl7hnohvdrt2rjct7miph9n2k3.apps.googleusercontent.com";
    private final UsuarioServicio usuarioServicio = new UsuarioServicio();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idTokenString = request.getParameter("credential");
        boolean isRegistration = Boolean.parseBoolean(request.getParameter("isRegistration"));
        response.setContentType("application/json");
        
        if (idTokenString == null || idTokenString.isEmpty()) {
            sendErrorResponse(response, "Token no proporcionado");
            return;
        }

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                sendErrorResponse(response, "Token inválido");
                return;
            }

            Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            boolean emailVerified = payload.getEmailVerified();
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");
            
            if (!emailVerified) {
                sendErrorResponse(response, "Email no verificado");
                return;
            }

            UsuarioDto usuario = usuarioServicio.buscarPorCorreo(email);
            
            if (usuario == null) {
                if (!isRegistration) {
                    sendErrorResponse(response, "Usuario no registrado. Por favor, regístrese primero.");
                    return;
                }
                
                // Crear nuevo usuario usando CrearUsuDto
                CrearUsuDto nuevoUsuario = new CrearUsuDto();
                nuevoUsuario.setNombreCompleto(name);
                nuevoUsuario.setCorreoElectronico(email);
                nuevoUsuario.setRolId(4L); // Rol de operario por defecto
                nuevoUsuario.setGoogle(true); // Marcar como usuario de Google
                nuevoUsuario.setCorreoConfirmado(true); // Correo automáticamente confirmado para usuarios de Google
                
                // Si la foto es una URL, descargarla y convertirla a bytes
                if (picture != null && !picture.isEmpty()) {
                    try {
                        byte[] fotoBytes = descargarFoto(picture);
                        nuevoUsuario.setFoto(fotoBytes);
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "No se pudo descargar la foto de perfil: " + e.getMessage());
                    }
                }
                
                usuarioServicio.crearUsuario(nuevoUsuario);
                LOGGER.info("Nuevo usuario registrado con Google: " + email);
            } else {
                // Verificar si el usuario se registró originalmente con Google
                if (!usuario.isGoogle()) {
                    sendErrorResponse(response, "Esta cuenta fue registrada usando el formulario tradicional. Por favor, inicie sesión con su correo y contraseña.");
                    return;
                }
                
                if (isRegistration) {
                    sendErrorResponse(response, "El correo ya está registrado. Por favor, inicie sesión.");
                    return;
                }
            }
            
            // Iniciar sesión
            HttpSession session = request.getSession();
            session.setAttribute("usuario", usuario);
            
            response.getWriter().write("{\"success\": true, \"redirect\": \"" + 
                request.getContextPath() + "/dashboard\"}");
            
        } catch (GeneralSecurityException e) {
            LOGGER.log(Level.SEVERE, "Error de seguridad al verificar el token", e);
            sendErrorResponse(response, "Error de seguridad al verificar credenciales");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado durante la autenticación", e);
            sendErrorResponse(response, "Error interno del servidor");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }

    private byte[] descargarFoto(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        return conn.getInputStream().readAllBytes();
    }
}