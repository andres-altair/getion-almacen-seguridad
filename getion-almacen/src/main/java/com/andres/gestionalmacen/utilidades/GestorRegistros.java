package com.andres.gestionalmacen.utilidades;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

/**
 * Gestor de registros para el sistema de almacén.
 * Se encarga de crear y gestionar archivos de registro para:
 * - Usuarios individuales: <idUsuario>_<fecha>_registro.txt
 * - Sistema: SISTEMA_<fecha>_registro.txt
 * 
 * @author andres
 */
public class GestorRegistros {
    private static final String DIRECTORIO_REGISTROS = "registros";
    private static final long TAMANIO_MAXIMO = 100 * 1024 * 1024; // 100MB
    private static final int DIAS_RETENCION = 30;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String SUFIJO_ANTIGUO = "_ANTIGUO";
    private static final String ID_SISTEMA = "SISTEMA";

    // Códigos de color ANSI
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    private static final String ANSI_BOLD = "\u001B[1m";

    /**
     * Niveles de registro disponibles con sus respectivos colores
     */
    public enum NivelRegistro {
        DEBUG(ANSI_CYAN + "[DEBUG]" + ANSI_RESET),
        INFO(ANSI_GREEN + "[INFO]" + ANSI_RESET),
        WARNING(ANSI_YELLOW + "[ADVERTENCIA]" + ANSI_RESET),
        ERROR(ANSI_RED + ANSI_BOLD + "[ERROR]" + ANSI_RESET),
        FATAL(ANSI_RED_BACKGROUND + ANSI_BOLD + "[FATAL]" + ANSI_RESET);

        private final String etiqueta;

        NivelRegistro(String etiqueta) {
            this.etiqueta = etiqueta;
        }

        public String getEtiqueta() {
            return etiqueta;
        }
    }

    static {
        crearDirectorioRegistros();
    }

    private static void crearDirectorioRegistros() {
        File directorio = new File(DIRECTORIO_REGISTROS);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
    }

    /**
     * Métodos específicos para logs del sistema
     */
    public static void sistemaDebug(String mensaje) {
        registrar(ID_SISTEMA, mensaje, NivelRegistro.DEBUG);
    }

    public static void sistemaInfo(String mensaje) {
        registrar(ID_SISTEMA, mensaje, NivelRegistro.INFO);
    }

    public static void sistemaWarning(String mensaje) {
        registrar(ID_SISTEMA, mensaje, NivelRegistro.WARNING);
    }

    public static void sistemaError(String mensaje) {
        registrar(ID_SISTEMA, mensaje, NivelRegistro.ERROR);
    }

    public static void sistemaFatal(String mensaje) {
        registrar(ID_SISTEMA, mensaje, NivelRegistro.FATAL);
    }

    /**
     * Registra un mensaje de depuración (Cyan)
     */
    public static void debug(Long idUsuario, String mensaje) {
        registrar(idUsuario.toString(), mensaje, NivelRegistro.DEBUG);
    }

    /**
     * Registra un mensaje informativo (Verde)
     */
    public static void info(Long idUsuario, String mensaje) {
        registrar(idUsuario.toString(), mensaje, NivelRegistro.INFO);
    }

    /**
     * Registra una advertencia (Amarillo)
     */
    public static void warning(Long idUsuario, String mensaje) {
        registrar(idUsuario.toString(), mensaje, NivelRegistro.WARNING);
    }

    /**
     * Registra un error (Rojo)
     */
    public static void error(Long idUsuario, String mensaje) {
        registrar(idUsuario.toString(), mensaje, NivelRegistro.ERROR);
    }

    /**
     * Registra un error fatal (Fondo Rojo)
     */
    public static void fatal(Long idUsuario, String mensaje) {
        registrar(idUsuario.toString(), mensaje, NivelRegistro.FATAL);
    }

    /**
     * Registra un mensaje con el nivel y color especificado
     */
    public static synchronized void registrar(String identificador, String mensaje, NivelRegistro nivel) {
        try {
            // Primero verificar si hay que eliminar archivo antiguo
            verificarYEliminarArchivoAntiguo(identificador);
            
            // Buscar o crear archivo para escribir
            String rutaArchivo = buscarOCrearArchivoRegistro(identificador);
            
            // Escribir el mensaje
            try (FileWriter escritor = new FileWriter(rutaArchivo, true)) {
                LocalDateTime ahora = LocalDateTime.now();
                String mensajeFormateado = String.format("[%s] %s %s%n", 
                    ahora.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    nivel.getEtiqueta(),
                    mensaje);
                escritor.write(mensajeFormateado);
                
                // También mostrar en consola
                System.out.print(mensajeFormateado);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Verifica y elimina el archivo antiguo si han pasado más de 30 días
     * Solo se aplica a archivos que superaron los 100MB
     */
    private static void verificarYEliminarArchivoAntiguo(String identificador) {
        try {
            File directorio = new File(DIRECTORIO_REGISTROS);
            
            // Buscar archivo antiguo del usuario (con sufijo _ANTIGUO)
            File[] archivosAntiguos = directorio.listFiles((dir, nombre) -> 
                nombre.startsWith(identificador + "_") && 
                nombre.contains(SUFIJO_ANTIGUO) &&
                nombre.endsWith("_registro.txt"));

            if (archivosAntiguos != null && archivosAntiguos.length > 0) {
                File archivoAntiguo = archivosAntiguos[0]; // Solo debe haber uno
                
                // Obtener la fecha del archivo
                LocalDateTime fechaArchivo = obtenerFechaArchivo(archivoAntiguo.getName());
                LocalDateTime ahora = LocalDateTime.now();
                
                // Si han pasado más de 30 días, eliminar
                if (fechaArchivo != null && 
                    ChronoUnit.DAYS.between(fechaArchivo, ahora) > DIAS_RETENCION) {
                    System.out.println(ANSI_YELLOW + 
                        "Eliminando archivo antiguo que superó 100MB y tiene más de 30 días: " + 
                        archivoAntiguo.getName() + ANSI_RESET);
                    Files.delete(archivoAntiguo.toPath());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Busca el archivo de registro para un usuario siguiendo este flujo:
     * 1. Buscar archivos por ID de usuario
     * 2. Si encuentra varios, usar el más reciente por fecha
     * 3. Si el archivo está lleno (>=100MB):
     *    - Marcar como antiguo
     *    - Crear nuevo archivo
     */
    private static String buscarOCrearArchivoRegistro(String identificador) throws IOException {
        File directorio = new File(DIRECTORIO_REGISTROS);
        
        // 1. Buscar todos los archivos por ID (excluyendo los antiguos)
        File[] archivosUsuario = directorio.listFiles((dir, nombre) -> 
            nombre.startsWith(identificador + "_") && 
            !nombre.contains(SUFIJO_ANTIGUO) &&
            nombre.endsWith("_registro.txt"));

        // Si no hay archivos, crear nuevo
        if (archivosUsuario == null || archivosUsuario.length == 0) {
            return crearNuevoArchivoRegistro(identificador, false);
        }

        // 2. Si hay varios archivos, encontrar el más reciente por fecha
        Optional<File> archivoMasReciente = Arrays.stream(archivosUsuario)
                .max((f1, f2) -> {
                    String fecha1 = extraerFechaDeNombreArchivo(f1.getName());
                    String fecha2 = extraerFechaDeNombreArchivo(f2.getName());
                    return fecha1.compareTo(fecha2);
                });

        // Si encontramos un archivo
        if (archivoMasReciente.isPresent()) {
            File archivo = archivoMasReciente.get();
            
            // 3. Verificar si está lleno (>=100MB)
            if (archivo.length() >= TAMANIO_MAXIMO) {
                // Marcar como antiguo (eliminar el antiguo anterior si existe)
                eliminarArchivoAntiguoExistente(identificador);
                
                // Renombrar el actual como antiguo
                String nombreAntiguo = archivo.getName().replace("_registro.txt", SUFIJO_ANTIGUO + "_registro.txt");
                File archivoAntiguo = new File(directorio, nombreAntiguo);
                archivo.renameTo(archivoAntiguo);
                
                // Crear nuevo archivo actual
                return crearNuevoArchivoRegistro(identificador, false);
            }
            
            // Si no está lleno, usar el existente
            return archivo.getPath();
        }

        // Si por alguna razón no se encontró el archivo, crear nuevo
        return crearNuevoArchivoRegistro(identificador, false);
    }

    /**
     * Elimina el archivo antiguo existente de un usuario si existe
     */
    private static void eliminarArchivoAntiguoExistente(String identificador) {
        try {
            File directorio = new File(DIRECTORIO_REGISTROS);
            File[] archivosAntiguos = directorio.listFiles((dir, nombre) -> 
                nombre.startsWith(identificador + "_") && 
                nombre.contains(SUFIJO_ANTIGUO) &&
                nombre.endsWith("_registro.txt"));

            if (archivosAntiguos != null) {
                for (File antiguo : archivosAntiguos) {
                    Files.delete(antiguo.toPath());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String crearNuevoArchivoRegistro(String identificador, boolean esAntiguo) {
        String timestamp = LocalDateTime.now().format(FORMATO_FECHA);
        String nombreArchivo = identificador + "_" + timestamp + 
                             (esAntiguo ? SUFIJO_ANTIGUO : "") + 
                             "_registro.txt";
        return DIRECTORIO_REGISTROS + File.separator + nombreArchivo;
    }

    /**
     * Convierte la fecha del nombre del archivo a LocalDateTime
     * Formato esperado: <idUsuario>_yyyyMMdd_HHmmss_registro.txt
     */
    private static LocalDateTime obtenerFechaArchivo(String nombreArchivo) {
        try {
            String fechaStr = extraerFechaDeNombreArchivo(nombreArchivo);
            return LocalDateTime.parse(fechaStr, FORMATO_FECHA);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extrae la fecha del nombre del archivo de registro
     * Formato del archivo: <idUsuario>_<fecha>_registro.txt
     */
    private static String extraerFechaDeNombreArchivo(String nombreArchivo) {
        String[] partes = nombreArchivo.split("_");
        if (partes.length >= 2) {
            return partes[1]; // Retorna la parte de la fecha
        }
        return "";
    }
}