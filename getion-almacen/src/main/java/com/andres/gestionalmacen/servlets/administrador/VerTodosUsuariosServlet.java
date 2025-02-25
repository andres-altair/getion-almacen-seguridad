package com.andres.gestionalmacen.servlets.administrador;

import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.servicios.UsuarioServicio;
import com.andres.gestionalmacen.utilidades.ImagenUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/usuarios")
public class VerTodosUsuariosServlet extends HttpServlet {

    private final UsuarioServicio usuarioServicio;

    public VerTodosUsuariosServlet() {
        this.usuarioServicio = new UsuarioServicio();
    }

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) throws ServletException, IOException {
        try {

             // Verificar sesión y rol (código existente)
            HttpSession session = peticion.getSession(false);
            System.out.println("Sesión: " + (session != null ? "existe" : "no existe"));
            
            if (session == null || session.getAttribute("usuario") == null) {
                System.out.println("GestionUsuariosServlet.doGet - No hay sesión activa o usuario no encontrado en sesión");
                respuesta.sendRedirect(peticion.getContextPath() + "/acceso.jsp");
                return;
            }
            
            UsuarioDto usuarioActual = (UsuarioDto) session.getAttribute("usuario");
            System.out.println("Usuario en sesión: " + usuarioActual.getNombreCompleto());
            System.out.println("Rol del usuario: " + usuarioActual.getRolId());
            
            if (usuarioActual.getRolId() != 1) {
                System.out.println("GestionUsuariosServlet.doGet - Usuario no es admin: " + usuarioActual.getRolId());
                respuesta.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
                return;
            }
            // Obtener y mostrar lista de usuarios
            List<UsuarioDto> usuarios = usuarioServicio.obtenerUsuarios();
            if (usuarios != null) {
                // Crear un mapa para almacenar las fotos convertidas
                Map<Long, String> fotosBase64 = new HashMap<>();
                
                for (UsuarioDto user : usuarios) {
                    if (user.getFoto() != null) {
                        byte[] fotoConMime = ImagenUtil.asegurarMimeTypeImagen(user.getFoto());
                        if (fotoConMime != null) {
                            String fotoBase64 = new String(fotoConMime, StandardCharsets.UTF_8);
                            fotosBase64.put(user.getId(), fotoBase64);
                        }
                    }
                }
                peticion.setAttribute("fotosBase64", fotosBase64);
                peticion.setAttribute("usuarios", usuarios);
            }

            peticion.getRequestDispatcher("/admin/gestionUsuarios.jsp").forward(peticion, respuesta);

        } catch (Exception e) {
            e.printStackTrace();
            peticion.setAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
            peticion.getRequestDispatcher("/admin/gestionUsuarios.jsp").forward(peticion, respuesta);
        }
    }
}