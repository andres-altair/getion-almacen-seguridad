<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.andres.gestionalmacen.dtos.UsuarioDto" %>
<%@ page import="com.andres.gestionalmacen.utilidades.ImagenUtil" %>
<%@ page import="java.time.Year" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    // Verificar si los atributos necesarios están presentes
    if (request.getAttribute("usuarioNombre") == null || request.getAttribute("usuarioFoto") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/panel");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/img/favicon.png">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Panel de Usuario</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .plan-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 20px;
            margin: 10px;
            text-align: center;
            transition: transform 0.3s;
        }
        .plan-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        .plan-price {
            font-size: 2em;
            color: #007bff;
            margin: 15px 0;
        }
        .plan-features {
            list-style: none;
            padding: 0;
            margin: 15px 0;
        }
        .plan-features li {
            margin: 5px 0;
        }
        .ocupado {
            opacity: 0.5;
        }
    </style>
</head>
<body>
    <!-- Barra de Navegación -->
    <nav class="navbar">
        <div class="navbar-logo">
            <img src="${pageContext.request.contextPath}/img/logo.svg" alt="EnvioGo" class="img-fluid">
        </div>
        <div class="nav-links-container">
            <a href="${pageContext.request.contextPath}/admin/recepcion" class="nav-link">
                <i class="fas fa-users"></i> Registrar Recepción
            </a>
            <a href="${pageContext.request.contextPath}/admin/estado" class="nav-link">
                <i class="fas fa-database"></i> Actualizar Estado
            </a>
            <a href="${pageContext.request.contextPath}/admin/mantenimiento" class="nav-link">
                <i class="fas fa-exclamation-triangle"></i> Mantenimiento Almacén
            </a>
            <a href="${pageContext.request.contextPath}/admin/menu" class="nav-link">
                <i class="fas fa-bars"></i> Menú
            </a>
            <a href="${pageContext.request.contextPath}/cerrarSesion" class="nav-link">
                <i class="fas fa-sign-out-alt"></i> 
            </a>
        </div>
    </nav>

    <!-- Contenedor Principal -->
    <div class="panel-container container-fluid">
        <!-- Información de Usuario -->
        <div class="user-info row">
            <div class="col-12">
                <img src="${usuarioFoto}" class="img-perfil" alt="Foto de perfil">
                <span class="welcome-text">Bienvenido <span class="nombre-usuario">${usuarioNombre}</span></span><span><h2>Panel de Usuario</h2></span>
            </div>
        </div>

        <!-- Sección de Estadísticas -->
        <section class="panel-section">
            <div class="stats-container">
                <div class="stat-card">
                    <i class="fas fa-box panel-icon"></i>
                    <h3>Productos en Stock</h3>
                    <div class="value">150</div>
                    <div class="label">unidades</div>
                </div>
                <div class="stat-card">
                    <i class="fas fa-truck panel-icon"></i>
                    <h3>Envíos Pendientes</h3>
                    <div class="value">25</div>
                    <div class="label">pedidos</div>
                </div>
                <div class="stat-card">
                    <i class="fas fa-tasks panel-icon"></i>
                    <h3>Tareas Asignadas</h3>
                    <div class="value">8</div>
                    <div class="label">tareas</div>
                </div>
            </div>
        </section>

        <!-- Sección de Acciones -->
        <section class="panel-section">
            <div class="panel-grid">
                <div class="panel-card">
                    <i class="fas fa-clipboard-list panel-icon"></i>
                    <h2>Gestión de Inventario</h2>
                    <p>Administra el stock y ubicación de productos</p>
                    <a href="inventario" class="panel-button">
                        <i class="fas fa-boxes"></i>Ver Inventario
                    </a>
                </div>
                <div class="panel-card">
                    <i class="fas fa-shipping-fast panel-icon"></i>
                    <h2>Gestión de Envíos</h2>
                    <p>Controla los envíos y recepciones</p>
                    <a href="envios" class="panel-button">
                        <i class="fas fa-truck-loading"></i>Gestionar Envíos
                    </a>
                </div>
                <div class="panel-card">
                    <i class="fas fa-tasks panel-icon"></i>
                    <h2>Tareas Pendientes</h2>
                    <p>Visualiza y gestiona tus tareas asignadas</p>
                    <a href="tareas" class="panel-button">
                        <i class="fas fa-list-check"></i>Ver Tareas
                    </a>
                </div>
            </div>
        </section>

        <!-- Sección de alquiler de sectores -->
        <section class="panel-section">
            <div class="container mt-5">
                <h1 class="text-center mb-5">Alquiler de Sectores de Almacén</h1>
                
                <div class="row">
                    <!-- Sector A -->
                    <div class="col-md-4">
                        <div class="plan-card">
                            <h3>Sector A</h3>
                            <div class="plan-price">149.99€<small>/mes</small></div>
                            <ul class="plan-features">
                                <li><i class="fas fa-warehouse text-primary"></i> 50m² de espacio</li>
                                <li><i class="fas fa-temperature-low text-info"></i> Climatizado</li>
                                <li><i class="fas fa-shield-alt text-success"></i> Seguridad 24/7</li>
                                <li><i class="fas fa-truck text-warning"></i> Acceso para camiones</li>
                            </ul>
                            <div id="paypal-button-sectorA"></div>
                        </div>
                    </div>
                    
                    <!-- Sector B -->
                    <div class="col-md-4">
                        <div class="plan-card">
                            <h3>Sector B</h3>
                            <div class="plan-price">199.99€<small>/mes</small></div>
                            <ul class="plan-features">
                                <li><i class="fas fa-warehouse text-primary"></i> 75m² de espacio</li>
                                <li><i class="fas fa-temperature-low text-info"></i> Climatizado</li>
                                <li><i class="fas fa-shield-alt text-success"></i> Seguridad 24/7</li>
                                <li><i class="fas fa-dolly text-warning"></i> Equipo de carga incluido</li>
                            </ul>
                            <div id="paypal-button-sectorB"></div>
                        </div>
                    </div>
                    
                    <!-- Sector C -->
                    <div class="col-md-4">
                        <div class="plan-card">
                            <h3>Sector C</h3>
                            <div class="plan-price">299.99€<small>/mes</small></div>
                            <ul class="plan-features">
                                <li><i class="fas fa-warehouse text-primary"></i> 100m² de espacio</li>
                                <li><i class="fas fa-temperature-low text-info"></i> Control de temperatura</li>
                                <li><i class="fas fa-shield-alt text-success"></i> Seguridad avanzada</li>
                                <li><i class="fas fa-users text-warning"></i> Personal de apoyo</li>
                            </ul>
                            <div id="paypal-button-sectorC"></div>
                        </div>
                    </div>
                </div>

                <div class="row mt-4">
                    <!-- Sector D -->
                    <div class="col-md-4">
                        <div class="plan-card">
                            <h3>Sector D</h3>
                            <div class="plan-price">399.99€<small>/mes</small></div>
                            <ul class="plan-features">
                                <li><i class="fas fa-warehouse text-primary"></i> 150m² de espacio</li>
                                <li><i class="fas fa-temperature-low text-info"></i> Control ambiental total</li>
                                <li><i class="fas fa-shield-alt text-success"></i> Seguridad biométrica</li>
                                <li><i class="fas fa-clock text-warning"></i> Acceso 24/7</li>
                            </ul>
                            <div id="paypal-button-sectorD"></div>
                        </div>
                    </div>
                    
                    <!-- Sector E -->
                    <div class="col-md-4">
                        <div class="plan-card">
                            <h3>Sector E</h3>
                            <div class="plan-price">499.99€<small>/mes</small></div>
                            <ul class="plan-features">
                                <li><i class="fas fa-warehouse text-primary"></i> 200m² de espacio</li>
                                <li><i class="fas fa-temperature-low text-info"></i> Zona refrigerada</li>
                                <li><i class="fas fa-shield-alt text-success"></i> Vigilancia dedicada</li>
                                <li><i class="fas fa-robot text-warning"></i> Sistema automatizado</li>
                            </ul>
                            <div id="paypal-button-sectorE"></div>
                        </div>
                    </div>
                    
                    <!-- Sector F -->
                    <div class="col-md-4">
                        <div class="plan-card">
                            <h3>Sector F</h3>
                            <div class="plan-price">699.99€<small>/mes</small></div>
                            <ul class="plan-features">
                                <li><i class="fas fa-warehouse text-primary"></i> 300m² de espacio</li>
                                <li><i class="fas fa-temperature-low text-info"></i> Control climático premium</li>
                                <li><i class="fas fa-shield-alt text-success"></i> Centro de control dedicado</li>
                                <li><i class="fas fa-star text-warning"></i> Servicios VIP</li>
                            </ul>
                            <div id="paypal-button-sectorF"></div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>

    <!-- Footer -->
    <footer class="footer">
        <p>&copy; <%= Year.now() %> EnvioGo - Todos los derechos reservados</p>
    </footer>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <!-- PayPal JS SDK -->
    <script src="https://www.paypal.com/sdk/js?client-id=YOUR_PAYPAL_CLIENT_ID&currency=EUR"></script>
    <script>
        // Configuración de la API
        const API_URL = 'http://localhost:8081/api';
        
        // Función para manejar errores de fetch
        async function handleFetchResponse(response) {
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error HTTP: ${response.status} - ${errorText}`);
            }
            return response.json();
        }
        
        // Función para cargar el estado de los sectores
        async function cargarEstadoSectores() {
            try {
                const response = await fetch(`${API_URL}/sectores`);
                const sectores = await handleFetchResponse(response);
                
                sectores.forEach(sector => {
                    const button = document.querySelector(`#paypal-button-sector${sector.nombre}`);
                    if (!button) return;
                    
                    const card = button.closest('.plan-card');
                    const priceElement = card.querySelector('.plan-price');
                    
                    // Actualizar precio si es diferente
                    const displayedPrice = priceElement.textContent.replace('€/mes', '').trim();
                    if (sector.precioMensual !== displayedPrice) {
                        priceElement.textContent = `${sector.precioMensual}€/mes`;
                    }
                    
                    if (!sector.disponible) {
                        button.style.display = 'none';
                        card.classList.add('ocupado');
                        if (!card.querySelector('.text-danger')) {
                            priceElement.insertAdjacentHTML('afterend', 
                                '<div class="text-danger mt-2">No disponible</div>');
                        }
                    } else {
                        button.style.display = 'block';
                        card.classList.remove('ocupado');
                        const noDisponible = card.querySelector('.text-danger');
                        if (noDisponible) noDisponible.remove();
                    }
                });
            } catch (error) {
                console.error('Error al cargar estado de sectores:', error);
                mostrarError('Error al cargar el estado de los sectores. Por favor, recarga la página.');
            }
        }

        // Función para mostrar mensajes de error
        function mostrarError(mensaje) {
            const errorDiv = document.createElement('div');
            errorDiv.className = 'alert alert-danger alert-dismissible fade show';
            errorDiv.innerHTML = `
                ${mensaje}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            `;
            document.querySelector('.container').prepend(errorDiv);
        }

        // Cargar estado inicial y configurar actualización periódica
        document.addEventListener('DOMContentLoaded', () => {
            cargarEstadoSectores();
            // Actualizar cada 30 segundos
            setInterval(cargarEstadoSectores, 30000);
        });

        // Función para crear botón de PayPal
        function createPayPalButton(containerId, sectorName, price) {
            paypal.Buttons({
                createOrder: function(data, actions) {
                    return actions.order.create({
                        purchase_units: [{
                            description: `Alquiler Sector ${sectorName} - 1 mes`,
                            amount: {
                                currency_code: 'EUR',
                                value: price
                            }
                        }]
                    });
                },
                onApprove: async function(data, actions) {
                    try {
                        const orderData = await actions.order.capture();
                        
                        const response = await fetch('${pageContext.request.contextPath}/usuario/procesarPago', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify({
                                orderId: orderData.id,
                                sectorName: sectorName,
                                amount: price,
                                type: 'SECTOR_RENTAL'
                            })
                        });
                        
                        const result = await handleFetchResponse(response);
                        
                        if (result.success) {
                            alert('¡Gracias por tu alquiler! El Sector ' + sectorName + ' ha sido reservado.');
                            window.location.reload();
                        } else {
                            mostrarError('Error al procesar el pago: ' + result.error);
                        }
                    } catch (error) {
                        console.error('Error:', error);
                        mostrarError('Error al procesar el pago. Por favor, inténtalo de nuevo.');
                    }
                },
                onError: function(err) {
                    console.error('Error PayPal:', err);
                    mostrarError('Error en el proceso de pago con PayPal. Por favor, inténtalo de nuevo.');
                }
            }).render('#' + containerId);
        }

        // Crear botones de PayPal para cada sector
        createPayPalButton('paypal-button-sectorA', 'A', '149.99');
        createPayPalButton('paypal-button-sectorB', 'B', '199.99');
        createPayPalButton('paypal-button-sectorC', 'C', '299.99');
        createPayPalButton('paypal-button-sectorD', 'D', '399.99');
        createPayPalButton('paypal-button-sectorE', 'E', '499.99');
        createPayPalButton('paypal-button-sectorF', 'F', '699.99');
    </script>
</body>
</html>