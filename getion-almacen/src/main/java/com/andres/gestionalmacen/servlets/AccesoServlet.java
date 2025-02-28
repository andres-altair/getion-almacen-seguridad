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

        System.out.println("Intento de login para: " + correoElectronico);

        // Hashear la contraseña
        String contrasenaHasheada = EncriptarUtil.hashPassword(contrasena);
        System.out.println("Contraseña hasheada: " + contrasenaHasheada);
        
        // Crear una instancia de UsuarioServicio
        UsuarioServicio usuarioServicio = new UsuarioServicio();

        try {
            // Llamar al servicio para validar las credenciales
            UsuarioDto usuarioDto = usuarioServicio.validarCredenciales(correoElectronico, contrasenaHasheada);
            
            if (usuarioDto != null) {
                System.out.println("Login exitoso para: " + correoElectronico);
                System.out.println("Rol del usuario: " + usuarioDto.getRolId());
                
                HttpSession session = request.getSession();
                session.setAttribute("usuario", usuarioDto);

                if (recordar) {
                    Cookie cookie = new Cookie("usuario", correoElectronico);
                    cookie.setMaxAge(60 * 60 * 24 * 30); // 30 días
                    response.addCookie(cookie);
                }

                // Redirigir según el rol
                switch (usuarioDto.getRolId().intValue()) {
                    case 1: // Admin
                        response.sendRedirect(request.getContextPath() + "/admin/panel");
                        break;
                    case 2: // Gerente
                        response.sendRedirect(request.getContextPath() + "/gerente/panel");
                        break;
                    case 3: // Operador
                        response.sendRedirect(request.getContextPath() + "/operario/panel");
                        break;
                    default:
                        System.out.println("Rol no válido: " + usuarioDto.getRolId());
                        request.setAttribute("error", "Rol no válido");
                        request.getRequestDispatcher("/acceso.jsp").forward(request, response);
                }
            } else {
                System.out.println("Login fallido: usuario no encontrado");
                request.setAttribute("error", "Credenciales incorrectas");
                request.getRequestDispatcher("/acceso.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.err.println("Error en login: " + e.getMessage());
            e.printStackTrace();
            String errorMessage = e.getMessage();
            if (errorMessage.contains("500")) {
                errorMessage = "Error en el servidor. Por favor, inténtelo más tarde.";
            }
            request.setAttribute("error", errorMessage);
            request.getRequestDispatcher("/acceso.jsp").forward(request, response);
        }
    }
}