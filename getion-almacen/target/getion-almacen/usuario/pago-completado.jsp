<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Pago Completado</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <div class="alert alert-success">
            <h3>¡Pago Completado con Éxito!</h3>
            <p>Tu alquiler del sector ha sido procesado correctamente.</p>
            <a href="${pageContext.request.contextPath}/usuario/panel" class="btn home-btn ms-auto">Volver al Panel de Control</a>
        </div>
    </div>
</body>
</html>