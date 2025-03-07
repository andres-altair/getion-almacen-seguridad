package com.andres.gestionalmacen.utilidades;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase utilitaria para el manejo de encriptación de contraseñas.
 * Esta clase proporciona métodos para hashear contraseñas utilizando el algoritmo SHA-256.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Hash de contraseñas</li>
 *   <li>Manejo de excepciones durante el proceso de encriptación</li>
 * </ul>
 * 
 * <p>Según [875eb101-5aa8-4067-87e7-39617e3a474a], esta clase maneja el registro
 * de eventos relacionados con el hashing de contraseñas.</p>
 * 
 * @author Andrés
 * @version 1.0
 */
public class EncriptarUtil {
    
    /**
     * Hashea la contraseña proporcionada utilizando SHA-256.
     * 
     * @param password La contraseña a hashear
     * @return La contraseña hasheada en formato hexadecimal
     */
    public static String contraseñaHash(String password) {
        try {
            GestorRegistros.sistemaInfo("Hasheando contraseña original: " + password);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Convertir el hash a hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            String resultado = hexString.toString();
            GestorRegistros.sistemaInfo("Hash generado: " + resultado);
            return resultado;
        } catch (NoSuchAlgorithmException e) {
            GestorRegistros.sistemaError("Error al hashear contraseña: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al hashear la contraseña", e);
        }
    }
}
