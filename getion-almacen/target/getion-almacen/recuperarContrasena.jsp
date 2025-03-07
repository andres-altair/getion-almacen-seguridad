<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.time.Year"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/img/favicon.png">
    <title>Sistema de Gestión de Almacén - Recuperar Contraseña</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome para iconos -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/components.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/forms.css">
    <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
</head>
<body class="d-flex flex-column min-vh-100">
    <div class="container my-5">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card shadow">
                    <div class="card-body p-4">
                        <h3 class="text-center mb-4">Recuperar Contraseña</h3>
                        
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger" role="alert">
                                ${error}
                            </div>
                        </c:if>
                        
                        <form action="recuperarContrasena" method="post" class="needs-validation" novalidate>
                            <div class="form-group mb-3">
                                <label for="correoElectronico" class="form-label">Correo Electrónico:</label>
                                <div class="input-group">
                                    <span class="input-group-text">
                                        <i class="fas fa-envelope"></i>
                                    </span>
                                    <input type="email" 
                                           id="correoElectronico"
                                           name="correoElectronico"
                                           class="form-control" 
                                           required
                                           placeholder="Ingresa tu correo electrónico">
                                    <div class="invalid-feedback">
                                        Por favor, ingresa un correo electrónico válido.
                                    </div>
                                </div>
                            </div>
                            
                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-paper-plane me-2"></i>Enviar Enlace de Recuperación
                                </button>
                                <a href="${pageContext.request.contextPath}/acceso" class="btn btn-outline-secondary">
                                    <i class="fas fa-arrow-left me-2"></i>Volver al Inicio de Sesión
                                </a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="container-fluid py-3 text-center mt-auto">
        <p>&copy; <%=Year.now()%> Sistema de Gestión de Almacén - Todos los derechos reservados</p>
    </footer>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Script de validación -->
    <script src="${pageContext.request.contextPath}/js/recuperarContrasena.js"></script>
</body>
</html>