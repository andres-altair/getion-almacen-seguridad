package com.andres.gestionalmacen.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.andres.gestionalmacen.utilidades.GestorRegistros;

@WebServlet("/inicio")
public class InicioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
        super.init();
        GestorRegistros.sistemaInfo("InicioServlet inicializado - Punto de entrada principal");
    }

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        try {
            GestorRegistros.sistemaDebug("Mostrando página de inicio");
            peticion.getRequestDispatcher("/inicio.jsp").forward(peticion, respuesta);
        } catch (Exception e) {
            // Registrar el error detallado en el log
            GestorRegistros.sistemaError("Error al mostrar inicio: " + e.getMessage());
            
            // Enviar mensaje de error al JSP
            peticion.setAttribute("error", "Lo sentimos, ha ocurrido un error. Por favor, inténtelo más tarde.");
            peticion.getRequestDispatcher("/inicio.jsp").forward(peticion, respuesta);
        }
    }
}