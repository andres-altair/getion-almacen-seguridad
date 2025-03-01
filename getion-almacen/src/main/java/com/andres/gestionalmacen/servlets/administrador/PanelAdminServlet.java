package com.andres.gestionalmacen.servlets.administrador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.utilidades.ImagenUtil;
import com.andres.gestionalmacen.utilidades.GestorRegistros;

@WebServlet("/admin/panel")
public class PanelAdminServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        HttpSession sesion = peticion.getSession(false);
        
        if (sesion != null && sesion.getAttribute("usuario") != null) {
            UsuarioDto usuario = (UsuarioDto) sesion.getAttribute("usuario");
            
            if (usuario.getRolId() == 1) { // Verificar si es admin
                GestorRegistros.info(usuario.getId(), "Acceso al panel de administración");
                
                try {
                    // Preparar los datos de manera segura
                    String nombreCompleto = usuario.getNombreCompleto();
                    String fotoBase64 = null;
                    
                    // Procesar la foto si existe
                    if (usuario.getFoto() != null) {
                        byte[] fotoConMime = ImagenUtil.asegurarMimeTypeImagen(usuario.getFoto());
                        if (fotoConMime != null) {
                            String fotoStr = new String(fotoConMime, StandardCharsets.UTF_8);
                            fotoBase64 = fotoStr;
                            GestorRegistros.debug(usuario.getId(), "Foto de perfil procesada correctamente");
                        } else {
                            GestorRegistros.warning(usuario.getId(), "No se pudo procesar la foto de perfil");
                        }
                    }
                    
                    // Establecer atributos con valores por defecto si son null
                    peticion.setAttribute("usuarioNombre", nombreCompleto != null ? nombreCompleto : "Usuario");
                    String fotoSrc = fotoBase64 != null ? fotoBase64 : "https://via.placeholder.com/32";
                    peticion.setAttribute("usuarioFoto", fotoSrc);
                    
                    GestorRegistros.debug(usuario.getId(), "Datos del panel de administración cargados correctamente");
                    peticion.getRequestDispatcher("/admin/panelAdmin.jsp").forward(peticion, respuesta);
                    
                } catch (Exception e) {
                    GestorRegistros.error(usuario.getId(), "Error al cargar el panel de administración: " + e.getMessage());
                    respuesta.sendRedirect(peticion.getContextPath() + "/acceso");
                }
            } else {
                // Intento de acceso no autorizado
                if (usuario.getId() != null) {
                    GestorRegistros.warning(usuario.getId(), 
                        "Intento de acceso no autorizado al panel de administración. Rol actual: " + usuario.getRolId());
                }
                GestorRegistros.sistemaWarning("Intento de acceso no autorizado al panel de administración desde IP: " 
                    + peticion.getRemoteAddr());
                respuesta.sendRedirect(peticion.getContextPath() + "/acceso");
            }
        } else {
            // Intento de acceso sin sesión
            GestorRegistros.sistemaWarning("Intento de acceso al panel de administración sin sesión válida desde IP: " 
                + peticion.getRemoteAddr());
            respuesta.sendRedirect(peticion.getContextPath() + "/acceso");
        }
    }
}