package com.andres.gestionalmacen.utilidades;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Clase utilitaria para el manejo de correos electrónicos.
 * Esta clase proporciona métodos para generar tokens de confirmación,
 * validar tokens, y enviar correos de confirmación y recuperación.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Generación de tokens de confirmación</li>
 *   <li>Validación de tokens</li>
 *   <li>Envío de correos de confirmación</li>
 *   <li>Envío de correos de recuperación de contraseña</li>
 *   <li>Registro detallado de actividades</li>
 * </ul>
 * 
 * <p>Según [875eb101-5aa8-4067-87e7-39617e3a474a], esta clase maneja el registro
 * de eventos relacionados con el envío de correos.</p>
 * 
 * <p>Según [35176471-70ce-4b89-92e3-77ccfc940534], utiliza un almacenamiento de tokens
 * para gestionar la validez de los mismos.</p>
 * 
 * @author Andrés
 * @version 1.0
 */
public class EmailUtil {
    private static final String FROM_EMAIL = "andresxiaomd12@gmail.com";
    private static final String PASSWORD = "frjn ntoe crjy bqgi";
    private static final long TOKEN_EXPIRATION_HOURS = 1; // Token expira en 1 hora
    private static final Map<String, TokenInfo> tokenStorage = new HashMap<>();

    private static class TokenInfo {
        String email;
        Instant expirationTime;
        
        TokenInfo(String email, Instant expirationTime) {
            this.email = email;
            this.expirationTime = expirationTime;
        }
    }

    /**
     * Genera un token de confirmación para el correo proporcionado.
     * 
     * @param email El correo electrónico del usuario
     * @return El token generado
     */
    public static String generarToken(String email) {
        String token = Base64.getEncoder().encodeToString((email + ":" + System.currentTimeMillis()).getBytes());
        Instant expiracion = Instant.now().plus(TOKEN_EXPIRATION_HOURS, ChronoUnit.HOURS);
        tokenStorage.put(token, new TokenInfo(email, expiracion));
        GestorRegistros.sistemaInfo("Token de confirmación generado para: " + email);
        return token;
    }

    /**
     * Valida un token de confirmación.
     * 
     * @param token El token a validar
     * @return true si el token es válido, false en caso contrario
     */
    public static boolean validarToken(String token) {
        TokenInfo info = tokenStorage.get(token);
        if (info == null) {
            GestorRegistros.sistemaInfo("Token de confirmación inválido: " + token);
            return false;
        }
        
        if (Instant.now().isAfter(info.expirationTime)) {
            tokenStorage.remove(token);
            GestorRegistros.sistemaInfo("Token de confirmación expirado: " + token);
            return false;
        }
        
        GestorRegistros.sistemaInfo("Token de confirmación válido: " + token);
        return true;
    }

    /**
     * Obtiene el correo electrónico asociado a un token.
     * 
     * @param token El token del cual se desea obtener el correo
     * @return El correo electrónico asociado al token, o null si no existe
     */
    public static String obtenerCorreoDeToken(String token) {
        TokenInfo info = tokenStorage.get(token);
        return info != null ? info.email : null;
    }

    /**
     * Envía un correo de confirmación al usuario.
     * 
     * @param toEmail El correo electrónico del destinatario
     * @param token El token de confirmación
     */
    public static void enviarCorreoConfirmacion(String toEmail, String token) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Confirma tu correo electrónico");

            String contexto = System.getProperty("server.context");
            String contenido = String.format(
                "Hola,<br/><br/>" +
                "Gracias por registrarte. Por favor, confirma tu correo electrónico haciendo clic en el siguiente enlace:<br/><br/>" +
                "<a href='http://localhost:8080/gestion-almacen/confirmarCorreo?token=%s'>Confirmar correo electrónico</a><br/><br/>" +
                "Este enlace expirará en %d horas.<br/><br/>" +
                "Si no has creado una cuenta, puedes ignorar este mensaje.<br/><br/>" +
                "Saludos,<br/>El equipo de Gestión de Almacén", 
                token, TOKEN_EXPIRATION_HOURS);

            message.setContent(contenido, "text/html; charset=utf-8");
            Transport.send(message);
            
            GestorRegistros.sistemaInfo("Correo de confirmación enviado a: " + toEmail);
        } catch (MessagingException e) {
            GestorRegistros.sistemaError("Error al enviar correo de confirmación: " + e.getMessage());
            throw new RuntimeException("Error al enviar el correo de confirmación", e);
        }
    }

    /**
     * Reenvía el correo de confirmación al usuario.
     * 
     * @param email El correo electrónico del destinatario
     */
    public static void reenviarCorreoConfirmacion(String email) {
        // Invalidar token anterior si existe
        tokenStorage.entrySet().removeIf(entry -> entry.getValue().email.equals(email));
        
        // Generar nuevo token y enviar correo
        String nuevoToken = generarToken(email);
        enviarCorreoConfirmacion(email, nuevoToken);
    }

    /**
     * Envía un correo de recuperación de contraseña al usuario.
     * 
     * @param email El correo electrónico del destinatario
     * @param token El token de recuperación
     */
    public static void enviarCorreoRecuperacionContasena(String email, String token) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        String contexto = System.getProperty("server.context");
        String contenido = String.format(
            "Hola,<br/><br/>" +
            "Has solicitado restablecer tu contraseña. Haz clic en el siguiente enlace:<br/><br/>" +
            "<a href='http://localhost:8080/gestion-almacen/restablecerContrasena?token=%s'>Restablecer contraseña</a><br/><br/>" +
            "Este enlace expirará en %d horas.<br/><br/>" +
            "Si no solicitaste esto, ignora este mensaje.<br/><br/>" +
            "Saludos,<br/>El equipo de Gestión de Almacén", 
            token, TOKEN_EXPIRATION_HOURS);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Restablecer contraseña - Gestión Almacén");

            message.setContent(contenido, "text/html; charset=utf-8");

            Transport.send(message);
            GestorRegistros.sistemaInfo("Correo de recuperación de contraseña enviado a: " + email);
        } catch (MessagingException e) {
            GestorRegistros.sistemaError("Error al enviar correo de recuperación: " + e.getMessage());
            throw new RuntimeException("Error al enviar el correo de recuperación", e);
        }
    }
}