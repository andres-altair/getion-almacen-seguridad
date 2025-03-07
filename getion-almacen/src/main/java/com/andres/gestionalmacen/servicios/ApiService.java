package com.andres.gestionalmacen.servicios;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase ApiService que maneja las interacciones con la API.
 * Esta clase proporciona métodos para realizar solicitudes HTTP a la API
 * y manejar las respuestas.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Realizar solicitudes GET, POST y PUT a la API</li>
 *   <li>Manejar errores y reintentos en las solicitudes</li>
 *   <li>Soporte para solicitudes asíncronas</li>
 * </ul>
 * 
 * <p>Según [875eb101-5aa8-4067-87e7-39617e3a474a], esta clase maneja el registro
 * de eventos relacionados con las interacciones con la API.</p>
 * 
 * @author Andrés
 * @version 1.0
 */
public class ApiService {
    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);
    private static final String API_BASE_URL = "http://localhost:8081/api";
    private static final int MAX_RETRIES = 3;
    private static final Duration TIMEOUT = Duration.ofSeconds(30);
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /** 
     * Constructor de la clase ApiService.
     * Este constructor inicializa el cliente HTTP y el objeto ObjectMapper
     * con configuraciones específicas para manejar la serialización y deserialización de objetos JSON.
     */
    public ApiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .executor(java.util.concurrent.Executors.newFixedThreadPool(10))
                .version(HttpClient.Version.HTTP_2)
                .build();
                
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    /** 
     * Realiza una solicitud GET a la API.
     * 
     * @param endpoint El endpoint de la API al que se realiza la solicitud
     * @return Un objeto JsonNode con la respuesta de la API
     * @throws IOException Si ocurre un error durante la solicitud
     * @throws InterruptedException Si la operación es interrumpida
     */
    public JsonNode get(String endpoint) throws IOException, InterruptedException {
        return executeWithRetry(() -> executeRequest(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(API_BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .timeout(TIMEOUT)
                .build()));
    }

    /** 
     * Realiza una solicitud POST a la API.
     * 
     * @param endpoint El endpoint de la API al que se realiza la solicitud
     * @param body El cuerpo de la solicitud como un objeto ObjectNode
     * @return Un objeto JsonNode con la respuesta de la API
     * @throws IOException Si ocurre un error durante la solicitud
     * @throws InterruptedException Si la operación es interrumpida
     */
    public JsonNode post(String endpoint, ObjectNode body) throws IOException, InterruptedException {
        return executeWithRetry(() -> executeRequest(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .uri(URI.create(API_BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .timeout(TIMEOUT)
                .build()));
    }

    /** 
     * Realiza una solicitud PUT a la API.
     * 
     * @param endpoint El endpoint de la API al que se realiza la solicitud
     * @param body El cuerpo de la solicitud como un objeto ObjectNode
     * @return Un objeto JsonNode con la respuesta de la API
     * @throws IOException Si ocurre un error durante la solicitud
     * @throws InterruptedException Si la operación es interrumpida
     */
    public JsonNode put(String endpoint, ObjectNode body) throws IOException, InterruptedException {
        return executeWithRetry(() -> executeRequest(HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                .uri(URI.create(API_BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .timeout(TIMEOUT)
                .build()));
    }

    /** 
     * Ejecuta una solicitud HTTP y maneja la respuesta.
     * 
     * @param request El objeto HttpRequest que se va a ejecutar
     * @return Un objeto JsonNode con la respuesta de la API
     * @throws IOException Si ocurre un error durante la solicitud
     * @throws InterruptedException Si la operación es interrumpida
     */
    private JsonNode executeRequest(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() >= 400) {
            String errorMsg = String.format("API error: %d - %s", response.statusCode(), response.body());
            logger.error(errorMsg);
            throw new IOException(errorMsg);
        }
        
        return objectMapper.readTree(response.body());
    }

    /** 
     * Ejecuta una solicitud con reintentos en caso de fallos.
     * 
     * @param executor El ejecutor de la solicitud
     * @return Un objeto JsonNode con la respuesta de la API
     * @throws IOException Si ocurre un error durante la solicitud
     * @throws InterruptedException Si la operación es interrumpida
     */
    private JsonNode executeWithRetry(RequestExecutor executor) throws IOException, InterruptedException {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            try {
                return executor.execute();
            } catch (Exception e) {
                attempts++;
                if (attempts == MAX_RETRIES) {
                    logger.error("Failed after {} retries", MAX_RETRIES, e);
                    throw e;
                }
                logger.warn("Attempt {} failed, retrying...", attempts, e);
                TimeUnit.SECONDS.sleep(attempts); // Exponential backoff
            }
        }
        throw new IOException("Failed to execute request after " + MAX_RETRIES + " retries");
    }

    @FunctionalInterface
    private interface RequestExecutor {
        JsonNode execute() throws IOException, InterruptedException;
    }

    /** 
     * Realiza una solicitud GET asíncrona a la API.
     * 
     * @param endpoint El endpoint de la API al que se realiza la solicitud
     * @return Un CompletableFuture con la respuesta de la API como JsonNode
     */
    public CompletableFuture<JsonNode> getAsync(String endpoint) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return get(endpoint);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}