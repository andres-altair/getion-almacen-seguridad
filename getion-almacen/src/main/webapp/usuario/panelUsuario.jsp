<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.andres.gestionalmacen.dtos.UsuarioDto" %>
<%@ page import="com.andres.gestionalmacen.utilidades.ImagenUtil" %>
<%@ page import="java.time.Year" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    // Verificar si hay una sesión válida
    if (session == null || session.getAttribute("usuario") == null) {
        response.sendRedirect(request.getContextPath() + "/acceso");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/img/favicon.png">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Panel de Usuario</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <!-- Estilos propios -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/components.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css">
    <style>
        .sector-card {
            background: #fff;
            border-radius: 15px;
            padding: 2rem;
            margin: 1rem 0;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            transition: transform 0.3s ease;
        }
        .sector-card:hover {
            transform: translateY(-5px);
        }
        .sector-price {
            font-size: 2.5rem;
            color: var(--primary-color);
            font-weight: bold;
            margin: 1rem 0;
        }
        .sector-features {
            list-style: none;
            padding: 0;
            margin: 1.5rem 0;
        }
        .sector-features li {
            margin: 0.8rem 0;
            color: var(--text-color);
        }
        .sector-features i {
            margin-right: 0.5rem;
            color: var(--primary-color);
        }
        .payment-button {
            width: 100%;
            padding: 1rem;
            border-radius: 8px;
            background: var(--primary-color);
            color: white;
            border: none;
            font-weight: 600;
            transition: background 0.3s ease;
        }
        .payment-button:hover {
            background: var(--primary-dark);
        }
        .panel-section {
            margin-bottom: 2rem;
        }
        .stats-container {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 1.5rem;
            margin: 2rem 0;
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
            <a href="${pageContext.request.contextPath}/usuario/alquileres" class="nav-link">
                <i class="fas fa-warehouse"></i> Mis Alquileres
            </a>
            <a href="${pageContext.request.contextPath}/usuario/perfil" class="nav-link">
                <i class="fas fa-user"></i> Mi Perfil
            </a>
            <a href="${pageContext.request.contextPath}/usuario/ayuda" class="nav-link">
                <i class="fas fa-question-circle"></i> Ayuda
            </a>
            <a href="${pageContext.request.contextPath}/cerrarSesion" class="nav-link">
                <i class="fas fa-sign-out-alt"></i> Salir
            </a>
        </div>
    </nav>

    <!-- Contenedor Principal -->
    <div class="panel-container container-fluid">
        <!-- Información de Usuario -->
        <div class="user-info row">
            <div class="col-12">
                <img src="${usuarioFoto}" class="img-perfil" alt="Foto de perfil">
                <span class="welcome-text">Bienvenido <span class="nombre-usuario">${usuarioNombre}</span></span>
            </div>
        </div>

        <!-- Sección de Estadísticas -->
        <section class="panel-section">
            <div class="stats-container">
                <div class="stat-card">
                    <i class="fas fa-warehouse panel-icon"></i>
                    <h3>Sectores Alquilados</h3>
                    <div class="value">2</div>
                    <div class="label">sectores</div>
                </div>
                <div class="stat-card">
                    <i class="fas fa-box panel-icon"></i>
                    <h3>Espacio Total</h3>
                    <div class="value">150</div>
                    <div class="label">m²</div>
                </div>
                <div class="stat-card">
                    <i class="fas fa-calendar-alt panel-icon"></i>
                    <h3>Días Restantes</h3>
                    <div class="value">25</div>
                    <div class="label">días</div>
                </div>
            </div>
        </section>

        <!-- Sección de Alquiler de Sectores -->
        <section class="panel-section">
            <h2 class="section-title">Alquiler de Sectores</h2>
            <div class="row">
                <!-- Sector A -->
                <div class="col-lg-3 col-md-6">
                    <div class="sector-card">
                        <h3>Sector A</h3>
                        <div class="sector-price">149.99€<small>/mes</small></div>
                        <ul class="sector-features">
                            <li><i class="fas fa-warehouse"></i> 50m² de espacio</li>
                            <li><i class="fas fa-temperature-low"></i> Climatizado</li>
                            <li><i class="fas fa-shield-alt"></i> Seguridad 24/7</li>
                            <li><i class="fas fa-truck"></i> Acceso para camiones</li>
                        </ul>
                        <form action="${pageContext.request.contextPath}/usuario/procesarPago" method="post">
                            <input type="hidden" name="sectorId" value="1">
                            <input type="hidden" name="total" value="149.99">
                            <input type="hidden" name="sectorName" value="Sector A">
                            <button type="submit" class="payment-button">
                                <i class="fab fa-paypal me-2"></i> Pagar con PayPal
                            </button>
                        </form>
                    </div>
                </div>

                <!-- Sector B -->
                <div class="col-lg-3 col-md-6">
                    <div class="sector-card">
                        <h3>Sector B</h3>
                        <div class="sector-price">199.99€<small>/mes</small></div>
                        <ul class="sector-features">
                            <li><i class="fas fa-warehouse"></i> 75m² de espacio</li>
                            <li><i class="fas fa-temperature-low"></i> Climatizado</li>
                            <li><i class="fas fa-shield-alt"></i> Seguridad 24/7</li>
                            <li><i class="fas fa-dolly"></i> Equipo de carga incluido</li>
                        </ul>
                        <form action="${pageContext.request.contextPath}/usuario/procesarPago" method="post">
                            <input type="hidden" name="sectorId" value="2">
                            <input type="hidden" name="total" value="199.99">
                            <input type="hidden" name="sectorName" value="Sector B">
                            <button type="submit" class="payment-button">
                                <i class="fab fa-paypal me-2"></i> Pagar con PayPal
                            </button>
                        </form>
                    </div>
                </div>

                <!-- Sector C -->
                <div class="col-lg-3 col-md-6">
                    <div class="sector-card">
                        <h3>Sector C</h3>
                        <div class="sector-price">299.99€<small>/mes</small></div>
                        <ul class="sector-features">
                            <li><i class="fas fa-warehouse"></i> 100m² de espacio</li>
                            <li><i class="fas fa-temperature-low"></i> Climatizado</li>
                            <li><i class="fas fa-shield-alt"></i> Seguridad 24/7</li>
                            <li><i class="fas fa-users"></i> Personal de apoyo</li>
                        </ul>
                        <form action="${pageContext.request.contextPath}/usuario/procesarPago" method="post">
                            <input type="hidden" name="sectorId" value="3">
                            <input type="hidden" name="total" value="299.99">
                            <input type="hidden" name="sectorName" value="Sector C">
                            <button type="submit" class="payment-button">
                                <i class="fab fa-paypal me-2"></i> Pagar con PayPal
                            </button>
                        </form>
                    </div>
                </div>

                <!-- Sector D -->
                <div class="col-lg-3 col-md-6">
                    <div class="sector-card">
                        <h3>Sector D</h3>
                        <div class="sector-price">399.99€<small>/mes</small></div>
                        <ul class="sector-features">
                            <li><i class="fas fa-warehouse"></i> 150m² de espacio</li>
                            <li><i class="fas fa-temperature-low"></i> Climatizado</li>
                            <li><i class="fas fa-shield-alt"></i> Seguridad 24/7</li>
                            <li><i class="fas fa-star"></i> Servicios premium</li>
                        </ul>
                        <form action="${pageContext.request.contextPath}/usuario/procesarPago" method="post">
                            <input type="hidden" name="sectorId" value="4">
                            <input type="hidden" name="total" value="399.99">
                            <input type="hidden" name="sectorName" value="Sector D">
                            <button type="submit" class="payment-button">
                                <i class="fab fa-paypal me-2"></i> Pagar con PayPal
                            </button>
                        </form>
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
</body>
</html>