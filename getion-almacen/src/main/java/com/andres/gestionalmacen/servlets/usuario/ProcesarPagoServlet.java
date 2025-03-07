package com.andres.gestionalmacen.servlets.usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Servlet que maneja el procesamiento de pagos.
 * Este servlet verifica la sesión del usuario y procesa el pago a través de PayPal.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Verificación de sesión del usuario</li>
 *   <li>Procesamiento de pagos utilizando la API de PayPal</li>
 * </ul>
 * 
 * <p>Según [875eb101-5aa8-4067-87e7-39617e3a474a], esta clase maneja el registro
 * de eventos relacionados con el procesamiento de pagos.</p>
 * 
 * @author Andrés
 * @version 1.0
 */
@WebServlet("/usuario/procesarPago")
public class ProcesarPagoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcesarPagoServlet.class);
    private APIContext apiContext;

    /**
     * Inicializa el servlet y configura la conexión con PayPal.
     * 
     * @throws ServletException si ocurre un error durante la inicialización
     */
    @Override
    public void init() throws ServletException {
        try {
            String clientId = getServletContext().getInitParameter("paypal.client.id");
            String clientSecret = getServletContext().getInitParameter("paypal.client.secret");
            String mode = getServletContext().getInitParameter("paypal.mode");
            
            LOGGER.info("Inicializando PayPal - Mode: {}", mode);
            
            if (clientId == null || clientId.isEmpty()) {
                throw new ServletException("PayPal Client ID no configurado");
            }
            if (clientSecret == null || clientSecret.isEmpty()) {
                throw new ServletException("PayPal Client Secret no configurado");
            }
            
            // Configuración del SDK de PayPal
            Map<String, String> sdkConfig = new HashMap<>();
            sdkConfig.put("mode", mode);
            sdkConfig.put("http.ConnectionTimeOut", "30000");
            sdkConfig.put("http.RetryCount", "1");
            sdkConfig.put("http.ReadTimeOut", "30000");
            sdkConfig.put("http.MaxConnection", "100");
            
            if ("sandbox".equals(mode)) {
            sdkConfig.put("service.EndPoint", "https://api.sandbox.paypal.com");
            sdkConfig.put("oauth.EndPoint", "https://api.sandbox.paypal.com");
            } else {
                sdkConfig.put("service.EndPoint", "https://api.paypal.com");
                sdkConfig.put("oauth.EndPoint", "https://api.paypal.com");
            }
            
            apiContext = new APIContext(clientId, clientSecret, mode);
            apiContext.setConfigurationMap(sdkConfig);
            
            LOGGER.info("PayPal inicializado correctamente con modo: {} y endpoint: {}", 
                       mode, sdkConfig.get("service.EndPoint"));
        } catch (Exception e) {
            LOGGER.error("Error al inicializar PayPal", e);
            throw new ServletException("Error al inicializar PayPal", e);
        }
    }

    /**
     * Maneja las solicitudes GET y redirige al panel de usuario.
     * 
     * @param request la solicitud HTTP
     * @param response la respuesta HTTP
     * @throws ServletException si ocurre un error durante el procesamiento
     * @throws IOException si ocurre un error de E/S
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        LOGGER.info("Intento de acceso GET a ProcesarPagoServlet");
        // Si intentan acceder directamente al servlet por GET, redirigir al panel
        response.sendRedirect(request.getContextPath() + "/usuario/panel");
    }

    /**
     * Maneja las solicitudes POST y procesa el pago a través de PayPal.
     * 
     * @param request la solicitud HTTP
     * @param response la respuesta HTTP
     * @throws ServletException si ocurre un error durante el procesamiento
     * @throws IOException si ocurre un error de E/S
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        LOGGER.info("Iniciando procesamiento de pago");
        
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuario") == null) {
                LOGGER.error("Sesión inválida");
                response.sendRedirect(request.getContextPath() + "/acceso");
                return;
            }

            String total = request.getParameter("total");
            String sectorId = request.getParameter("sectorId");
            String sectorName = request.getParameter("sectorName");
            
            LOGGER.info("Parámetros recibidos - total: {}, sectorId: {}, sectorName: {}", 
                       total, sectorId, sectorName);
            
            if (total == null || sectorId == null) {
                LOGGER.error("Parámetros faltantes");
                request.setAttribute("error", "Parámetros de pago incompletos");
                request.getRequestDispatcher("/usuario/pago-cancelado.jsp").forward(request, response);
                return;
            }

            try {
                double totalAmount = Double.parseDouble(total);
                if (totalAmount <= 0) {
                    LOGGER.error("Monto inválido: {}", totalAmount);
                    request.setAttribute("error", "Monto de pago inválido");
                    request.getRequestDispatcher("/usuario/pago-cancelado.jsp").forward(request, response);
                    return;
                }

                String baseURL = request.getScheme() + "://" + request.getServerName() + ":" + 
                               request.getServerPort() + request.getContextPath();
                
                LOGGER.info("Creando pago en PayPal - Monto: {}, BaseURL: {}", totalAmount, baseURL);
                
                Payment payment = createPayment(
                    totalAmount,
                    "EUR",
                    baseURL + "/usuario/pago-cancelado",
                    baseURL + "/usuario/completarPago"
                );

                if (payment == null) {
                    LOGGER.error("Error al crear el pago en PayPal - payment es null");
                    request.setAttribute("error", "Error al crear el pago");
                    request.getRequestDispatcher("/usuario/pago-cancelado.jsp").forward(request, response);
                    return;
                }

                LOGGER.info("Pago creado en PayPal - ID: {}", payment.getId());

                String redirectUrl = null;
                for(Links link : payment.getLinks()) {
                    LOGGER.info("Link encontrado - Rel: {}, Href: {}", link.getRel(), link.getHref());
                    if("approval_url".equals(link.getRel())) {
                        redirectUrl = link.getHref();
                    }
                }

                if (redirectUrl != null) {
                    session.setAttribute("paymentId", payment.getId());
                    session.setAttribute("sectorId", sectorId);
                    session.setAttribute("sectorName", sectorName);
                    
                    LOGGER.info("Redirigiendo a PayPal: {}", redirectUrl);
                    response.sendRedirect(redirectUrl);
                } else {
                    LOGGER.error("No se encontró URL de aprobación en la respuesta de PayPal");
                    request.setAttribute("error", "Error en la configuración de PayPal");
                    request.getRequestDispatcher("/usuario/pago-cancelado.jsp").forward(request, response);
                }
                
            } catch (NumberFormatException e) {
                LOGGER.error("Error al convertir el monto: {}", total, e);
                request.setAttribute("error", "Monto de pago inválido");
                request.getRequestDispatcher("/usuario/pago-cancelado.jsp").forward(request, response);
            }
            
        } catch (PayPalRESTException e) {
            LOGGER.error("Error de PayPal: {}", e.getMessage(), e);
            request.setAttribute("error", "Error en el servicio de PayPal: " + e.getMessage());
            request.getRequestDispatcher("/usuario/pago-cancelado.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.error("Error general: {}", e.getMessage(), e);
            request.setAttribute("error", "Error inesperado al procesar el pago");
            request.getRequestDispatcher("/usuario/pago-cancelado.jsp").forward(request, response);
        }
    }

    /**
     * Crea un pago en PayPal con los parámetros especificados.
     * 
     * @param total el monto del pago
     * @param currency la moneda del pago
     * @param cancelUrl la URL de cancelación del pago
     * @param successUrl la URL de éxito del pago
     * @return el pago creado en PayPal
     * @throws PayPalRESTException si ocurre un error durante la creación del pago
     */
    private Payment createPayment(double total, String currency, 
            String cancelUrl, String successUrl) throws PayPalRESTException {
                
            LOGGER.info("Creando pago - Total: {}, Moneda: {}", total, currency);
            total = Double.parseDouble(String.valueOf(total).replace(",", ".")); // Reemplazar la coma por un punto
            
            // Formatear el monto correctamente para PayPal usando Locale.US para asegurar el punto decimal
            String formattedAmount = String.format(Locale.US, "%.2f", total);
            LOGGER.info("Monto formateado: {}", formattedAmount);
            
            Amount amount = new Amount();
            amount.setCurrency(currency);
            amount.setTotal(formattedAmount);

            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setDescription("Alquiler de Sector en Gestión Almacén");

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);

            Payer payer = new Payer();
            payer.setPaymentMethod("paypal");

            Payment payment = new Payment();
            payment.setIntent("sale");
            payment.setPayer(payer);
            payment.setTransactions(transactions);

            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setCancelUrl(cancelUrl);
            redirectUrls.setReturnUrl(successUrl);
            
            payment.setRedirectUrls(redirectUrls);
            
            return payment.create(apiContext);
    }
}