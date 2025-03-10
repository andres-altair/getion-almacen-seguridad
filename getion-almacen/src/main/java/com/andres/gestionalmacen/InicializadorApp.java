package com.andres.gestionalmacen;

import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.annotation.WebListener;
import com.andres.gestionalmacen.utilidades.GestorRegistros;

@WebListener
public class InicializadorApp implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //System.out.println("=== Iniciando AppInitializer ===");
        try {
            // Inicializar el sistema de logs
            GestorRegistros.inicializar(sce.getServletContext());
           // System.out.println("=== Sistema de logs inicializado correctamente ===");
        } catch (Exception e) {
            //System.err.println("Error al inicializar logs: " + e.getMessage());
            //e.printStackTrace();
            GestorRegistros.sistemaError("Error al inicializar logs: " + e.getMessage());
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //System.out.println("=== Deteniendo AppInitializer ===");
        GestorRegistros.sistemaInfo("Deteniendo AppInitializer");
    }
}