package com.andres.gestionalmacen.utilidades;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncriptarUtil {
    
    public static String hashPassword(String password) {
        try {
            System.out.println("Hasheando contraseña original: " + password);
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
            System.out.println("Hash generado: " + resultado);
            return resultado;
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error al hashear contraseña: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al hashear la contraseña", e);
        }
    }
}
