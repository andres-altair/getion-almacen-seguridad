<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.andres.gestionalmacen.dtos.UsuarioDto" %>
<%@ page import="com.andres.gestionalmacen.utilidades.ImagenUtil" %>
<%
    // Verificar si los atributos necesarios están presentes
    if (request.getAttribute("usuarioNombre") == null || request.getAttribute("usuarioFoto") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/panel");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Panel de Administración</title>
    <!-- FontAwesome para iconos -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        /* Paleta de Colores */
        :root {
            --azul-marino: #003366;
            --amarillo-estrella: #FFA500;
            --amarillo-oscuro: #CC8400; /* Para mejorar contraste */
            --rojo-coral: #E74C3C;
            --rojo-oscuro: #C0392B;
            --gris-oscuro: #2C3E50;
            --gris-claro: #F4F4F4;
            --blanco: #FFFFFF;
            --negro: #000000;
        }

        /* Estilos Generales */
        body {
            font-family: Arial, sans-serif;
            background-color: var(--gris-claro);
            color: var(--gris-oscuro);
            margin: 0;
            padding: 0;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
        }

        /* Barra de Navegación */
        .navbar {
            background-color: var(--azul-marino);
            padding: 10px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .navbar-brand {
            color: var(--blanco);
            font-size: 24px;
            text-decoration: none;
        }

        .navbar-nav {
            display: flex;
            align-items: center;
            gap: 15px;
            list-style: none;
            margin: 0;
            padding: 0;
        }

        .nav-item {
            display: flex;
            align-items: center;
        }

        .nav-link {
            color: var(--azul-marino); /* Texto azul marino */
            text-decoration: none;
            padding: 10px 15px;
            background-color: var(--blanco); /* Fondo blanco */
            border: 2px solid var(--blanco); /* Borde blanco */
            border-radius: 5px;
            font-size: 14px;
            transition: background-color 0.3s ease, color 0.3s ease;
        }

        .nav-link:hover {
            background-color: var(--azul-marino); /* Fondo azul marino al pasar el ratón */
            color: var(--blanco); /* Texto blanco al pasar el ratón */
        }

       .welcome-text {
            color: var(--negro); /* Texto negro */
            font-size: 30px; /* Tamaño más grande */
            margin-right: 20px;
            margin-left: 20px;
        }

        .welcome-text .nombre-usuario {
            color: var(--amarillo-oscuro); /* Color amarillo oscuro para el nombre */
            font-weight: bold; /* Opcional: texto en negrita */
        }

        .logout-icon {
            color: var(--blanco); /* Icono blanco */
            font-size: 30px;
            cursor: pointer;
            transition: color 0.3s ease;
        }

        .logout-icon:hover {
            color: var(--amarillo-estrella); /* Icono amarillo al pasar el ratón */
        }

        .img-perfil {
    width: 64px; /* Ajusta el tamaño según sea necesario */
    height: 64px; /* Ajusta el tamaño según sea necesario */
    object-fit: cover; /* Mantiene la proporción de la imagen */
    border-radius: 50%; /* Asegura que la imagen sea redonda */
}

        /* Contenedor Principal */
        .container {
            margin: 20px auto;
            width: 90%;
            max-width: 1200px;
            flex: 1; /* Para que el contenedor ocupe el espacio restante */
        }

        h2 {
            color: var(--azul-marino);
            font-size: 24px;
            margin-bottom: 20px;
        }

        .card {
            background-color: var(--blanco);
            border-radius: 8px;
            box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
        }

        .card-title {
            color: var(--azul-marino);
            font-size: 20px;
            margin-bottom: 15px;
        }

        .card-text {
            color: var(--gris-oscuro);
            font-size: 16px;
            margin-bottom: 20px;
        }

        .btn {
            padding: 10px 15px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            transition: background-color 0.3s ease;
            text-decoration: none;
            display: inline-block;
            text-align: center;
        }

        .btn-primary {
            background-color: var(--azul-marino);
            color: var(--blanco);
        }

        .btn-primary:hover {
            background-color: #002244; /* Azul más oscuro */
        }

        .d-grid {
            display: grid;
            gap: 10px;
        }

        /* Footer */
        footer {
            background-color: var(--azul-marino);
            color: var(--blanco);
            text-align: center;
            padding: 15px 0;
            margin-top: auto; /* Para que el footer se quede abajo */
        }

        footer p {
            margin: 0;
            font-size: 14px;
        }

        @media (max-width: 768px) {
            .dashboard-container {
                grid-template-columns: 1fr;
                padding: 10px;
            }
            .card {
                margin: 10px 0;
                padding: 15px;
            }
            .navigation {
                padding: 10px;
                flex-direction: column;
            }
            .navbar {
                padding: 10px;
            }
            .navbar-nav {
                flex-direction: column;
                gap: 10px;
            }
            .welcome-text {
                font-size: 20px;
                margin: 10px 0;
                text-align: center;
            }
            .img-perfil {
                width: 48px;
                height: 48px;
            }
            .nav-link {
                width: 100%;
                text-align: center;
            }
            .container {
                width: 95%;
                padding: 10px;
            }
            .btn {
                width: 100%;
                margin: 5px 0;
            }
            .card-title {
                font-size: 18px;
            }
            .card-text {
                font-size: 14px;
            }
        }

        @media (max-width: 480px) {
            .welcome-text {
                font-size: 18px;
            }
            .img-perfil {
                width: 40px;
                height: 40px;
            }
            .container {
                width: 100%;
                padding: 5px;
            }
        }
    </style>
</head>
<body>
    <!-- Barra de Navegación -->
    <div class="navbar">
        <img src="logo.png" alt="EnvioGo" height="40">
        <a href="${pageContext.request.contextPath}/admin/usuarios" class="nav-link">Gestión de Usuarios</a>
        <a href="#" class="nav-link">Backup</a>
        <a href="#" class="nav-link">Incidencia</a>
        <a href="#" class="nav-link">Menú</a>
        <ul class="navbar-nav">
            <li class="nav-item">
                <i class="fas fa-sign-out-alt logout-icon" onclick="window.location.href='logout'"></i>
            </li>
        </ul>
    </div>
    <br><br>

    <!-- Contenedor Principal -->
    <div class="container">
        <h1>Panel de control del administrador</h1>
        <br><br>
        <li class="nav-item">
           <img src="${usuarioFoto}" 
     class="img-perfil" 
     alt="Foto de perfil">
            <span class="welcome-text">Bienvenido  <span class="nombre-usuario">${usuarioNombre}</span></span>
        </li>
    </div>

    <!-- Footer -->
    <footer>
        <p>&copy; 2023 EnvioGo. Todos los derechos reservados.</p>
    </footer>
</body>
</html>