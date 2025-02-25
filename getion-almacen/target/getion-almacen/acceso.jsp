<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar Sesión - Sistema de Gestión de Almacén</title>
</head>
<body>
    <h2>Iniciar Sesión</h2>
    <form action="acceso" method="post">
        <div>
            <label>Correo Electrónico</label>
            <input type="email" name="correoElectronico" required>
        </div>
        <div>
            <label>Contraseña</label>
            <input type="password" name="contrasena" required>
        </div>
        <div>
            <input type="checkbox" id="recordar" name="recordar">
            <label for="recordar">Recordar sesión</label>
        </div>
        <div>
            <button type="submit">Iniciar Sesión</button>
        </div>
    </form>
    <div>
        <a href="inicio.jsp">Volver al inicio</a>
    </div>

    <c:if test="${not empty error}">
        <div style="color: red;">${error}</div>
    </c:if>

    <c:if test="${not empty contrasenaHasheada}">
        <div>
            <strong>Contraseña Hasheada:</strong> ${contrasenaHasheada}
        </div>
    </c:if>
</body>
</html>