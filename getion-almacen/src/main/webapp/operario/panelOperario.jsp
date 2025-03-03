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
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/img/favicon.png">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Panel de Operario</title>
    <!-- FontAwesome para iconos -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
     <!-- Estilos propios -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/components.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css">
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
            <span class="welcome-text">Bienvenido <span class="nombre-usuario">${usuarioNombre}</span></span>
        </div>
    </div>        <!-- Sección de Estadísticas -->
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
        </div>

     <!-- Footer -->
    <footer class="footer">
        <p>&copy; <%= Year.now() %> EnvioGo - Todos los derechos reservados</p>
    </footer>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>