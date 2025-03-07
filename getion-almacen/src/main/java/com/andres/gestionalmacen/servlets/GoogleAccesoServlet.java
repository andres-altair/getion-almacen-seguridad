package com.andres.gestionalmacen.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.andres.gestionalmacen.dtos.CrearUsuDto;
import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.servicios.UsuarioServicio;
import com.andres.gestionalmacen.utilidades.GestorRegistros;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

/**
 * Servlet que maneja la autenticación de usuarios mediante Google Sign-In.
 * Verifica tokens de Google, gestiona usuarios existentes y nuevos, y mantiene
 * la información de perfil actualizada.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Verificación de tokens de Google</li>
 *   <li>Validación de correos verificados</li>
 *   <li>Gestión de usuarios de Google</li>
 *   <li>Actualización de información de perfil</li>
 *   <li>Manejo de sesiones</li>
 *   <li>Registro detallado de actividades</li>
 * </ul>
 * 
 * <p>Según [875eb101-5aa8-4067-87e7-39617e3a474a], este servlet maneja el campo
 * 'google' para distinguir entre métodos de autenticación.</p>
 * 
 * @author Andrés
 * @version 1.0
 */
@WebServlet("/GoogleAccesoServlet")
public class GoogleAccesoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String ID_CLIENTE = "478375949160-lf7nntvl7hnohvdrt2rjct7miph9n2k3.apps.googleusercontent.com";
    
    private UsuarioServicio servicioUsuario;
    private GoogleIdTokenVerifier verificador;
    
    /**
     * Inicializa el servlet configurando el servicio de usuario y el verificador de Google.
     * 
     * @throws ServletException Si ocurre un error durante la inicialización
     */
    @Override
    public void init() throws ServletException {
        super.init();
        servicioUsuario = new UsuarioServicio();
        verificador = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
            .setAudience(Collections.singletonList(ID_CLIENTE))
            .build();
        GestorRegistros.sistemaInfo("GoogleAccesoServlet inicializado correctamente");
    }
    
    /**
     * Procesa las solicitudes POST para la autenticación con Google.
     * Verifica el token, valida el usuario y gestiona la sesión.
     * 
     * @param peticion La petición HTTP que contiene el token de Google
     * @param respuesta La respuesta HTTP al cliente
     * @throws ServletException Si ocurre un error en el servlet
     * @throws IOException Si ocurre un error de E/S
     */
    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        
        respuesta.setContentType("text/plain;charset=UTF-8");
        
        String tokenId = peticion.getParameter("idToken");
        if (tokenId == null || tokenId.isEmpty()) {
            GestorRegistros.sistemaWarning("Intento de acceso con Google sin token");
            enviarError(respuesta, HttpServletResponse.SC_BAD_REQUEST, "Token de ID no proporcionado");
            return;
        }
        
        try {
            // Verificar el token de ID
            GoogleIdToken tokenGoogle = verificador.verify(tokenId);
            if (tokenGoogle == null) {
                GestorRegistros.sistemaWarning("Token de Google inválido");
                enviarError(respuesta, HttpServletResponse.SC_UNAUTHORIZED, "Token de ID inválido");
                return;
            }
            
            // Obtener información del usuario
            GoogleIdToken.Payload datosToken = tokenGoogle.getPayload();
            String correoElectronico = datosToken.getEmail();
            
            GestorRegistros.sistemaInfo("Intento de acceso con Google: " + correoElectronico);
            
            // Verificar que el correo esté verificado
            if (!Boolean.TRUE.equals(datosToken.getEmailVerified())) {
                GestorRegistros.sistemaWarning("Correo de Google no verificado: " + correoElectronico);
                enviarError(respuesta, HttpServletResponse.SC_BAD_REQUEST, "El correo electrónico de Google no está verificado");
                return;
            }
            
            String nombreCompleto = (String) datosToken.get("name");
            String urlFoto = (String) datosToken.get("picture");
            
            // Verificar si el usuario existe
            UsuarioDto usuario = servicioUsuario.buscarPorCorreo(correoElectronico);
            
            if (usuario == null) {
                GestorRegistros.sistemaWarning("Usuario de Google no encontrado: " + correoElectronico);
                enviarError(respuesta, HttpServletResponse.SC_NOT_FOUND, "Usuario no encontrado");
                return;
            }
            
            GestorRegistros.sistemaInfo("Usuario de Google encontrado - ID: " + usuario.getId());
            
            // Verificar que sea un usuario de Google
            if (!usuario.isGoogle()) {
                GestorRegistros.sistemaWarning("Intento de acceso Google para cuenta normal: " + correoElectronico);
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
                    GestorRegistros.info(usuario.getId(), "Foto de perfil actualizada");
                } catch (Exception error) {
                    GestorRegistros.warning(usuario.getId(), "Error al actualizar foto de perfil: " + error.getMessage());
                }
            }
            
            try {
                // Actualizar usuario
                CrearUsuDto usuarioActualizado = servicioUsuario.actualizarUsuario(usuario.getId(), usuarioActualizar);
                GestorRegistros.info(usuario.getId(), "Perfil actualizado correctamente");
                
                // Obtener usuario actualizado completo
                UsuarioDto usuarioCompleto = servicioUsuario.buscarPorCorreo(correoElectronico);
                
                // Establecer el usuario en la sesión
                HttpSession sesion = peticion.getSession();
                sesion.setAttribute("usuario", usuarioCompleto);
                sesion.setAttribute("mensaje", "¡Bienvenido de nuevo!");
                GestorRegistros.info(usuario.getId(), "Sesión iniciada correctamente");
                
                // Enviar respuesta exitosa
                respuesta.setStatus(HttpServletResponse.SC_OK);
                respuesta.getWriter().write("Inicio de sesión exitoso");
                
            } catch (Exception error) {
                GestorRegistros.error(usuario.getId(), "Error al actualizar perfil: " + error.getMessage());
                enviarError(respuesta, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al iniciar sesión: " + error.getMessage());
            }
            
        } catch (Exception error) {
            GestorRegistros.sistemaError("Error en autenticación Google: " + error.getMessage());
            enviarError(respuesta, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al iniciar sesión: " + error.getMessage());
        }
    }
    
    /**
     * Envía una respuesta de error al cliente.
     * 
     * @param respuesta La respuesta HTTP
     * @param estado El código de estado HTTP
     * @param mensaje El mensaje de error
     * @throws IOException Si ocurre un error al escribir la respuesta
     */
    private void enviarError(HttpServletResponse respuesta, int estado, String mensaje) throws IOException {
        respuesta.setStatus(estado);
        respuesta.getWriter().write(mensaje);
    }
    
    /**
     * Descarga una imagen desde una URL.
     * 
     * @param urlImagen La URL de la imagen
     * @return Los bytes de la imagen
     * @throws IOException Si ocurre un error al descargar la imagen
     */
    private byte[] descargarFoto(String urlImagen) throws IOException {
        try (InputStream entrada = URI.create(urlImagen).toURL().openStream()) {
            return entrada.readAllBytes();
        }
    }
}
