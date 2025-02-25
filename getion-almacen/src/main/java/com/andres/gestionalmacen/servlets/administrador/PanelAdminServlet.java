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

@WebServlet("/admin/panel")
public class PanelAdminServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        HttpSession sesion = peticion.getSession(false);
        System.out.println("PanelAdminServlet - Session exists: " + (sesion != null));
        
        if (sesion != null && sesion.getAttribute("usuario") != null) {
            System.out.println("PanelAdminServlet - Usuario exists in session");
            UsuarioDto usuario = (UsuarioDto) sesion.getAttribute("usuario");
            System.out.println("PanelAdminServlet - Usuario nombre: " + usuario.getNombreCompleto());
            
            if (usuario.getRolId() == 1) { // Verificar si es admin
                System.out.println("PanelAdminServlet - Usuario es admin");
                
                try {
                    // Preparar los datos de manera segura
                    String nombreCompleto = usuario.getNombreCompleto();
                    String fotoBase64 = null;
                    
                    // Procesar la foto con MIME type
                    if (usuario.getFoto() != null) {
                        byte[] fotoConMime = ImagenUtil.asegurarMimeTypeImagen(usuario.getFoto());
                        if (fotoConMime != null) {
                            String fotoStr = new String(fotoConMime, StandardCharsets.UTF_8);
                            System.out.println("PanelAdminServlet - Foto con MIME: " + fotoStr.substring(0, Math.min(fotoStr.length(), 10)) + "...");
                            fotoBase64 = fotoStr;
                        }
                    }
                    
                    
                    // Establecer atributos con valores por defecto si son null
                    peticion.setAttribute("usuarioNombre", nombreCompleto != null ? nombreCompleto : "Usuario");
                    String fotoSrc = fotoBase64 != null ? fotoBase64 : "https://via.placeholder.com/32";
                    System.out.println("PanelAdminServlet - Foto src: " + fotoSrc);
                    peticion.setAttribute("usuarioFoto", fotoSrc);
                    
                    System.out.println("PanelAdminServlet - Atributos establecidos correctamente");
                    peticion.getRequestDispatcher("/admin/panelAdmin.jsp").forward(peticion, respuesta);
                } catch (Exception e) {
                    System.out.println("Error al procesar datos de usuario: " + e.getMessage());
                    e.printStackTrace();
                    respuesta.sendRedirect("../acceso.jsp");
                }
            } else {
                respuesta.sendRedirect("../acceso.jsp");
            }
        } else {
            respuesta.sendRedirect("../acceso.jsp");
        }
    }
}