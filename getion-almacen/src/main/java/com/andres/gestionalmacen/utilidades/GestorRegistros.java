package com.andres.gestionalmacen.utilidades;

import jakarta.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Gestor de registros que se integra con logback.xml para el manejo de logs.
 * Los logs se guardan en la carpeta 'registros' en la raíz del proyecto.
 * - Sistema: SISTEMA_[fecha]_registro.txt
 * - Usuario: [userId]_[fecha]_registro.txt
 */
public class GestorRegistros {
    private static ServletContext contextoServlet;
    
    // Loggers específicos que coinciden con los nombres en logback.xml
    private static final Logger loggerSistema = LoggerFactory.getLogger("com.andres.gestionalmacen.sistema");
    private static final Logger loggerUsuarios = LoggerFactory.getLogger("com.andres.gestionalmacen.usuarios");

    /**
     * Inicializa el sistema de logs.
     * @param contexto El contexto del servlet
     */
    public static void inicializar(ServletContext contexto) {
        contextoServlet = contexto;
        // No necesitamos crear directorios ni archivos
        // logback.xml se encarga de todo eso
        sistemaInfo("Sistema de logs inicializado");
    }

    // Métodos para logs del sistema - van al archivo SISTEMA_[fecha]_registro.txt
    public static void sistemaDebug(String mensaje) {
        loggerSistema.debug(mensaje);
    }

    public static void sistemaInfo(String mensaje) {
        loggerSistema.info(mensaje);
    }

    public static void sistemaWarning(String mensaje) {
        loggerSistema.warn(mensaje);
    }

    public static void sistemaError(String mensaje) {
        loggerSistema.error(mensaje);
    }

    // Métodos para logs de usuario - van al archivo [userId]_[fecha]_registro.txt
    public static void debug(Long idUsuario, String mensaje) {
        try {
            MDC.put("userId", idUsuario != null ? idUsuario.toString() : "sistema");
            loggerUsuarios.debug(mensaje);
        } finally {
            MDC.remove("userId");
        }
    }

    public static void info(Long idUsuario, String mensaje) {
        try {
            MDC.put("userId", idUsuario != null ? idUsuario.toString() : "sistema");
            loggerUsuarios.info(mensaje);
        } finally {
            MDC.remove("userId");
        }
    }

    public static void warning(Long idUsuario, String mensaje) {
        try {
            MDC.put("userId", idUsuario != null ? idUsuario.toString() : "sistema");
            loggerUsuarios.warn(mensaje);
        } finally {
            MDC.remove("userId");
        }
    }

    public static void error(Long idUsuario, String mensaje) {
        try {
            MDC.put("userId", idUsuario != null ? idUsuario.toString() : "sistema");
            loggerUsuarios.error(mensaje);
        } finally {
            MDC.remove("userId");
        }
    }
}