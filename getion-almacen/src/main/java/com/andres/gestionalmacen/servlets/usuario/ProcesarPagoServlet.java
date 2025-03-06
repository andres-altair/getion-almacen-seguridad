package com.andres.gestionalmacen.servlets.usuario;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;

import com.andres.gestionalmacen.dtos.UsuarioDto;
import com.andres.gestionalmacen.dtos.SectorDto;
import com.andres.gestionalmacen.servicios.ApiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/usuario/procesarPago")
public class ProcesarPagoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcesarPagoServlet.class);
    private final ObjectMapper objectMapper;
    private final ApiService apiService;

    public ProcesarPagoServlet() {
        this.objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.apiService = new ApiService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        
        try {
            // Obtener el usuario de la sesión
            HttpSession session = request.getSession();
            UsuarioDto usuario = (UsuarioDto) session.getAttribute("usuario");
            
            if (usuario == null) {
                sendError(response, "Usuario no autenticado");
                return;
            }
            
            // Leer y validar datos del pago
            JsonNode paymentData = objectMapper.readTree(request.getReader());
            if (!validarDatosPago(paymentData)) {
                sendError(response, "Datos de pago inválidos");
                return;
            }
            
            String orderId = paymentData.get("orderId").asText();
            String sectorName = paymentData.get("sectorName").asText();
            BigDecimal amount = new BigDecimal(paymentData.get("amount").asText());
            
            // Obtener y validar sector
            JsonNode sectorResponse = apiService.get("/api/sectores/nombre/" + sectorName);
            SectorDto sector = objectMapper.treeToValue(sectorResponse, SectorDto.class);
            
            if (sector == null || !sector.isDisponible()) {
                sendError(response, "Sector no disponible");
                return;
            }
            
            // Validar monto
            if (!amount.equals(sector.getPrecioMensual())) {
                sendError(response, "Monto incorrecto");
                return;
            }
            
            // Crear el alquiler
            LocalDateTime fechaInicio = LocalDateTime.now();
            LocalDateTime fechaFin = fechaInicio.plus(1, ChronoUnit.MONTHS);
            
            ObjectNode alquilerData = objectMapper.createObjectNode()
                .put("sectorId", sector.getId())
                .put("usuarioId", usuario.getId())
                .put("ordenId", orderId)
                .put("montoPagado", amount.toString())
                .put("fechaInicio", fechaInicio.toString())
                .put("fechaFin", fechaFin.toString());
            
            // Enviar solicitud de alquiler
            JsonNode result = apiService.post("/api/alquileres", alquilerData);
            
            if (result.has("id")) {
                LOGGER.info("Alquiler creado exitosamente: {}", result.get("id").asText());
                sendSuccess(response);
            } else {
                sendError(response, "Error al crear el alquiler");
            }
            
        } catch (Exception e) {
            LOGGER.error("Error procesando pago", e);
            sendError(response, "Error interno: " + e.getMessage());
        }
    }
    
    private boolean validarDatosPago(JsonNode paymentData) {
        return paymentData != null &&
               paymentData.has("orderId") &&
               paymentData.has("sectorName") &&
               paymentData.has("amount");
    }
    
    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.getWriter().write("{\"success\":false,\"error\":\"" + message + "\"}");
    }
    
    private void sendSuccess(HttpServletResponse response) throws IOException {
        response.getWriter().write("{\"success\":true}");
    }
}