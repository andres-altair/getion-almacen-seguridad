package com.andres.gestionalmacen.servlets.usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.utilidades.GestorRegistros;
import com.andres.gestionalmacen.utilidades.ImagenUtil;

@WebServlet("/usuario/panel")

public class PanelUsuarioServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        HttpSession sesion = peticion.getSession(false);
        
        if (sesion == null || sesion.getAttribute("usuario") == null) {
            GestorRegistros.sistemaWarning("Intento de acceso al panel de operario sin sesión válida desde IP: " 
                + peticion.getRemoteAddr());
            respuesta.sendRedirect(peticion.getContextPath() + "/acceso");
            return;
        }

        UsuarioDto usuario = (UsuarioDto) sesion.getAttribute("usuario");
        
        if (usuario.getRolId() != 4) { // Verificar si es usuario
            GestorRegistros.warning(usuario.getId(), 
                "Intento no autorizado de acceso al panel de usuario. Rol actual: " + usuario.getRolId());
            respuesta.sendRedirect(peticion.getContextPath() + "/acceso");
            return;
        }

        try {
            // Preparar los datos de manera segura
            String nombreCompleto = usuario.getNombreCompleto();
            String fotoBase64 = null;
            
            // Procesar la foto con MIME type
            if (usuario.getFoto() != null) {
                byte[] fotoConMime = ImagenUtil.asegurarMimeTypeImagen(usuario.getFoto());
                if (fotoConMime != null) {
                    fotoBase64 = new String(fotoConMime, StandardCharsets.UTF_8);
                } else {
                    GestorRegistros.warning(usuario.getId(), "No se pudo procesar la foto de perfil");
                }
            }
            
            // Establecer atributos con valores por defecto si son null
            peticion.setAttribute("usuarioNombre", nombreCompleto != null ? nombreCompleto : "Usuario");
            peticion.setAttribute("usuarioFoto", fotoBase64 != null ? fotoBase64 : "https://via.placeholder.com/32");
            
            GestorRegistros.info(usuario.getId(), "Panel de usuario cargado exitosamente");
            peticion.getRequestDispatcher("/usuario/panelUsuario.jsp").forward(peticion, respuesta);
            
        } catch (Exception e) {
            GestorRegistros.error(usuario.getId(), 
                "Error al cargar el panel de usuario: " + e.getMessage());
            respuesta.sendRedirect(peticion.getContextPath() + "/acceso");
        }
    }
}
