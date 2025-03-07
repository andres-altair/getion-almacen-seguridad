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
import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.servicios.UsuarioServicio;
import com.andres.gestionalmacen.utilidades.EmailUtil;
import com.andres.gestionalmacen.utilidades.EncriptarUtil;
import com.andres.gestionalmacen.utilidades.GestorRegistros;
import com.andres.gestionalmacen.utilidades.ImagenUtil;

/**
 * Servlet que maneja el registro de nuevos usuarios.
 * Este servlet procesa las solicitudes de registro, verifica la existencia
 * de usuarios y envía correos de confirmación.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Verificación de existencia de usuario</li>
 *   <li>Registro de nuevos usuarios</li>
 *   <li>Envío de correos de confirmación</li>
 *   <li>Procesamiento de imágenes de perfil</li>
 *   <li>Registro detallado de actividades</li>
 * </ul>
 * 
 * <p>Según [875eb101-5aa8-4067-87e7-39617e3a474a], este servlet verifica
 * si el usuario ya existe antes de proceder con el registro.</p>
 * 
 * <p>Según [35176471-70ce-4b89-92e3-77ccfc940534], utiliza buscarPorCorreo
 * de manera eficiente para verificar usuarios.</p>
 * 
 * @author Andrés
 * @version 1.0
 */
@WebServlet("/registro")
@MultipartConfig
public class RegistroServlet extends HttpServlet {
    private final UsuarioServicio servicioUsuario;
    
    public RegistroServlet() {
        this.servicioUsuario = new UsuarioServicio();
    }
    
    /**
     * Maneja las peticiones GET mostrando el formulario de registro.
     * 
     * @param peticion La petición HTTP del cliente
     * @param respuesta La respuesta HTTP al cliente
     * @throws ServletException Si ocurre un error en el servlet
     * @throws IOException Si ocurre un error de E/S
     */
    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        GestorRegistros.sistemaInfo("Acceso a página de registro");
        peticion.getRequestDispatcher("/registro.jsp").forward(peticion, respuesta);
    }

    /**
     * Procesa las solicitudes de registro de nuevos usuarios.
     * 
     * @param peticion La petición HTTP que contiene los datos del formulario
     * @param respuesta La respuesta HTTP al cliente
     * @throws ServletException Si ocurre un error en el servlet
     * @throws IOException Si ocurre un error de E/S
     */
    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        try {
            // Obtener datos del formulario
            String nombreCompleto = peticion.getParameter("nombreCompleto");
            String correoElectronico = peticion.getParameter("correoElectronico");
            String movil = peticion.getParameter("movil");
            String contrasena = peticion.getParameter("contrasena");
            Long rolId = 4L; // Usuario normal

            // Verificar si el usuario ya existe
            UsuarioDto correoExistente = servicioUsuario.buscarPorCorreo(correoElectronico);
            if (correoExistente == null) {
                // El correo no existe, podemos continuar con el registro
                String contrasenaEncriptada = EncriptarUtil.contraseñaHash(contrasena);

                // Procesar la foto si existe
                Part parteFoto = peticion.getPart("foto");
                byte[] bytesFoto = null;
                if (parteFoto != null && parteFoto.getSize() > 0) {
                    bytesFoto = parteFoto.getInputStream().readAllBytes();
                    try {
                        String nombreArchivo = parteFoto.getSubmittedFileName();
                        ImagenUtil.verificarImagen(bytesFoto, nombreArchivo);
                    } catch (IllegalArgumentException error) {
                        GestorRegistros.sistemaWarning("Error al procesar foto para usuario " + correoElectronico + ": " + error.getMessage());
                        peticion.getSession().setAttribute("error", 
                            "La imagen debe tener un formato válido (JPEG, PNG, GIF, BMP, WEBP) y la extensión debe coincidir con el tipo de archivo.");
                        respuesta.sendRedirect(peticion.getContextPath() + "/registro");
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
                nuevoUsuario.setFoto(bytesFoto);
                nuevoUsuario.setCorreoConfirmado(false); // Por defecto, no está confirmado
                nuevoUsuario.setGoogle(false); // Los usuarios registrados no son de Google

                // Guardar el usuario
                servicioUsuario.crearUsuario(nuevoUsuario);

                // Generar y enviar confirmación por correo
                String tokenConfirmacion = EmailUtil.generarToken(correoElectronico);
                EmailUtil.enviarCorreoConfirmacion(correoElectronico, tokenConfirmacion);
                
                // Redirigir con mensaje de éxito
                peticion.getSession().setAttribute("mensaje", 
                    "Te hemos enviado un correo de confirmación. Por favor, revisa tu bandeja de entrada.");
                respuesta.sendRedirect(peticion.getContextPath() + "/acceso");
            } else {
                // El correo ya existe, mostrar error y detener el proceso
                GestorRegistros.sistemaWarning("Intento de registro con correo existente: " + correoElectronico);
                peticion.getSession().setAttribute("error", "Ya existe una cuenta con ese correo electrónico.");
                // Mantener los datos del formulario excepto la contraseña
                peticion.getSession().setAttribute("nombreCompleto", nombreCompleto);
                peticion.getSession().setAttribute("correoElectronico", correoElectronico);
                peticion.getSession().setAttribute("movil", movil);
                respuesta.sendRedirect(peticion.getContextPath() + "/registro");
                return;
            }

        } catch (Exception error) {
            GestorRegistros.sistemaError("Error en el registro de usuario - Error: " + error.getMessage());
            peticion.getSession().setAttribute("error", "Error en el registro: " + error.getMessage());
            respuesta.sendRedirect(peticion.getContextPath() + "/registro");
        }
    }
}
