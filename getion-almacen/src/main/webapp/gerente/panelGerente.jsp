<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.andres.gestionalmacen.dtos.UsuarioDto" %>
<%@ page import="com.andres.gestionalmacen.utilidades.ImagenUtil" %>
<%@ page import="java.time.Year" %>
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
    <link rel="icon" type="image/png" href="img/favicon.png">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Panel de Gerente</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome para iconos -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <!-- Estilos propios -->
    <link rel="stylesheet" href="../css/global.css">
    <link rel="stylesheet" href="../css/components.css">
    <link rel="stylesheet" href="../css/layout.css">
</head>
<body>
    <!-- Barra de Navegación -->
    <nav class="navbar">
        <div class="navbar-logo">
            <img src="../img/logo.svg" alt="EnvioGo" class="img-fluid" height="40">
        </div>
        <span class="welcome-text">
            Bienvenido, <span class="nombre-usuario">${usuarioNombre}</span>
        </span>
        <div class="navbar-nav">
            <a href="../cerrar-sesion" class="nav-link">
                <i class="fas fa-sign-out-alt"></i> Cerrar Sesión
            </a>
        </div>
    </nav>

    <!-- Contenedor Principal -->
    <div class="panel-container">
        <!-- Sección de Estadísticas -->
        <section class="panel-section">
            <div class="stats-container">
                <div class="stat-card">
                    <i class="fas fa-chart-line panel-icon"></i>
                    <h3>Ventas Totales</h3>
                    <div class="value">€25.4K</div>
                    <div class="label">este mes</div>
                </div>
                <div class="stat-card">
                    <i class="fas fa-box panel-icon"></i>
                    <h3>Inventario Total</h3>
                    <div class="value">1,250</div>
                    <div class="label">productos</div>
                </div>
                <div class="stat-card">
                    <i class="fas fa-users panel-icon"></i>
                    <h3>Personal Activo</h3>
                    <div class="value">15</div>
                    <div class="label">empleados</div>
                </div>
            </div>
        </section>

        <!-- Sección de Acciones -->
        <section class="panel-section">
            <div class="panel-grid">
                <div class="panel-card">
                    <i class="fas fa-chart-bar panel-icon"></i>
                    <h2>Informes y Análisis</h2>
                    <p>Visualiza informes detallados y análisis de rendimiento</p>
                    <a href="informes" class="panel-button">
                        <i class="fas fa-file-alt"></i>Ver Informes
                    </a>
                </div>
                <div class="panel-card">
                    <i class="fas fa-warehouse panel-icon"></i>
                    <h2>Gestión de Almacén</h2>
                    <p>Supervisa las operaciones y el inventario</p>
                    <a href="almacen" class="panel-button">
                        <i class="fas fa-dolly"></i>Gestionar Almacén
                    </a>
                </div>
                <div class="panel-card">
                    <i class="fas fa-user-cog panel-icon"></i>
                    <h2>Gestión de Personal</h2>
                    <p>Administra el personal y asigna tareas</p>
                    <a href="personal" class="panel-button">
                        <i class="fas fa-users-cog"></i>Gestionar Personal
                    </a>
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