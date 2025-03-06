<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.time.Year"%>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/img/favicon.png">
<title>Sistema de Gestión de Almacén - Restablecer Contaseña</title>
<!-- Bootstrap CSS -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">
<!-- FontAwesome para iconos -->
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/global.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/components.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/layout.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/forms.css">
<link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css'
	rel='stylesheet'>
</head>
<body>
	<div class="container">
		<div class="card">
			<div class="card-body">
				<h3>Restablecer ContraseÃ±a</h3>
				<form action="restablecerContrasena" method="post">
					<input type="hidden" name="token" value="${token}">
					<div class="form-group">
						<label>Nueva ContraseÃ±a:</label> <input type="password"
							name="nuevaContrasena" class="form-control" required>
					</div>
					<div class="form-group">
						<label>Confirmar ContraseÃ±a:</label> <input type="password"
							name="confirmarContrasena" class="form-control" required>
					</div>
					<button type="submit" class="btn btn-primary">Guardar</button>
				</form>
			</div>
		</div>
	</div>
	<!-- Footer -->
	<footer class="container-fluid py-3 text-center mt-auto">
		<p>
			&copy;
			<%=Year.now()%>
			EnvioGo - Todos los derechos reservados
		</p>
	</footer>

	<!-- Bootstrap JS -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
	<!-- Custom JS -->
	<script src="${pageContext.request.contextPath}/js/acceso.js"></script>
</body>
</html>