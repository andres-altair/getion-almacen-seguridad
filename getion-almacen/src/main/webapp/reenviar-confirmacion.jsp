<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Reenviar Confirmación</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/img/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
    <div class="container">
        <h2>Reenviar Correo de Confirmación</h2>
        
        <% if (request.getSession().getAttribute("error") != null) { %>
            <div class="alert alert-danger">
                <%= request.getSession().getAttribute("error") %>
                <% request.getSession().removeAttribute("error"); %>
            </div>
        <% } %>
        
        <form action="${pageContext.request.contextPath}/reenviar-confirmacion" method="post">
            <div class="form-group">
                <label for="email">Correo Electrónico:</label>
                <input type="email" id="email" name="email" required>
            </div>
            
            <button type="submit" class="btn btn-primary">Reenviar Confirmación</button>
        </form>
        
        <div class="links">
            <a href="${pageContext.request.contextPath}/acceso">Volver al Inicio de Sesión</a>
        </div>
    </div>
</body>
</html>