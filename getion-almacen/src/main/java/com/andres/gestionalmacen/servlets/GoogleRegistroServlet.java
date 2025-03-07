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
 * Servlet que maneja el registro de nuevos usuarios mediante Google Sign-In.
 * Verifica tokens de Google, crea nuevos usuarios y gestiona la información del perfil.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Verificación de tokens de Google</li>
 *   <li>Validación de correos verificados</li>
 *   <li>Creación de nuevos usuarios de Google</li>
 *   <li>Gestión de fotos de perfil</li>
 *   <li>Manejo de sesiones iniciales</li>
 *   <li>Registro detallado de actividades</li>
 * </ul>
 * 
 * <p>Según [875eb101-5aa8-4067-87e7-39617e3a474a], este servlet establece
 * el campo 'google' como true para los nuevos usuarios.</p>
 * 
 * <p>Según [35176471-70ce-4b89-92e3-77ccfc940534], utiliza buscarPorCorreo
 * de manera eficiente para verificar usuarios existentes.</p>
 * 
 * @author Andrés
 * @version 1.0
 */
@WebServlet("/GoogleRegistroServlet")
public class GoogleRegistroServlet extends HttpServlet {
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
        GestorRegistros.sistemaInfo("GoogleRegistroServlet inicializado correctamente");
    }
    
    /**
     * Procesa las solicitudes POST para el registro con Google.
     * Verifica el token, crea el usuario y gestiona la sesión inicial.
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
            GestorRegistros.sistemaWarning("Intento de registro con Google sin token");
            enviarError(respuesta, HttpServletResponse.SC_BAD_REQUEST, "Token de ID no proporcionado");
            return;
        }
        
        try {
            // Verificar el token de ID
            GoogleIdToken tokenGoogle = verificador.verify(tokenId);
            if (tokenGoogle == null) {
                GestorRegistros.sistemaWarning("Token de Google inválido en registro");
                enviarError(respuesta, HttpServletResponse.SC_UNAUTHORIZED, "Token de ID inválido");
                return;
            }
            
            // Obtener información del usuario
            GoogleIdToken.Payload datosToken = tokenGoogle.getPayload();
            String correoElectronico = datosToken.getEmail();
            
            GestorRegistros.sistemaInfo("Intento de registro con Google: " + correoElectronico);
            
            // Verificar que el correo esté verificado
            if (!Boolean.TRUE.equals(datosToken.getEmailVerified())) {
                GestorRegistros.sistemaWarning("Intento de registro con correo no verificado: " + correoElectronico);
                enviarError(respuesta, HttpServletResponse.SC_BAD_REQUEST, "El correo electrónico de Google no está verificado");
                return;
            }
            
            // Verificar si el usuario ya existe
            UsuarioDto usuarioExistente = servicioUsuario.buscarPorCorreo(correoElectronico);
            if (usuarioExistente != null) {
                GestorRegistros.sistemaWarning("Intento de registro con correo existente: " + correoElectronico);
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
                    GestorRegistros.sistemaInfo("Foto de perfil descargada para: " + correoElectronico);
                } catch (Exception error) {
                    GestorRegistros.sistemaWarning("Error al descargar foto de perfil para " + 
                        correoElectronico + ": " + error.getMessage());
                }
            }
            
            try {
                // Crear el usuario
                CrearUsuDto usuarioCreado = servicioUsuario.crearUsuario(nuevoUsuario);
                GestorRegistros.sistemaInfo("Usuario Google creado - ID: " + usuarioCreado.getId() + 
                    ", Correo: " + correoElectronico);
                
                // Obtener usuario completo
                UsuarioDto usuarioCompleto = servicioUsuario.buscarPorCorreo(correoElectronico);
                
                // Establecer el usuario en la sesión
                HttpSession sesion = peticion.getSession();
                sesion.setAttribute("usuario", usuarioCompleto);
                sesion.setAttribute("mensaje", "¡Bienvenido! Tu cuenta ha sido creada exitosamente.");
                
                GestorRegistros.info(usuarioCreado.getId(), "Registro y sesión inicial completados");
                
                // Enviar respuesta exitosa
                respuesta.setStatus(HttpServletResponse.SC_OK);
                respuesta.getWriter().write("Registro exitoso");
                
            } catch (Exception error) {
                GestorRegistros.sistemaError("Error al crear usuario Google: " + error.getMessage());
                enviarError(respuesta, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error al crear usuario: " + error.getMessage());
            }
            
        } catch (Exception error) {
            GestorRegistros.sistemaError("Error en proceso de registro Google: " + error.getMessage());
            enviarError(respuesta, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error al procesar registro: " + error.getMessage());
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