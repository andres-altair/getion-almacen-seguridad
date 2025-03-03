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
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/img/favicon.png">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Panel de Administrador</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome para iconos -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
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
            <a href="${pageContext.request.contextPath}/admin/usuarios" class="nav-link">
                <i class="fas fa-users"></i> Gestión de Usuarios
            </a>
            <a href="${pageContext.request.contextPath}/admin/backup" class="nav-link">
                <i class="fas fa-database"></i> Backup
            </a>
            <a href="${pageContext.request.contextPath}/admin/incidencias" class="nav-link">
                <i class="fas fa-exclamation-triangle"></i> Incidencia
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
        </div>
        <!-- Sección de Estadísticas -->
        <section class="panel-section">
            <div class="stats-container row">
                <div class="col-lg-4 col-md-6 col-12">
                    <div class="stat-card">
                        <i class="fas fa-users panel-icon"></i>
                        <h3>Usuarios Activos</h3>
                        <div class="value">124</div>
                        <div class="label">usuarios</div>
                    </div>
                </div>
                <div class="col-lg-4 col-md-6 col-12">
                    <div class="stat-card">
                        <i class="fas fa-shield-alt panel-icon"></i>
                        <h3>Roles Definidos</h3>
                        <div class="value">5</div>
                        <div class="label">roles</div>
                    </div>
                </div>
                <div class="col-lg-4 col-md-6 col-12">
                    <div class="stat-card">
                        <i class="fas fa-key panel-icon"></i>
                        <h3>Permisos Totales</h3>
                        <div class="value">25</div>
                        <div class="label">permisos</div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Sección de Acciones -->
        <section class="panel-section">
            <div class="panel-grid row">
                <div class="col-lg-4 col-md-6 col-12">
                    <div class="panel-card">
                        <i class="fas fa-user-plus panel-icon"></i>
                        <h2>Gestión de Usuarios</h2>
                        <p>Administra usuarios, roles y permisos del sistema</p>
                        <a href="${pageContext.request.contextPath}/admin/usuarios" class="panel-button">
                            <i class="fas fa-users"></i> Gestionar Usuarios
                        </a>
                    </div>
                </div>
                <div class="col-lg-4 col-md-6 col-12">
                    <div class="panel-card">
                        <i class="fas fa-shield-alt panel-icon"></i>
                        <h2>Seguridad del Sistema</h2>
                        <p>Configura la seguridad y accesos del sistema</p>
                        <a href="${pageContext.request.contextPath}/admin/seguridad" class="panel-button">
                            <i class="fas fa-lock"></i> Gestionar Seguridad
                        </a>
                    </div>
                </div>
                <div class="col-lg-4 col-md-6 col-12">
                    <div class="panel-card">
                        <i class="fas fa-cogs panel-icon"></i>
                        <h2>Configuración</h2>
                        <p>Ajusta la configuración general del sistema</p>
                        <a href="${pageContext.request.contextPath}/admin/configuracion" class="panel-button">
                            <i class="fas fa-cog"></i> Configurar Sistema
                        </a>
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