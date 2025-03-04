package com.andres.gestionalmacen.servicios;

import com.andres.gestionalmacen.dtos.CrearUsuDto;
import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.utilidades.GestorRegistros;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsuarioServicio {
    private static final String API_BASE_URL = "http://localhost:8081/api/usuarios";
    
    private final ObjectMapper objetoMapeador;
    
    /**
     * @author andres
     * Constructor de la clase UsuarioServicio.
     * Este constructor inicializa el objeto ObjectMapper con configuraciones específicas
     * para manejar la serialización y deserialización de objetos JSON.
     */
    public UsuarioServicio() {
        this.objetoMapeador = new ObjectMapper()
            .registerModule(new JavaTimeModule())//manejar feachas:localdatetime,date...
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)//fecha legible no en ms
            .configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)//Permite nombres de campo no entrecomillados en el JSON
            .configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)//Permite el uso de comillas simples:campo  y valores
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);//para que no falle si SON contiene campos que no están presentes en la clase Java
    }

    public UsuarioDto validarCredenciales(String correoElectronico, String contrasenaEncriptada) throws Exception {
        try {
            Map<String, String> credencialesMap = new HashMap<>();
            credencialesMap.put("correoElectronico", correoElectronico);
            credencialesMap.put("contrasena", contrasenaEncriptada);
            
            String cuerpoJson = objetoMapeador.writeValueAsString(credencialesMap);
            System.out.println("Enviando petición a la API:");
            System.out.println("URL: " + API_BASE_URL + "/autenticar");
            System.out.println("Body: " + cuerpoJson);
            
            URL url = URI.create(API_BASE_URL + "/autenticar").toURL();
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("POST");
            conexion.setRequestProperty("Content-Type", "application/json");
            conexion.setDoOutput(true);
            conexion.getOutputStream().write(cuerpoJson.getBytes());
            
            System.out.println("Respuesta de la API:");
            System.out.println("Status: " + conexion.getResponseCode());
            
            if (conexion.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "UTF-8"));
                StringBuilder respuesta = new StringBuilder();
                String salida;
                while ((salida = br.readLine()) != null) {
                    respuesta.append(salida);
                }
                
                String respuestaJson = respuesta.toString();
                System.out.println("Body: " + respuestaJson);
                
                return objetoMapeador.readValue(respuestaJson, UsuarioDto.class);
            } else {
                String mensajeError = "Error en la autenticación: " + conexion.getResponseCode();
                BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getErrorStream(), "UTF-8"));
                StringBuilder respuesta = new StringBuilder();
                String salida;
                while ((salida = br.readLine()) != null) {
                    respuesta.append(salida);
                }
                
                String respustaJson = respuesta.toString();
                if (respustaJson != null && !respustaJson.isEmpty()) {
                    mensajeError += " - " + respustaJson;
                }
                throw new IOException(mensajeError);
            }
        } catch (Exception e) {
            System.err.println("Error durante la autenticación: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error durante la autenticación: " + e.getMessage(), e);
        }
    }

    public void confirmarCorreo(String email) {
        try {
            URL url = URI.create(API_BASE_URL + "/confirmarCorreo/" + email).toURL();
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("POST");
            conexion.setRequestProperty("Content-Type", "application/json");
            
            int responseCode = conexion.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("Error al confirmar correo: " + responseCode);
            }
            
            GestorRegistros.sistemaInfo("Correo confirmado para: " + email);
        } catch (Exception e) {
            GestorRegistros.sistemaError("Error al confirmar correo: " + e.getMessage());
            throw new RuntimeException("Error al confirmar correo", e);
        }
    }

    public List<UsuarioDto> obtenerUsuarios() throws Exception {
        System.out.println("\n=== UsuarioServicio.obtenerUsuarios - Iniciando ===");
        try {
            System.out.println("URL de la API: " + API_BASE_URL);
            URL url = URI.create(API_BASE_URL).toURL();
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");
            conexion.setRequestProperty("Accept", "application/json");
            conexion.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            
            System.out.println("Conectando a la API...");
            int repuestaCodigo = conexion.getResponseCode();
            System.out.println("Código de respuesta: " + repuestaCodigo);
            
            if (repuestaCodigo != 200) {
                String errorMsg = "Error HTTP: " + repuestaCodigo;
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getErrorStream(), "UTF-8"))) {
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    errorMsg += " - " + response.toString();
                }
                throw new RuntimeException(errorMsg);
            }

            System.out.println("Leyendo respuesta...");
            StringBuilder respuesta = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "UTF-8"))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    respuesta.append(linea);
                }
            }
            
            String respuestaJson = respuesta.toString();
            System.out.println("Respuesta recibida. Longitud: " + respuestaJson.length());
            System.out.println("Respuesta: " + respuestaJson);
            
            System.out.println("Deserializando JSON...");
            List<UsuarioDto> usuarios = objetoMapeador.readValue(respuestaJson, new TypeReference<List<UsuarioDto>>() {});
            System.out.println("Usuarios deserializados: " + usuarios.size());
            
            for (UsuarioDto dto : usuarios) {
                System.out.println("Usuario -> ID: " + dto.getId() + 
                                 ", Nombre: " + dto.getNombreCompleto() + 
                                 ", Email: " + dto.getCorreoElectronico() + 
                                 ", Rol: " + dto.getRolId());
            }
            
            System.out.println("=== UsuarioServicio.obtenerUsuarios - Completado ===\n");
            return usuarios;
            
        } catch (Exception e) {
            System.err.println("\n=== UsuarioServicio.obtenerUsuarios - ERROR ===");
            System.err.println("Mensaje de error: " + e.getMessage());
            System.err.println("Causa: " + (e.getCause() != null ? e.getCause().getMessage() : "No hay causa"));
            e.printStackTrace();
            System.err.println("=====================================\n");
            throw new Exception("Error al obtener usuarios: " + e.getMessage(), e);
        }
    }

    public CrearUsuDto crearUsuario(CrearUsuDto usuarioDTO) throws Exception {
        try {
            // Si el usuario es creado desde el panel de administrador (rol 1), se marca como confirmado automáticamente
            if (usuarioDTO.getRolId() == 1L || usuarioDTO.getRolId() == 2L || usuarioDTO.getRolId() == 3L) {
                usuarioDTO.setCorreoConfirmado(true);
            } else {
                // Si es un registro normal (rol 4), necesita confirmación
                usuarioDTO.setCorreoConfirmado(false);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            
            String usuarioJson = objectMapper.writeValueAsString(usuarioDTO);
            System.out.println("Enviando petición para crear usuario:");
            System.out.println("URL: " + API_BASE_URL);
            System.out.println("Body: " + usuarioJson);
            
            URL url = URI.create(API_BASE_URL).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            
            // Enviar el JSON
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = usuarioJson.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            // Leer la respuesta
            int responseCode = conn.getResponseCode();
            System.out.println("Código de respuesta: " + responseCode);
            
            if (responseCode == 201) {
                // Leer respuesta exitosa
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    String jsonResponse = response.toString();
                    System.out.println("Respuesta exitosa: " + jsonResponse);
                    return objetoMapeador.readValue(jsonResponse, CrearUsuDto.class);
                }
            } else {
                // Leer respuesta de error
                String mensajeError;
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    mensajeError = response.toString();
                }
                
                System.err.println("Error al crear usuario. Código: " + responseCode + ", Mensaje: " + mensajeError);
                
                switch (responseCode) {
                    case 400:
                        throw new Exception("Datos inválidos: " + mensajeError);
                    case 409:
                        throw new Exception("El correo electrónico ya existe");
                    case 401:
                        throw new Exception("No autorizado");
                    case 403:
                        throw new Exception("Acceso denegado");
                    default:
                        throw new Exception("Error del servidor: " + mensajeError);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            throw new Exception("Error al crear usuario: " + e.getMessage());
        }
    }

    public UsuarioDto actualizarUsuario(Long id, CrearUsuDto usuario) throws Exception {
        try {
            String jsonBody = objetoMapeador.writeValueAsString(usuario);
            System.out.println("\n=== Actualizando usuario ===");
            System.out.println("URL: " + API_BASE_URL + "/" + id);
            System.out.println("Body: " + jsonBody);
            
            URL url = URI.create(API_BASE_URL + "/" + id).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Enviar el JSON
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Código de respuesta: " + responseCode);

            if (responseCode != 200) {
                StringBuilder errorResponse = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        errorResponse.append(line);
                    }
                }
                
                String errorMessage = "Error al actualizar usuario. Código: " + responseCode;
                if (errorResponse.length() > 0) {
                    errorMessage += ", Mensaje: " + errorResponse.toString();
                }
                
                System.err.println(errorMessage);
                throw new Exception(errorMessage);
            }

            // Leer respuesta exitosa
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
            
            String jsonResponse = response.toString();
            System.out.println("Respuesta exitosa: " + jsonResponse);
            
            return objetoMapeador.readValue(jsonResponse, UsuarioDto.class);
            
        } catch (Exception e) {
            System.err.println("Error en actualizarUsuario: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al actualizar usuario: " + e.getMessage(), e);
        }
    }

    public void eliminarUsuario(Long id) throws Exception {
        System.out.println("\n=== Eliminando usuario ===");
        System.out.println("URL: " + API_BASE_URL + "/" + id);
        
        HttpURLConnection conn = null;
        try {
            URL url = URI.create(API_BASE_URL + "/" + id).toURL();
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            System.out.println("Conectando al servidor...");
            conn.connect();

            int responseCode = conn.getResponseCode();
            System.out.println("Código de respuesta: " + responseCode);

            // Leer la respuesta si existe
            String responseMessage = "";
            InputStream inputStream = (responseCode >= 400) ? conn.getErrorStream() : conn.getInputStream();
            
            if (inputStream != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    responseMessage = response.toString();
                    if (!responseMessage.isEmpty()) {
                        System.out.println("Respuesta del servidor: " + responseMessage);
                    }
                }
            }

            // Manejar la respuesta según el código
            if (responseCode == 200 || responseCode == 204) {
                System.out.println("Usuario eliminado exitosamente");
                return;
            }
            
            // Si llegamos aquí, es porque hubo un error
            String errorMsg;
            switch (responseCode) {
                case 404:
                    errorMsg = "Usuario no encontrado. ID: " + id;
                    break;
                case 400:
                    errorMsg = "Error en la solicitud: " + responseMessage;
                    break;
                case 401:
                    errorMsg = "No autorizado para eliminar usuarios";
                    break;
                case 403:
                    errorMsg = "Acceso denegado para eliminar usuarios";
                    break;
                case 500:
                    errorMsg = "Error interno del servidor: " + responseMessage;
                    break;
                default:
                    errorMsg = "Error inesperado (Código " + responseCode + "): " + responseMessage;
            }
            throw new Exception(errorMsg);
            
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al eliminar usuario: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                    System.err.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }

    public UsuarioDto obtenerUsuarioPorId(Long id) throws Exception {
        try {
            System.out.println("Obteniendo usuario por ID: " + id);
            URL url = URI.create(API_BASE_URL + "/" + id).toURL();
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");
            conexion.setRequestProperty("Accept", "application/json");
            
            if (conexion.getResponseCode() != 200) {
                throw new RuntimeException("Error HTTP: " + conexion.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "UTF-8"));
            StringBuilder respuesta = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                respuesta.append(linea);
            }
            
            return objetoMapeador.readValue(respuesta.toString(), UsuarioDto.class);
        } catch (Exception e) {
            throw new Exception("Error al obtener usuario por ID: " + e.getMessage(), e);
        }
    }
}
