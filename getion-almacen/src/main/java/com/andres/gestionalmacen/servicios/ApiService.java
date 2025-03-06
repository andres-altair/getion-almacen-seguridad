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

public class ApiService {
    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);
    private static final String API_BASE_URL = "http://localhost:8081/api";
    private static final int MAX_RETRIES = 3;
    private static final Duration TIMEOUT = Duration.ofSeconds(30);
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

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

    public JsonNode get(String endpoint) throws IOException, InterruptedException {
        return executeWithRetry(() -> executeRequest(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(API_BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .timeout(TIMEOUT)
                .build()));
    }

    public JsonNode post(String endpoint, ObjectNode body) throws IOException, InterruptedException {
        return executeWithRetry(() -> executeRequest(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .uri(URI.create(API_BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .timeout(TIMEOUT)
                .build()));
    }

    public JsonNode put(String endpoint, ObjectNode body) throws IOException, InterruptedException {
        return executeWithRetry(() -> executeRequest(HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                .uri(URI.create(API_BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .timeout(TIMEOUT)
                .build()));
    }

    private JsonNode executeRequest(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() >= 400) {
            String errorMsg = String.format("API error: %d - %s", response.statusCode(), response.body());
            logger.error(errorMsg);
            throw new IOException(errorMsg);
        }
        
        return objectMapper.readTree(response.body());
    }

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