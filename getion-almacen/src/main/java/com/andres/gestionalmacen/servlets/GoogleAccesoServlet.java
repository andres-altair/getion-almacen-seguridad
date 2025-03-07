package com.andres.gestionalmacen.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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
    private static final String ID_CLIENTE = "478375949160-lf7nntvl7hnohvdrt2rjct7miph9n2k3.apps.googleusercontent.com";
    
    private UsuarioServicio servicioUsuario;
    private GoogleIdTokenVerifier verificador;
    
    @Override
    public void init() throws ServletException {
        super.init();
        servicioUsuario = new UsuarioServicio();
        verificador = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
            .setAudience(Collections.singletonList(ID_CLIENTE))
            .build();
    }
    
    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        
        respuesta.setContentType("text/plain;charset=UTF-8");
        
        String tokenId = peticion.getParameter("idToken");
        if (tokenId == null || tokenId.isEmpty()) {
            LOGGER.warning("Token de ID no proporcionado");
            enviarError(respuesta, HttpServletResponse.SC_BAD_REQUEST, "Token de ID no proporcionado");
            return;
        }
        
        try {
            // Verificar el token de ID
            GoogleIdToken tokenGoogle = verificador.verify(tokenId);
            if (tokenGoogle == null) {
                LOGGER.warning("Token de ID inválido");
                enviarError(respuesta, HttpServletResponse.SC_UNAUTHORIZED, "Token de ID inválido");
                return;
            }
            
            // Obtener información del usuario
            GoogleIdToken.Payload datosToken = tokenGoogle.getPayload();
            String correoElectronico = datosToken.getEmail();
            
            LOGGER.info("Intentando acceder con correo: " + correoElectronico);
            
            // Verificar que el correo esté verificado
            if (!Boolean.TRUE.equals(datosToken.getEmailVerified())) {
                LOGGER.warning("Correo no verificado: " + correoElectronico);
                enviarError(respuesta, HttpServletResponse.SC_BAD_REQUEST, "El correo electrónico de Google no está verificado");
                return;
            }
            
            String nombreCompleto = (String) datosToken.get("name");
            String urlFoto = (String) datosToken.get("picture");
            
            // Verificar si el usuario existe
            UsuarioDto usuario = servicioUsuario.buscarPorCorreo(correoElectronico);
            LOGGER.info("Resultado búsqueda usuario: " + (usuario != null ? "encontrado" : "no encontrado"));
            
            if (usuario == null) {
                LOGGER.warning("Usuario no encontrado para correo: " + correoElectronico);
                enviarError(respuesta, HttpServletResponse.SC_NOT_FOUND, "Usuario no encontrado");
                return;
            }
            
            LOGGER.info("Estado usuario - ID: " + usuario.getId() + 
                       ", Google: " + usuario.isGoogle());
            
            // Verificar que sea un usuario de Google
            if (!usuario.isGoogle()) {
                LOGGER.warning("Usuario no es de Google: " + correoElectronico);
                enviarError(respuesta, HttpServletResponse.SC_BAD_REQUEST, 
                    "Esta cuenta no fue creada con Google. Por favor, inicie sesión con su correo y contraseña.");
                return;
            }
            
            // Crear DTO para actualización
            CrearUsuDto usuarioActualizar = new CrearUsuDto();
            usuarioActualizar.setId(usuario.getId());
            usuarioActualizar.setNombreCompleto(nombreCompleto);
            usuarioActualizar.setCorreoElectronico(correoElectronico);
            usuarioActualizar.setGoogle(true);
            usuarioActualizar.setCorreoConfirmado(true);
            usuarioActualizar.setRolId(usuario.getRolId());
            usuarioActualizar.setMovil(usuario.getMovil());
            
            // Actualizar foto si hay una nueva
            if (urlFoto != null && !urlFoto.isEmpty()) {
                try {
                    byte[] bytesFoto = descargarFoto(urlFoto);
                    usuarioActualizar.setFoto(bytesFoto);
                } catch (Exception error) {
                    LOGGER.log(Level.WARNING, "No se pudo actualizar la foto de perfil: " + error.getMessage());
                }
            }
            
            try {
                // Actualizar usuario
                CrearUsuDto usuarioActualizado = servicioUsuario.actualizarUsuario(usuario.getId(), usuarioActualizar);
                LOGGER.info("Usuario actualizado correctamente");
                
                // Obtener usuario actualizado completo
                UsuarioDto usuarioCompleto = servicioUsuario.buscarPorCorreo(correoElectronico);
                LOGGER.info("Usuario recuperado después de actualización");
                
                // Establecer el usuario en la sesión
                HttpSession sesion = peticion.getSession();
                sesion.setAttribute("usuario", usuarioCompleto);
                sesion.setAttribute("mensaje", "¡Bienvenido de nuevo!");
                LOGGER.info("Sesión establecida correctamente");
                
                // Enviar respuesta exitosa
                respuesta.setStatus(HttpServletResponse.SC_OK);
                respuesta.getWriter().write("Inicio de sesión exitoso");
                LOGGER.info("Respuesta de éxito enviada");
                
            } catch (Exception error) {
                LOGGER.log(Level.SEVERE, "Error al actualizar usuario: " + error.getMessage());
                enviarError(respuesta, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al iniciar sesión: " + error.getMessage());
            }
            
        } catch (Exception error) {
            LOGGER.log(Level.SEVERE, "Error al procesar inicio de sesión con Google: " + error.getMessage());
            enviarError(respuesta, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al iniciar sesión: " + error.getMessage());
        }
    }
    
    private void enviarError(HttpServletResponse respuesta, int estado, String mensaje) throws IOException {
        respuesta.setStatus(estado);
        respuesta.getWriter().write(mensaje);
    }
    
    private byte[] descargarFoto(String urlImagen) throws IOException {
        try (InputStream entrada = URI.create(urlImagen).toURL().openStream()) {
            return entrada.readAllBytes();
        }
    }
}
