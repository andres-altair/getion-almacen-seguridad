package com.andres.gestionalmacen.servlets.gerente;

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

@WebServlet("/gerente/panel")
public class PanelGerenteServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        HttpSession sesion = peticion.getSession(false);
        System.out.println("PanelGerenteServlet - Session exists: " + (sesion != null));
        
        if (sesion != null && sesion.getAttribute("usuario") != null) {
            System.out.println("PanelGerenteServlet - Usuario exists in session");
            UsuarioDto usuario = (UsuarioDto) sesion.getAttribute("usuario");
            System.out.println("PanelGerenteServlet - Usuario nombre: " + usuario.getNombreCompleto());
            
            if (usuario.getRolId() == 2) { // Verificar si es gerente
                System.out.println("PanelGerenteServlet - Usuario es gerente");
                
                try {
                    // Preparar los datos de manera segura
                    String nombreCompleto = usuario.getNombreCompleto();
                    String fotoBase64 = null;
                    
                    // Procesar la foto con MIME type
                    if (usuario.getFoto() != null) {
                        byte[] fotoConMime = ImagenUtil.asegurarMimeTypeImagen(usuario.getFoto());
                        if (fotoConMime != null) {
                            fotoBase64 = new String(fotoConMime, StandardCharsets.UTF_8);
                        }
                    }
                    
                    System.out.println("PanelGerenteServlet - Nombre completo: " + nombreCompleto);
                    System.out.println("PanelGerenteServlet - Foto convertida: " + (fotoBase64 != null));
                    
                    // Establecer atributos con valores por defecto si son null
                    peticion.setAttribute("usuarioNombre", nombreCompleto != null ? nombreCompleto : "Usuario");
                    peticion.setAttribute("usuarioFoto", fotoBase64 != null ? fotoBase64 : "https://via.placeholder.com/32");
                    
                    System.out.println("PanelGerenteServlet - Atributos establecidos correctamente");
                    peticion.getRequestDispatcher("/gerente/panelGerente.jsp").forward(peticion, respuesta);
                } catch (Exception e) {
                    System.out.println("Error al procesar datos de usuario: " + e.getMessage());
                    e.printStackTrace();
                    respuesta.sendRedirect(peticion.getContextPath() + "/acceso.jsp");
                }
            } else {
                System.out.println("PanelGerenteServlet - Usuario no es gerente");
                respuesta.sendRedirect(peticion.getContextPath() + "/acceso.jsp");
            }
        } else {
            System.out.println("PanelGerenteServlet - No hay sesión activa");
            respuesta.sendRedirect(peticion.getContextPath() + "/acceso.jsp");
        }
    }
}
