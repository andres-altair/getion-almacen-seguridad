package com.andres.gestionalmacen.utilidades;

import org.apache.tika.Tika;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Clase utilitaria para el manejo de imágenes.
 * Esta clase proporciona métodos para verificar la validez de imágenes y asegurar que los bytes de la imagen incluyan el MIME type.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Verificación de imágenes válidas</li>
 *   <li>Asegurar que los bytes de la imagen incluyan el MIME type</li>
 *   <li>Registro detallado de actividades</li>
 * </ul>
 * 
 * <p>Según [875eb101-5aa8-4067-87e7-39617e3a474a], esta clase maneja el registro
 * de eventos relacionados con la verificación y procesamiento de imágenes.</p>
 * 
 * @author Andrés
 * @version 1.0
 */
public class ImagenUtil {
    
    private static final Logger LOGGER = Logger.getLogger(ImagenUtil.class.getName());
    
    // Set de MIME types permitidos para imágenes
    private static final Set<String> MIME_TYPES_PERMITIDOS = new HashSet<String>() {{
        add("image/jpeg");
        add("image/png");
        add("image/gif");
        add("image/bmp");
        add("image/webp");
    }};
    
    // Set de extensiones permitidas para imágenes
    private static final Set<String> EXTENSIONES_PERMITIDAS = new HashSet<String>() {{
        add("jpg");
        add("jpeg");
        add("png");
        add("gif");
        add("bmp");
        add("webp");
    }};
    
    /**
     * Verifica si un archivo es una imagen válida basándose en su MIME type y extensión
     * @param imageBytes Los bytes de la imagen
     * @param nombreArchivo El nombre del archivo original con su extensión
     * @throws IllegalArgumentException si el archivo no es una imagen válida
     */
    public static void verificarImagen(byte[] imageBytes, String nombreArchivo) {
        if (imageBytes == null || imageBytes.length == 0) {
            LOGGER.severe("No se proporcionaron datos de imagen");
            throw new IllegalArgumentException("No se proporcionaron datos de imagen");
        }
        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            LOGGER.severe("No se proporcionó nombre de archivo");
            throw new IllegalArgumentException("No se proporcionó nombre de archivo");
        }

        try {
            // Verificar MIME type usando Apache Tika
            Tika tika = new Tika();
            String mimeType = tika.detect(imageBytes);
            
            if (!MIME_TYPES_PERMITIDOS.contains(mimeType)) {
                LOGGER.warning("El archivo no es una imagen válida. MIME type detectado: " + mimeType);
                throw new IllegalArgumentException("El archivo no es una imagen válida. MIME type detectado: " + mimeType);
            }
            
            // Verificar extensión
            String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1).toLowerCase();
            if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
                LOGGER.warning("Extensión de archivo no permitida: " + extension);
                throw new IllegalArgumentException("Extensión de archivo no permitida: " + extension);
            }
            
            // Verificar que el MIME type coincida con la extensión
            boolean coincidencia = false;
            if (mimeType.equals("image/jpeg") && (extension.equals("jpg") || extension.equals("jpeg"))) {
                coincidencia = true;
            } else if (mimeType.equals("image/png") && extension.equals("png")) {
                coincidencia = true;
            } else if (mimeType.equals("image/gif") && extension.equals("gif")) {
                coincidencia = true;
            } else if (mimeType.equals("image/bmp") && extension.equals("bmp")) {
                coincidencia = true;
            } else if (mimeType.equals("image/webp") && extension.equals("webp")) {
                coincidencia = true;
            }
            
            if (!coincidencia) {
                LOGGER.warning("Imagen válida. MIME type detectado: " + mimeType + ", extensión: " + extension);
                throw new IllegalArgumentException("Imagen válida. MIME type detectado: " + mimeType + ", extensión: " + extension);
            }
            
        } catch (IllegalArgumentException e) {
            LOGGER.severe("Error al verificar la imagen: " + e.getMessage());
            throw e; // Re-lanzar excepciones de validación
        } catch (Exception e) {
            LOGGER.severe("Error al verificar la imagen: " + e.getMessage());
            throw new IllegalArgumentException("Error al verificar la imagen: " + e.getMessage());
        }
    }

    /**
     * Asegura que los bytes de la imagen incluyan el MIME type
     * @param imageBytes Los bytes de la imagen
     * @return Los bytes de la imagen con el MIME type incluido
     */
    public static byte[] asegurarMimeTypeImagen(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            LOGGER.warning("No se proporcionaron datos de imagen");
            return null;
        }

        try {
            // Primero verificar si ya tiene el MIME type
            String imagenString = new String(imageBytes, StandardCharsets.UTF_8);
            if (imagenString.startsWith("data:")) {
                return imageBytes;
            }

            // Si no tiene MIME type, detectarlo y añadirlo
            Tika tika = new Tika();
            String mimeType = tika.detect(imageBytes);
            String base64ConMime = "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(imageBytes);
            return base64ConMime.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.severe("Error al asegurar MIME type de la imagen: " + e.getMessage());
            return null;
        }
    }
}
