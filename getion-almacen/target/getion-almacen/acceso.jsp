<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.time.Year" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="google-signin-client_id" content="478375949160-lf7nntvl7hnohvdrt2rjct7miph9n2k3.apps.googleusercontent.com">
    <meta name="context-path" content="${pageContext.request.contextPath}">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/img/favicon.png">
    <title>Sistema de Gestión de Almacén - Iniciar Sesión</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome para iconos -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/components.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/forms.css">
    <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
    <!-- Script de Google -->
    <script src="https://accounts.google.com/gsi/client" async defer></script>
</head>
<body>
    <!-- Barra de Navegación -->
    <nav class="navbar navbar-expand-lg">
        <div class="container-fluid">
            <div class="navbar-logo">
                <img src="${pageContext.request.contextPath}/img/logo.svg" alt="EnvioGo" class="img-fluid">
            </div>
            <a class="navbar-brand" href="${pageContext.request.contextPath}/acceso">Iniciar Sesión</a>
            <a href="${pageContext.request.contextPath}/inicio" class="btn home-btn ms-auto">
                <i class="fas fa-home"></i> Inicio
            </a>
        </div>
    </nav>

    <!-- Contenedor Principal -->
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-12 col-md-8 col-lg-6">
                <div class="card shadow-sm my-5">
                    <div class="card-body p-4">
                        <form action="${pageContext.request.contextPath}/acceso" method="post">
                            <div class="mb-3">
                                <label for="correoElectronico" class="form-label">Correo Electrónico</label>
                                <input type="email" class="form-control" name="correoElectronico" id="correoElectronico" 
                                       value="${requestScope.correoElectronico}" required>
                            </div>
                            <div class="mb-3">
                                <label for="contrasena" class="form-label">Contraseña</label>
                                <div class="input-group">
                                    <input type="password" class="form-control" name="contrasena" id="contrasena" required>
                                    <button class="btn btn-outline-secondary" type="button" id="togglePasswordCrear" onclick="alternarContrasena('contrasena', this)">
                                        <i class="fas fa-eye" id="eyeIconCrear"></i>
                                    </button>
                                </div>
                            </div>
                            <div class="mb-3 form-check">
                                <input type="checkbox" class="form-check-input" id="recordar" name="recordar">
                                <label class="form-check-label" for="recordar">Recordar sesión</label>
                            </div>
                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary">Iniciar Sesión</button>
                                <!-- Botón de Google -->
                                <div id="g_id_onload"
                                     data-client_id="478375949160-lf7nntvl7hnohvdrt2rjct7miph9n2k3.apps.googleusercontent.com"
                                     data-context="signin"
                                     data-callback="manejarRespuestaGoogle"
                                     data-auto_prompt="false">
                                </div>
                                <div class="g_id_signin"
                                     data-type="standard"
                                     data-size="large"
                                     data-theme="outline"
                                     data-text="sign_in_with"
                                     data-shape="rectangular"
                                     data-logo_alignment="left"
                                     data-width="100%">
                                </div>
                            </div>
                        </form>
                        <div class="links">
                            <a href="${pageContext.request.contextPath}/recuperarContrasena">¿Olvidaste tu contraseña?</a>
                            <a href="${pageContext.request.contextPath}/reenviarConfirmacion">¿No recibiste el correo de confirmación?</a>
                            <a href="${pageContext.request.contextPath}/registro">¿No tienes una cuenta?</a>
                        </div>

                        <c:if test="${not empty sessionScope.mensaje}">
                            <div class="alert alert-success mt-3" role="alert">
                                ${sessionScope.mensaje}
                                <% session.removeAttribute("mensaje"); %>
                            </div>
                        </c:if>

                        <c:if test="${not empty sessionScope.error}">
                            <div class="alert alert-danger mt-3" role="alert">
                                ${sessionScope.error}
                                <% session.removeAttribute("error"); %>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="container-fluid py-3 text-center mt-auto">
        <p>&copy; <%= Year.now() %> EnvioGo - Todos los derechos reservados</p>
    </footer>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Custom JS -->
    <script src="${pageContext.request.contextPath}/js/acceso.js"></script>
</body>
</html>