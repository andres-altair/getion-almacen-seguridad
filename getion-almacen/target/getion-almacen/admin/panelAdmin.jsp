<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel de Control</title>

</head>
<body>
    <div class="sidebar">
        <h2>Almacén</h2>
        <a href="gestion-usuarios.jsp" class="nav-link">Gestionar Usuarios</a>
        <a href="../logout" class="nav-link">Cerrar sesión</a>
    </div>

    <div class="main-content">
        <h2>Panel de Control</h2>
        <p>Bienvenido al panel de control. Desde aquí puedes gestionar los usuarios.</p>
        <a href="gestion-usuarios.jsp" class="nav-link">Ir a Gestionar Usuarios</a>
    </div>
</body>
</html>