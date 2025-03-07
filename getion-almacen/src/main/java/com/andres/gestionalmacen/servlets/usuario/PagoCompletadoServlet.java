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

@WebServlet("/usuario/completarPago")
public class PagoCompletadoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(PagoCompletadoServlet.class);
    private APIContext apiContext;

    @Override
    public void init() throws ServletException {
        String clientId = getServletContext().getInitParameter("paypal.client.id");
        String clientSecret = getServletContext().getInitParameter("paypal.client.secret");
        String mode = getServletContext().getInitParameter("paypal.mode");
        apiContext = new APIContext(clientId, clientSecret, mode);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuario") == null) {
                LOGGER.error("Intento de completar pago sin sesión válida");
                response.sendRedirect(request.getContextPath() + "/acceso");
                return;
            }

            String paymentId = request.getParameter("paymentId");
            String payerId = request.getParameter("PayerID");
            String storedPaymentId = (String) session.getAttribute("paymentId");
            String sectorId = (String) session.getAttribute("sectorId");
            String sectorName = (String) session.getAttribute("sectorName");

            if (paymentId == null || payerId == null || storedPaymentId == null || 
                !paymentId.equals(storedPaymentId) || sectorId == null) {
                LOGGER.error("Parámetros de pago inválidos o faltantes");
                response.sendRedirect(request.getContextPath() + "/usuario/pago-cancelado");
                return;
            }

            Payment payment = new Payment();
            payment.setId(paymentId);

            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);

            // Ejecutar el pago
            Payment executedPayment = payment.execute(apiContext, paymentExecution);

            if ("approved".equals(executedPayment.getState().toLowerCase())) {
                LOGGER.info("Pago completado exitosamente. PaymentId: {}, Sector: {}", paymentId, sectorName);
                
                // Aquí deberías actualizar tu base de datos para registrar el pago y asignar el sector
                // TODO: Implementar la lógica de actualización de la base de datos
                
                // Limpiar atributos de sesión relacionados con el pago
                session.removeAttribute("paymentId");
                session.removeAttribute("sectorId");
                session.removeAttribute("sectorName");
                
                request.getRequestDispatcher("/usuario/pago-completado.jsp").forward(request, response);
            } else {
                LOGGER.error("Estado de pago no aprobado: {}", executedPayment.getState());
                response.sendRedirect(request.getContextPath() + "/usuario/pago-cancelado");
            }

        } catch (PayPalRESTException e) {
            LOGGER.error("Error procesando pago con PayPal", e);
            response.sendRedirect(request.getContextPath() + "/usuario/pago-cancelado");
        } catch (Exception e) {
            LOGGER.error("Error general completando pago", e);
            response.sendRedirect(request.getContextPath() + "/usuario/pago-cancelado");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Redirigir POST requests al método GET
        doGet(request, response);
    }
}