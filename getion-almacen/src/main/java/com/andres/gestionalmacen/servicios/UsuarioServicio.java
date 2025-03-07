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

/**
 * Clase que maneja la lógica de negocio relacionada con los usuarios.
 * Esta clase proporciona métodos para validar credenciales, confirmar correos,
 * obtener usuarios y crear nuevos usuarios.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Validación de credenciales de usuario</li>
 *   <li>Confirmación de correos electrónicos</li>
 *   <li>Obtención de la lista de usuarios</li>
 *   <li>Creación de nuevos usuarios</li>
 * </ul>
 * 
 * <p>Según [875eb101-5aa8-4067-87e7-39617e3a474a], esta clase maneja el registro
 * de eventos relacionados con las operaciones de usuario.</p>
 * 
 * @author Andrés
 * @version 1.0
 */
public class UsuarioServicio {
    private static final String API_BASE_URL = "http://localhost:8081/api/usuarios";
    
    private final ObjectMapper objetoMapeador;
    
    /**
     * Constructor de la clase UsuarioServicio.
     * Este constructor inicializa el objeto ObjectMapper con configuraciones específicas
     * para manejar la serialización y deserialización de objetos JSON.
     */
    public UsuarioServicio() {
        this.objetoMapeador = new ObjectMapper()
            .registerModule(new JavaTimeModule())//manejar fechas:localdatetime,date...
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)//fecha legible no en ms
            .configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)//Permite nombres de campo no entrecomillados en el JSON
            .configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)//Permite el uso de comillas simples:campo y valores
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);//para que no falle si contiene campos que no están presentes en la clase Java
    }

    /**
     * Valida las credenciales del usuario enviando una petición a la API.
     * 
     * @param correoElectronico El correo electrónico del usuario
     * @param contrasenaEncriptada La contraseña encriptada del usuario
     * @return Un objeto UsuarioDto con la información del usuario autenticado
     * @throws Exception Si ocurre un error durante la autenticación
     */
    public UsuarioDto validarCredenciales(String correoElectronico, String contrasenaEncriptada) throws Exception {
        try {
            Map<String, String> credencialesMap = new HashMap<>();
            credencialesMap.put("correoElectronico", correoElectronico);
            credencialesMap.put("contrasena", contrasenaEncriptada);
            
            String cuerpoJson = objetoMapeador.writeValueAsString(credencialesMap);
            GestorRegistros.sistemaInfo("Enviando petición a la API para validar credenciales");
            
            URL url = URI.create(API_BASE_URL + "/autenticar").toURL();
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("POST");
            conexion.setRequestProperty("Content-Type", "application/json");
            conexion.setDoOutput(true);
            conexion.getOutputStream().write(cuerpoJson.getBytes());
            
            GestorRegistros.sistemaInfo("Respuesta de la API:");
            if (conexion.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "UTF-8"));
                StringBuilder respuesta = new StringBuilder();
                String salida;
                while ((salida = br.readLine()) != null) {
                    respuesta.append(salida);
                }
                
                String respuestaJson = respuesta.toString();
                GestorRegistros.sistemaInfo("Credenciales validadas, respuesta: " + respuestaJson);
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
                GestorRegistros.sistemaError(mensajeError);
                throw new IOException(mensajeError);
            }
        } catch (Exception e) {
            GestorRegistros.sistemaError("Error durante la autenticación: " + e.getMessage());
            throw new Exception("Error durante la autenticación: " + e.getMessage(), e);
        }
    }

    /**
     * Confirma el correo electrónico del usuario enviando una petición a la API.
     * 
     * @param email El correo electrónico del usuario a confirmar
     */
    public void confirmarCorreo(String email) {
        try {
            URL url = URI.create(API_BASE_URL + "/confirmarCorreo/" + email).toURL();
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("POST");
            conexion.setRequestProperty("Content-Type", "application/json");
            
            int responseCode = conexion.getResponseCode();
            if (responseCode != 200) {
                GestorRegistros.sistemaError("Error al confirmar correo: " + responseCode);
                throw new RuntimeException("Error al confirmar correo: " + responseCode);
            }
            
            GestorRegistros.sistemaInfo("Correo confirmado para: " + email);
        } catch (Exception e) {
            GestorRegistros.sistemaError("Error al confirmar correo: " + e.getMessage());
            throw new RuntimeException("Error al confirmar correo", e);
        }
    }

    /**
     * Obtiene la lista de usuarios desde la API.
     * 
     * @return Una lista de objetos UsuarioDto
     * @throws Exception Si ocurre un error al obtener los usuarios
     */
    public List<UsuarioDto> obtenerUsuarios() throws Exception {
        GestorRegistros.sistemaInfo("Iniciando obtención de usuarios...");
        try {
            URL url = URI.create(API_BASE_URL).toURL();
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");
            conexion.setRequestProperty("Accept", "application/json");
            conexion.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            
            int repuestaCodigo = conexion.getResponseCode();
            if (repuestaCodigo != 200) {
                String errorMsg = "Error HTTP: " + repuestaCodigo;
                GestorRegistros.sistemaError(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            StringBuilder respuesta = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "UTF-8"))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    respuesta.append(linea);
                }
            }
            
            String respuestaJson = respuesta.toString();
            return objetoMapeador.readValue(respuestaJson, new TypeReference<List<UsuarioDto>>() {});
        } catch (Exception e) {
            GestorRegistros.sistemaError("Error al obtener usuarios: " + e.getMessage());
            throw new Exception("Error al obtener usuarios: " + e.getMessage(), e);
        }
    }

    /**
     * Crea un nuevo usuario enviando una petición a la API.
     * 
     * @param usuarioDTO El objeto CrearUsuDto con la información del nuevo usuario
     * @return El objeto UsuarioDto creado
     * @throws Exception Si ocurre un error durante la creación del usuario
     */
    public CrearUsuDto crearUsuario(CrearUsuDto usuarioDTO) throws Exception {
        try {
            String usuarioJson = objetoMapeador.writeValueAsString(usuarioDTO);
            GestorRegistros.sistemaInfo("Enviando petición para crear usuario:");
            
            URL url = URI.create(API_BASE_URL).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.getOutputStream().write(usuarioJson.getBytes());
            
            int responseCode = conn.getResponseCode();
            if (responseCode != 201) {
                GestorRegistros.sistemaError("Error al crear usuario: " + responseCode);
                throw new RuntimeException("Error al crear usuario: " + responseCode);
            }
            
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder respuesta = new StringBuilder();
            String salida;
            while ((salida = br.readLine()) != null) {
                respuesta.append(salida);
            }
            
            return objetoMapeador.readValue(respuesta.toString(), CrearUsuDto.class);
        } catch (Exception e) {
            GestorRegistros.sistemaError("Error al crear usuario: " + e.getMessage());
            throw new Exception("Error al crear usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza un usuario existente enviando una petición a la API.
     * 
     * @param id El ID del usuario a actualizar
     * @param usuario El objeto CrearUsuDto con la información actualizada del usuario
     * @return El objeto CrearUsuDto actualizado
     * @throws Exception Si ocurre un error durante la actualización del usuario
     */
    public CrearUsuDto actualizarUsuario(Long id, CrearUsuDto usuario) throws Exception {
        try {
            String jsonBody = objetoMapeador.writeValueAsString(usuario);
            GestorRegistros.sistemaInfo("Actualizando usuario con ID: " + id);
            
            URL url = URI.create(API_BASE_URL + "/" + id).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            conn.getOutputStream().write(jsonBody.getBytes());
            
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                GestorRegistros.sistemaError("Error al actualizar usuario: " + responseCode);
                throw new RuntimeException("Error al actualizar usuario: " + responseCode);
            }
            
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder respuesta = new StringBuilder();
            String salida;
            while ((salida = br.readLine()) != null) {
                respuesta.append(salida);
            }
            
            return objetoMapeador.readValue(respuesta.toString(), CrearUsuDto.class);
        } catch (Exception e) {
            GestorRegistros.sistemaError("Error al actualizar usuario: " + e.getMessage());
            throw new Exception("Error al actualizar usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un usuario existente enviando una petición a la API.
     * 
     * @param id El ID del usuario a eliminar
     * @throws Exception Si ocurre un error durante la eliminación del usuario
     */
    public void eliminarUsuario(Long id) throws Exception {
        try {
            URL url = URI.create(API_BASE_URL + "/" + id).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");
            
            int responseCode = conn.getResponseCode();
            if (responseCode != 200 && responseCode != 204) {
                GestorRegistros.sistemaError("Error al eliminar usuario: " + responseCode);
                throw new RuntimeException("Error al eliminar usuario: " + responseCode);
            }
            
            GestorRegistros.sistemaInfo("Usuario eliminado con éxito");
        } catch (Exception e) {
            GestorRegistros.sistemaError("Error al eliminar usuario: " + e.getMessage());
            throw new Exception("Error al eliminar usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene un usuario por su ID enviando una petición a la API.
     * 
     * @param id El ID del usuario a obtener
     * @return El objeto UsuarioDto correspondiente al ID
     * @throws Exception Si ocurre un error durante la obtención del usuario
     */
    public UsuarioDto obtenerUsuarioPorId(Long id) throws Exception {
        try {
            URL url = URI.create(API_BASE_URL + "/" + id).toURL();
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");
            conexion.setRequestProperty("Accept", "application/json");
            
            int responseCode = conexion.getResponseCode();
            if (responseCode != 200) {
                GestorRegistros.sistemaError("Error al obtener usuario: " + responseCode);
                throw new RuntimeException("Error al obtener usuario: " + responseCode);
            }
            
            BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "UTF-8"));
            StringBuilder respuesta = new StringBuilder();
            String salida;
            while ((salida = br.readLine()) != null) {
                respuesta.append(salida);
            }
            
            return objetoMapeador.readValue(respuesta.toString(), UsuarioDto.class);
        } catch (Exception e) {
            GestorRegistros.sistemaError("Error al obtener usuario: " + e.getMessage());
            throw new Exception("Error al obtener usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza la contraseña de un usuario existente enviando una petición a la API.
     * 
     * @param email El correo electrónico del usuario
     * @param nuevaContrasena La nueva contraseña del usuario
     * @throws Exception Si ocurre un error durante la actualización de la contraseña
     */
    public void actualizarContrasena(String email, String nuevaContrasena) throws Exception {
        try {
            Map<String, String> datos = new HashMap<>();
            datos.put("email", email);
            datos.put("nuevaContrasena", nuevaContrasena);
            
            String jsonDatos = objetoMapeador.writeValueAsString(datos);
            
            URL url = URI.create(API_BASE_URL + "/actualizarContrasena").toURL();
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("POST");
            conexion.setRequestProperty("Content-Type", "application/json");
            conexion.setDoOutput(true);
            
            conexion.getOutputStream().write(jsonDatos.getBytes());
            
            int responseCode = conexion.getResponseCode();
            if (responseCode != 200) {
                GestorRegistros.sistemaError("Error al actualizar contraseña: " + responseCode);
                throw new RuntimeException("Error al actualizar contraseña: " + responseCode);
            }
            
            GestorRegistros.sistemaInfo("Contraseña actualizada con éxito");
        } catch (Exception e) {
            GestorRegistros.sistemaError("Error al actualizar contraseña: " + e.getMessage());
            throw new Exception("Error al actualizar contraseña: " + e.getMessage(), e);
        }
    }

    /**
     * Busca un usuario por su correo electrónico enviando una petición a la API.
     * 
     * @param correoElectronico El correo electrónico del usuario a buscar
     * @return El objeto UsuarioDto correspondiente al correo electrónico
     * @throws Exception Si ocurre un error durante la búsqueda del usuario
     */
    public UsuarioDto buscarPorCorreo(String correoElectronico) throws Exception {
        try {
            URL url = URI.create(API_BASE_URL + "/correo/" + correoElectronico).toURL();
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");
            conexion.setRequestProperty("Accept", "application/json");
            
            int responseCode = conexion.getResponseCode();
            if (responseCode != 200) {
                GestorRegistros.sistemaError("Error al buscar usuario: " + responseCode);
                throw new RuntimeException("Error al buscar usuario: " + responseCode);
            }
            
            BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "UTF-8"));
            StringBuilder respuesta = new StringBuilder();
            String salida;
            while ((salida = br.readLine()) != null) {
                respuesta.append(salida);
            }
            
            return objetoMapeador.readValue(respuesta.toString(), UsuarioDto.class);
        } catch (Exception e) {
            GestorRegistros.sistemaError("Error al buscar usuario: " + e.getMessage());
            throw new Exception("Error al buscar usuario: " + e.getMessage(), e);
        }
    }
}
