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
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Inicialización del sistema de logs</li>
 *   <li>Registro de mensajes de sistema y usuario</li>
 *   <li>Manejo de excepciones durante el proceso de registro</li>
 * </ul>
 * 
 * <p>Según [875eb101-5aa8-4067-87e7-39617e3a474a], esta clase maneja el registro
 * de eventos relacionados con el sistema y los usuarios.</p>
 * 
 * @author Andrés
 * @version 1.0
 */
public class GestorRegistros {
    private static ServletContext contextoServlet;
    
    // Loggers específicos que coinciden con los nombres en logback.xml
    private static final Logger loggerSistema = LoggerFactory.getLogger("com.andres.gestionalmacen.sistema");
    private static final Logger loggerUsuarios = LoggerFactory.getLogger("com.andres.gestionalmacen.usuarios");

    /**
     * Inicializa el sistema de logs.
     * 
     * @param contexto El contexto del servlet
     */
    public static void inicializar(ServletContext contexto) {
        contextoServlet = contexto;
        sistemaInfo("Sistema de logs inicializado");
    }

    // Métodos para logs del sistema
    /**
     * Registra un mensaje de depuración en el sistema.
     * 
     * @param mensaje El mensaje a registrar
     */
    public static void sistemaDebug(String mensaje) {
        loggerSistema.debug(mensaje);
    }

    /**
     * Registra un mensaje de información en el sistema.
     * 
     * @param mensaje El mensaje a registrar
     */
    public static void sistemaInfo(String mensaje) {
        loggerSistema.info(mensaje);
    }

    /**
     * Registra un mensaje de advertencia en el sistema.
     * 
     * @param mensaje El mensaje a registrar
     */
    public static void sistemaWarning(String mensaje) {
        loggerSistema.warn(mensaje);
    }

    /**
     * Registra un mensaje de error en el sistema.
     * 
     * @param mensaje El mensaje a registrar
     */
    public static void sistemaError(String mensaje) {
        loggerSistema.error(mensaje);
    }

    // Métodos para logs de usuario
    /**
     * Registra un mensaje de depuración para un usuario.
     * 
     * @param mensaje El mensaje a registrar
     */
    public static void usuarioDebug(String mensaje) {
        loggerUsuarios.debug(mensaje);
    }

    /**
     * Registra un mensaje de información para un usuario.
     * 
     * @param mensaje El mensaje a registrar
     */
    public static void usuarioInfo(String mensaje) {
        loggerUsuarios.info(mensaje);
    }

    /**
     * Registra un mensaje de advertencia para un usuario.
     * 
     * @param mensaje El mensaje a registrar
     */
    public static void usuarioWarning(String mensaje) {
        loggerUsuarios.warn(mensaje);
    }

    /**
     * Registra un mensaje de error para un usuario.
     * 
     * @param mensaje El mensaje a registrar
     */
    public static void usuarioError(String mensaje) {
        loggerUsuarios.error(mensaje);
    }

    // Métodos para logs de usuario con ID específico
    /**
     * Registra un mensaje de depuración para un usuario específico.
     * 
     * @param userId El ID del usuario
     * @param mensaje El mensaje a registrar
     */
    public static void debug(Long userId, String mensaje) {
        try {
            MDC.put("userId", userId.toString());
            loggerUsuarios.debug(mensaje);
        } finally {
            MDC.remove("userId");
        }
    }

    /**
     * Registra un mensaje de información para un usuario específico.
     * 
     * @param userId El ID del usuario
     * @param mensaje El mensaje a registrar
     */
    public static void info(Long userId, String mensaje) {
        try {
            MDC.put("userId", userId.toString());
            loggerUsuarios.info(mensaje);
        } finally {
            MDC.remove("userId");
        }
    }

    /**
     * Registra un mensaje de advertencia para un usuario específico.
     * 
     * @param userId El ID del usuario
     * @param mensaje El mensaje a registrar
     */
    public static void warning(Long userId, String mensaje) {
        try {
            MDC.put("userId", userId.toString());
            loggerUsuarios.warn(mensaje);
        } finally {
            MDC.remove("userId");
        }
    }

    /**
     * Registra un mensaje de error para un usuario específico.
     * 
     * @param userId El ID del usuario
     * @param mensaje El mensaje a registrar
     */
    public static void error(Long userId, String mensaje) {
        try {
            MDC.put("userId", userId.toString());
            loggerUsuarios.error(mensaje);
        } finally {
            MDC.remove("userId");
        }
    }
}