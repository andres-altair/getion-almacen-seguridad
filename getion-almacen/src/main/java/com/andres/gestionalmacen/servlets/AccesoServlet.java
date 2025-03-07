package com.andres.gestionalmacen.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.servicios.UsuarioServicio;
import com.andres.gestionalmacen.utilidades.EncriptarUtil;
import com.andres.gestionalmacen.utilidades.GestorRegistros;

@WebServlet("/acceso")
public class AccesoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) throws ServletException, IOException {
        // Registrar el acceso
        GestorRegistros.sistemaInfo("Acceso a página de acceso");
        
        // Redirigir al JSP
        peticion.getRequestDispatcher("/acceso.jsp").forward(peticion, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta) throws ServletException, IOException {
        String correoElectronico = peticion.getParameter("correoElectronico");
        String contrasena = peticion.getParameter("contrasena");
        boolean recordar = peticion.getParameter("recordar") != null;

        // Log del intento de acceso
        GestorRegistros.sistemaInfo("Intento de acceso para usuario: " + correoElectronico);

        // Hashear la contraseña
        String contrasenaHasheada = EncriptarUtil.contraseñaHash(contrasena);
        
        // Crear una instancia de UsuarioServicio
        UsuarioServicio servicioUsuario = new UsuarioServicio();

        try {
            // Primero verificar si el usuario existe y su método de autenticación
            UsuarioDto usuarioExistente = servicioUsuario.buscarPorCorreo(correoElectronico);
            
            if (usuarioExistente != null && usuarioExistente.isGoogle()) {
                GestorRegistros.sistemaWarning("Intento de acceso con formulario para cuenta de Google: " + correoElectronico);
                peticion.setAttribute("error", "Esta cuenta fue registrada con Google. Por favor, use el botón 'Iniciar sesión con Google'.");
                peticion.setAttribute("correoElectronico", correoElectronico);
                peticion.getRequestDispatcher("/acceso.jsp").forward(peticion, respuesta);
                return;
            }

            // Llamar al servicio para validar las credenciales
            UsuarioDto datosUsuario = servicioUsuario.validarCredenciales(correoElectronico, contrasenaHasheada);
            
            if (datosUsuario != null) {
                // Log de acceso exitoso
                GestorRegistros.info(datosUsuario.getId(), "Acceso exitoso al sistema");
                GestorRegistros.sistemaInfo("Usuario con ID: " + datosUsuario.getId() + " accedió exitosamente. Rol: " + datosUsuario.getRolId());
                
                HttpSession sesion = peticion.getSession();
                sesion.setAttribute("usuario", datosUsuario);

                if (recordar) {
                    Cookie galleta = new Cookie("usuario", correoElectronico);
                    galleta.setMaxAge(60 * 60 * 24 * 30); // 30 días
                    respuesta.addCookie(galleta);
                    GestorRegistros.info(datosUsuario.getId(), "Se ha activado la opción 'recordar usuario'");
                }

                // Redirigir según el rol
                String destino;
                switch (datosUsuario.getRolId().intValue()) {
                    case 1: // Admin
                        destino = "/admin/panel";
                        break;
                    case 2: // Gerente
                        destino = "/gerente/panel";
                        break;
                    case 3: // Operador
                        destino = "/operario/panel";
                        break;
                    case 4: // Usuario
                        destino = "/usuario/panel";
                        break;
                    default:
                        GestorRegistros.warning(datosUsuario.getId(), "Intento de acceso con rol no válido: " + datosUsuario.getRolId());
                        peticion.setAttribute("error", "Rol no válido");
                        peticion.getRequestDispatcher("/acceso.jsp").forward(peticion, respuesta);
                        return;
                }
                GestorRegistros.info(datosUsuario.getId(), "Redirigiendo a: " + destino);
                respuesta.sendRedirect(peticion.getContextPath() + destino);
                
            } else {
                // Log de acceso fallido
                GestorRegistros.sistemaWarning("Intento de acceso fallido para usuario: " + correoElectronico + " - Credenciales incorrectas");
                
                // Establecer mensaje de error y atributos para mantener el email
                peticion.setAttribute("error", "¡Credenciales inválidas! Por favor, verifica tu correo y contraseña.");
                peticion.getRequestDispatcher("/acceso.jsp").forward(peticion, respuesta);
            }
        } catch (Exception error) {
            // Log de error
            GestorRegistros.sistemaError("Error en el proceso de acceso para usuario " + correoElectronico + ": " + error.getMessage());
            
            String mensajeError = error.getMessage();
            if (mensajeError.contains("500")) {
                mensajeError = "Error en el servidor. Por favor, inténtelo más tarde.";
            }
            // Establecer mensaje de error y mantener el email
            peticion.setAttribute("error", mensajeError);
            peticion.setAttribute("correoElectronico", correoElectronico);
            peticion.getRequestDispatcher("/acceso.jsp").forward(peticion, respuesta);
        }
    }
}