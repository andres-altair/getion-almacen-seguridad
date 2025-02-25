<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/png" href="img/favicon.png">
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome para iconos -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <title>Sistema de Gestión de Almacén - Iniciar Sesión</title>
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/components.css">
    <link rel="stylesheet" href="css/layout.css">
    <link rel="stylesheet" href="css/forms.css">
</head>
<body>
    <!-- Barra de Navegación -->
    <nav class="navbar">
        <div class="navbar-logo">
            <img src="img/logo.svg" alt="EnvioGo" height="40">
        </div>
        <a class="navbar-brand" href="#">Iniciar Sesión</a>
        <a href="inicio.jsp" class="navbar-back">
            <i class="fas fa-home"></i>
        </a>
    </nav>
    <br><br>

    <!-- Contenedor Principal -->
    <div class="container">
        <form action="acceso" method="post">
            <div class="mb-3">
                <label for="correoElectronico" class="form-label">Correo Electrónico</label>
                <input type="email" class="form-control" name="correoElectronico" required>
            </div>
            <div class="mb-3">
                <label for="contrasena" class="form-label">Contraseña</label>
                <div class="input-group">
                    <input type="password" class="form-control" name="contrasena" id="contrasena" required>
                    <button class="btn btn-outline-secondary" type="button" id="togglePasswordCrear" onclick="togglePassword('contrasena', this)">
                        <i class="fas fa-eye" id="eyeIconCrear"></i>
                    </button>
                </div>
            </div>
            <div class="mb-3 form-check">
                <input type="checkbox" class="form-check-input" id="recordar" name="recordar">
                <label class="form-check-label" for="recordar">Recordar sesión</label>
            </div>
            <div class="mb-3">
                <button type="submit" class="btn btn-primary">Iniciar Sesión</button>
            </div>
        </form>

        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>
    </div>

    <!-- Footer -->
    <footer>
        <p>&copy; 2025 EnvioGo Sistema de Gestión de Almacén</p>
    </footer>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Función para alternar la visibilidad de la contraseña
        function togglePassword(inputId, button) {
            const passwordInput = document.getElementById(inputId); // Obtener el campo de contraseña
            const eyeIcon = button.querySelector('i'); // Obtener el ícono del ojo

            if (passwordInput.type === "password") {
                passwordInput.type = "text"; // Mostrar contraseña
                eyeIcon.classList.remove("fa-eye");
                eyeIcon.classList.add("fa-eye-slash"); // Cambiar ícono a "ojo tachado"
            } else {
                passwordInput.type = "password"; // Ocultar contraseña
                eyeIcon.classList.remove("fa-eye-slash");
                eyeIcon.classList.add("fa-eye"); // Cambiar ícono a "ojo"
            }
        }
    </script>
</body>
</html>