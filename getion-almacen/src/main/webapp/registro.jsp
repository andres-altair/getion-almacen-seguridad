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
</head>
<body class="d-flex flex-column min-vh-100">
    <div class="container my-5">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card shadow">
                    <div class="card-body p-4">
                        <h3 class="text-center mb-4">Registro de Usuario</h3>
                        
                        <c:if test="${not empty sessionScope.error}">
                            <div class="alert alert-danger" role="alert">
                                ${sessionScope.error}
                                <% session.removeAttribute("error"); %>
                            </div>
                        </c:if>
                        
                        <form action="registro" method="post" enctype="multipart/form-data" 
                              class="needs-validation" novalidate id="formularioRegistro">
                            <!-- Nombre Completo -->
                            <div class="form-group mb-3">
                                <label for="nombreCompleto" class="form-label">Nombre Completo:</label>
                                <div class="input-group">
                                    <span class="input-group-text">
                                        <i class="fas fa-user"></i>
                                    </span>
                                    <input type="text" 
                                           id="nombreCompleto"
                                           name="nombreCompleto"
                                           class="form-control" 
                                           required
                                           minlength="3"
                                           maxlength="100"
                                           placeholder="Ingresa tu nombre completo"
                                           value="${sessionScope.nombreCompleto}">
                                    <% session.removeAttribute("nombreCompleto"); %>
                                    <div class="invalid-feedback">
                                        El nombre debe tener entre 3 y 100 caracteres.
                                    </div>
                                </div>
                            </div>

                            <!-- Correo Electrónico -->
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
                                           placeholder="Ingresa tu correo electrónico"
                                           value="${sessionScope.correoElectronico}">
                                    <% session.removeAttribute("correoElectronico"); %>
                                    <div class="invalid-feedback">
                                        Por favor, ingresa un correo electrónico válido.
                                    </div>
                                </div>
                            </div>

                            <!-- Móvil -->
                            <div class="form-group mb-3">
                                <label for="movil" class="form-label">Teléfono Móvil:</label>
                                <div class="input-group">
                                    <span class="input-group-text">
                                        <i class="fas fa-mobile-alt"></i>
                                    </span>
                                    <input type="tel" 
                                           id="movil"
                                           name="movil"
                                           class="form-control" 
                                           required
                                           pattern="[0-9]{9}"
                                           placeholder="Ingresa tu número móvil"
                                           value="${sessionScope.movil}">
                                    <% session.removeAttribute("movil"); %>
                                    <div class="invalid-feedback">
                                        Por favor, ingresa un número móvil válido (9 dígitos).
                                    </div>
                                </div>
                            </div>

                            <!-- Contraseña -->
                            <div class="form-group mb-3">
                                <label for="contrasena" class="form-label">Contraseña:</label>
                                <div class="input-group">
                                    <span class="input-group-text">
                                        <i class="fas fa-lock"></i>
                                    </span>
                                    <input type="password" 
                                           id="contrasena"
                                           name="contrasena"
                                           class="form-control" 
                                           required
                                           minlength="8"
                                           placeholder="Ingresa tu contraseña">
                                    <button class="btn btn-outline-secondary" type="button" id="mostrarContrasena">
                                        <i class="fas fa-eye"></i>
                                    </button>
                                    <div class="invalid-feedback">
                                        La contraseña debe tener al menos 8 caracteres.
                                    </div>
                                </div>
                            </div>

                            <!-- Confirmar Contraseña -->
                            <div class="form-group mb-3">
                                <label for="confirmarContrasena" class="form-label">Confirmar Contraseña:</label>
                                <div class="input-group">
                                    <span class="input-group-text">
                                        <i class="fas fa-lock"></i>
                                    </span>
                                    <input type="password" 
                                           id="confirmarContrasena"
                                           class="form-control" 
                                           required
                                           minlength="8"
                                           placeholder="Confirma tu contraseña">
                                    <button class="btn btn-outline-secondary" type="button" id="mostrarConfirmarContrasena">
                                        <i class="fas fa-eye"></i>
                                    </button>
                                    <div class="invalid-feedback">
                                        Las contraseñas no coinciden.
                                    </div>
                                </div>
                            </div>

                            <!-- Foto -->
                            <div class="form-group mb-3">
                                <label for="foto" class="form-label">Foto de Perfil:</label>
                                <div class="input-group">
                                    <span class="input-group-text">
                                        <i class="fas fa-image"></i>
                                    </span>
                                    <input type="file" 
                                           id="foto"
                                           name="foto"
                                           class="form-control" 
                                           accept="image/*">
                                    <div class="invalid-feedback">
                                        Por favor, selecciona una imagen válida.
                                    </div>
                                </div>
                                <div id="previewFoto" class="mt-2 text-center d-none">
                                    <img src="" alt="Vista previa" class="img-thumbnail" style="max-width: 200px;">
                                </div>
                            </div>
                            
                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-user-plus me-2"></i>Registrarse
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
    <script src="${pageContext.request.contextPath}/js/registro.js"></script>
</body>
</html>