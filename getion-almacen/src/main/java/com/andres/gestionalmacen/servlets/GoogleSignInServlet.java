package com.andres.gestionalmacen.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import com.andres.gestionalmacen.servicios.UsuarioServicio;
import com.andres.gestionalmacen.dtos.UsuarioDto;

import java.io.IOException;
import java.util.Collections;

@WebServlet("/google-signin")
public class GoogleSignInServlet extends HttpServlet {
    private static final String CLIENT_ID = "503302730974-grs94tgi74gh28k0a9qh5qv52chp1c8v.apps.googleusercontent.com";
    private final UsuarioServicio usuarioServicio = new UsuarioServicio();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idTokenString = request.getParameter("credential");
        
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                Payload payload = idToken.getPayload();
                
                String email = payload.getEmail();
                boolean emailVerified = payload.getEmailVerified();
                String name = (String) payload.get("name");
                
                if (emailVerified) {
                    UsuarioDto usuario = usuarioServicio.buscarPorCorreo(email);
                    
                    if (usuario == null) {
                        // Crear nuevo usuario si no existe
                        usuario = new UsuarioDto();
                        usuario.setCorreoElectronico(email);
                        usuario.setNombre(name);
                        usuario.setConfirmado(true);
                        // Asignar un rol por defecto
                        usuario.setRol("OPERARIO");
                        
                        usuarioServicio.crear(usuario);
                    }
                    
                    // Iniciar sesión
                    HttpSession session = request.getSession();
                    session.setAttribute("usuario", usuario);
                    session.setAttribute("rol", usuario.getRol());
                    
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\": true}");
                } else {
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\": false, \"error\": \"Email no verificado\"}");
                }
            } else {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"error\": \"Token inválido\"}");
            }
        } catch (Exception e) {
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
        }
    }
}