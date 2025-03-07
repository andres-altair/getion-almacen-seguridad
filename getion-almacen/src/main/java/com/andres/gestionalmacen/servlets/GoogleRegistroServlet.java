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

@WebServlet("/GoogleRegistroServlet")
public class GoogleRegistroServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(GoogleRegistroServlet.class.getName());
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
            
            LOGGER.info("Intentando registrar usuario con correo: " + correoElectronico);
            
            // Verificar que el correo esté verificado
            if (!Boolean.TRUE.equals(datosToken.getEmailVerified())) {
                LOGGER.warning("Correo no verificado: " + correoElectronico);
                enviarError(respuesta, HttpServletResponse.SC_BAD_REQUEST, "El correo electrónico de Google no está verificado");
                return;
            }
            
            // Verificar si el usuario ya existe
            UsuarioDto usuarioExistente = servicioUsuario.buscarPorCorreo(correoElectronico);
            if (usuarioExistente != null) {
                LOGGER.warning("Usuario ya existe: " + correoElectronico);
                enviarError(respuesta, HttpServletResponse.SC_CONFLICT, "El usuario ya existe");
                return;
            }
            
            String nombreCompleto = (String) datosToken.get("name");
            String urlFoto = (String) datosToken.get("picture");
            
            // Crear DTO para el nuevo usuario
            CrearUsuDto nuevoUsuario = new CrearUsuDto();
            nuevoUsuario.setNombreCompleto(nombreCompleto);
            nuevoUsuario.setCorreoElectronico(correoElectronico);
            nuevoUsuario.setGoogle(true);
            nuevoUsuario.setCorreoConfirmado(true);
            nuevoUsuario.setRolId(4L); // Usuario normal
            
            // Descargar y establecer la foto si está disponible
            if (urlFoto != null && !urlFoto.isEmpty()) {
                try {
                    byte[] bytesFoto = descargarFoto(urlFoto);
                    nuevoUsuario.setFoto(bytesFoto);
                } catch (Exception error) {
                    LOGGER.log(Level.WARNING, "No se pudo descargar la foto de perfil: " + error.getMessage());
                }
            }
            
            try {
                // Crear el usuario
                CrearUsuDto usuarioCreado = servicioUsuario.crearUsuario(nuevoUsuario);
                LOGGER.info("Usuario creado correctamente con ID: " + usuarioCreado.getId());
                
                // Obtener usuario completo
                UsuarioDto usuarioCompleto = servicioUsuario.buscarPorCorreo(correoElectronico);
                
                // Establecer el usuario en la sesión
                HttpSession sesion = peticion.getSession();
                sesion.setAttribute("usuario", usuarioCompleto);
                sesion.setAttribute("mensaje", "¡Bienvenido! Tu cuenta ha sido creada exitosamente.");
                
                // Enviar respuesta exitosa
                respuesta.setStatus(HttpServletResponse.SC_OK);
                respuesta.getWriter().write("Registro exitoso");
                LOGGER.info("Registro completado exitosamente para: " + correoElectronico);
                
            } catch (Exception error) {
                LOGGER.log(Level.SEVERE, "Error al crear usuario: " + error.getMessage());
                enviarError(respuesta, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al crear usuario: " + error.getMessage());
            }
            
        } catch (Exception error) {
            LOGGER.log(Level.SEVERE, "Error al procesar registro con Google: " + error.getMessage());
            enviarError(respuesta, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al procesar registro: " + error.getMessage());
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