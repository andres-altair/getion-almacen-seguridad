package com.andres.gestionalmacen.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.andres.gestionalmacen.utilidades.GestorRegistros;

/**
 * Servlet que maneja la página de inicio de la aplicación.
 * Este servlet es el punto de entrada principal y se encarga de la inicialización
 * del sistema de registro y la redirección a la página de inicio.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Inicialización del sistema de registro</li>
 *   <li>Manejo de la ruta raíz y /inicio</li>
 *   <li>Redirección a la página principal</li>
 *   <li>Registro de accesos al sistema</li>
 * </ul>
 * 
 * <p>Según [642b2fe9-8800-491a-915b-eef93466e8e0], este servlet mantiene
 * un registro consistente de actividades usando GestorRegistros.</p>
 * 
 * @author Andrés
 * @version 1.0
 */
@WebServlet(urlPatterns = {"/inicio", "/"})
public class InicioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Inicializa el servlet y configura el sistema de registro.
     * Este método se ejecuta una sola vez al inicio del servlet.
     * 
     * @throws ServletException Si ocurre un error durante la inicialización
     */
    @Override
    public void init() throws ServletException {
        super.init();
        GestorRegistros.inicializar(getServletContext());
        GestorRegistros.sistemaInfo("Sistema de registro inicializado correctamente");
    }

    /**
     * Maneja las peticiones GET a la página de inicio.
     * Registra el acceso y redirige al usuario a la página principal.
     * 
     * @param peticion La petición HTTP del cliente
     * @param respuesta La respuesta HTTP al cliente
     * @throws ServletException Si ocurre un error en el servlet
     * @throws IOException Si ocurre un error de E/S
     */
    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) 
            throws ServletException, IOException {
        try {
            GestorRegistros.sistemaInfo("Acceso a página de inicio");
            peticion.getRequestDispatcher("/inicio.jsp").forward(peticion, respuesta);
        } catch (Exception error) {
            GestorRegistros.sistemaError("Error al cargar página de inicio: " + error.getMessage());
            throw error; // Re-lanzar para mantener el manejo de errores estándar
        }
    }
}