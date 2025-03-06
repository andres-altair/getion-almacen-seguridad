<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.time.Year" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/img/favicon.png">
    <title>Sistema de Gestión de Almacén - Registro</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome para iconos -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/components.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/forms.css">
    <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
    <!-- Google Sign-In API -->
    <script src="https://accounts.google.com/gsi/client" async defer></script>
    <style>
        .separator {
            display: flex;
            align-items: center;
            text-align: center;
            margin: 20px 0;
        }
        
        .separator::before,
        .separator::after {
            content: '';
            flex: 1;
            border-bottom: 1px solid #dee2e6;
        }
        
        .separator span {
            padding: 0 10px;
            color: #6c757d;
            font-size: 0.9rem;
        }
    </style>
</head>
<body>
    <!-- Barra de Navegación -->
    <nav class="navbar navbar-expand-lg">
        <div class="container-fluid">
            <div class="navbar-logo">
                <img src="${pageContext.request.contextPath}/img/logo.svg" alt="EnvioGo" class="img-fluid">
            </div>
            <a class="navbar-brand" href="${pageContext.request.contextPath}/acceso">Registro</a>
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
                        <h4 class="text-center mb-4">Registro de Usuario</h4>
                        
                        <!-- Registro con Google -->
                        <div class="text-center mb-4">
                            <div id="g_id_onload"
                                 data-client_id="478375949160-lf7nntvl7hnohvdrt2rjct7miph9n2k3.apps.googleusercontent.com"
                                 data-context="signup"
                                 data-ux_mode="popup"
                                 data-callback="handleCredentialResponse"
                                 data-auto_prompt="false">
                            </div>
                            <div class="g_id_signin"
                                 data-type="standard"
                                 data-shape="rectangular"
                                 data-theme="outline"
                                 data-text="signup_with"
                                 data-size="large"
                                 data-logo_alignment="left"
                                 data-width="100%">
                            </div>
                        </div>

                        <div class="separator">
                            <span>O regístrate con tu correo</span>
                        </div>

                        <!-- Formulario de registro tradicional -->
                        <form action="${pageContext.request.contextPath}/registro" method="post" enctype="multipart/form-data">
                            <div class="mb-3">
                                <label for="nombreCompleto" class="form-label">Nombre completo</label>
                                <input type="text" class="form-control" name="nombreCompleto" id="nombreCompleto" 
                                       value="${requestScope.nombreCompleto}" required>
                            </div>
                            <div class="mb-3">
                                <label for="movil" class="form-label">Móvil (Opcional)</label>
                                <input type="tel" class="form-control" name="movil" id="movil" 
                                       value="${requestScope.movil}" pattern="[0-9]{9,15}"
                                       maxlength="15"
                                       placeholder="Ej: 612345678 +34612345678"
                                       title="Introduce un número de móvil válido (entre 9 y 15 dígitos)">
                                <div class="form-text">Formato: 612345678 (entre 9 y 15 dígitos)</div>
                            </div>
                            <div class="mb-3">
                                <label for="correoElectronico" class="form-label">Correo Electrónico</label>
                                <input type="email" class="form-control" name="correoElectronico" id="correoElectronico" 
                                       value="${requestScope.correoElectronico}" required>
                            </div>
                            <div class="mb-3">
                                <label for="contrasena" class="form-label">Contraseña</label>
                                <div class="input-group">
                                    <input type="password" class="form-control" name="contrasena" id="contrasena" required>
                                    <button class="btn btn-outline-secondary" type="button" id="togglePasswordCrear" onclick="verContasena('contrasena', this)">
                                        <i class="fas fa-eye" id="eyeIconCrear"></i>
                                    </button>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="foto" class="form-label">Foto</label>
                                <input type="file" class="form-control" name="foto" id="foto" accept="image/*" onchange="mostrarVistaPrevia(event)">
                                <!-- Vista previa de la imagen -->
                                <div class="mt-3 text-center">
                                    <img id="vistaPrevia" src="#" alt="Vista previa de la imagen" style="max-width: 100%; max-height: 200px; display: none;">
                                </div>
                            </div>
                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary">Registrar</button>
                            </div>
                        </form>

                        <c:if test="${not empty requestScope.error}">
                            <div class="alert alert-danger mt-3" role="alert">
                                ${requestScope.error}
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
    <script src="${pageContext.request.contextPath}/js/registro.js"></script>
    
    <script>
        function handleCredentialResponse(response) {
            fetch('${pageContext.request.contextPath}/GoogleRegistroServlet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'idToken=' + response.credential
            })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => {
                        if (text.includes("Ya existe una cuenta de Google")) {
                            // Si ya existe cuenta de Google, redirigir al login
                            window.location.href = '${pageContext.request.contextPath}/acceso';
                        } else {
                            throw new Error(text);
                        }
                    });
                }
                return response.text();
            })
            .then(data => {
                if (data === "Registro exitoso") {
                    window.location.href = '${pageContext.request.contextPath}/inicio';
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert(error.message || 'Error al registrarse con Google');
            });
        }
    </script>
</body>
</html>