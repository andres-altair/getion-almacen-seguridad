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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Registrar el acceso
        GestorRegistros.sistemaInfo("Acceso a página de acceso");
        
        // Redirigir al JSP
        request.getRequestDispatcher("/acceso.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String correoElectronico = request.getParameter("correoElectronico");
        String contrasena = request.getParameter("contrasena");
        boolean recordar = request.getParameter("recordar") != null;

        // Log del intento de acceso
        GestorRegistros.sistemaInfo("Intento de acceso para usuario: " + correoElectronico);

        // Hashear la contraseña
        String contrasenaHasheada = EncriptarUtil.hashPassword(contrasena);
        
        // Crear una instancia de UsuarioServicio
        UsuarioServicio usuarioServicio = new UsuarioServicio();

        try {
            // Primero verificar si el usuario existe y su método de autenticación
            UsuarioDto usuarioExistente = usuarioServicio.buscarPorCorreo(correoElectronico);
            
            if (usuarioExistente != null && usuarioExistente.isGoogle()) {
                GestorRegistros.sistemaWarning("Intento de acceso con formulario para cuenta de Google: " + correoElectronico);
                request.setAttribute("error", "Esta cuenta fue registrada con Google. Por favor, use el botón 'Iniciar sesión con Google'.");
                request.setAttribute("correoElectronico", correoElectronico);
                request.getRequestDispatcher("/acceso.jsp").forward(request, response);
                return;
            }

            // Llamar al servicio para validar las credenciales
            UsuarioDto usuarioDto = usuarioServicio.validarCredenciales(correoElectronico, contrasenaHasheada);
            
            if (usuarioDto != null) {
                // Log de acceso exitoso
                GestorRegistros.info(usuarioDto.getId(), "Acceso exitoso al sistema");
                GestorRegistros.sistemaInfo("Usuario con ID: " + usuarioDto.getId() + " accedió exitosamente. Rol: " + usuarioDto.getRolId());
                
                HttpSession session = request.getSession();
                session.setAttribute("usuario", usuarioDto);

                if (recordar) {
                    Cookie cookie = new Cookie("usuario", correoElectronico);
                    cookie.setMaxAge(60 * 60 * 24 * 30); // 30 días
                    response.addCookie(cookie);
                    GestorRegistros.info(usuarioDto.getId(), "Se ha activado la opción 'recordar usuario'");
                }

                // Redirigir según el rol
                String destino;
                switch (usuarioDto.getRolId().intValue()) {
                    case 1: // Admin
                        destino = "/admin/panel";
                        break;
                    case 2: // Gerente
                        destino = "/gerente/panel";
                        break;
                    case 3: // Operador
                        destino = "/operario/panel";
                        break;
                    default:
                        GestorRegistros.warning(usuarioDto.getId(), "Intento de acceso con rol no válido: " + usuarioDto.getRolId());
                        request.setAttribute("error", "Rol no válido");
                        request.getRequestDispatcher("/acceso.jsp").forward(request, response);
                        return;
                }
                GestorRegistros.info(usuarioDto.getId(), "Redirigiendo a: " + destino);
                response.sendRedirect(request.getContextPath() + destino);
                
            } else {
                // Log de acceso fallido
                GestorRegistros.sistemaWarning("Intento de acceso fallido para usuario: " + correoElectronico + " - Credenciales incorrectas");
                
                // Establecer mensaje de error y atributos para mantener el email
                request.setAttribute("error", "¡Credenciales inválidas! Por favor, verifica tu correo y contraseña.");
                request.getRequestDispatcher("/acceso.jsp").forward(request, response);
            }
        } catch (Exception e) {
            // Log de error
            GestorRegistros.sistemaError("Error en el proceso de acceso para usuario " + correoElectronico + ": " + e.getMessage());
            
            String errorMessage = e.getMessage();
            if (errorMessage.contains("500")) {
                errorMessage = "Error en el servidor. Por favor, inténtelo más tarde.";
            }
            // Establecer mensaje de error y mantener el email
            request.setAttribute("error", errorMessage);
            request.setAttribute("correoElectronico", correoElectronico);
            request.getRequestDispatcher("/acceso.jsp").forward(request, response);
        }
    }
}