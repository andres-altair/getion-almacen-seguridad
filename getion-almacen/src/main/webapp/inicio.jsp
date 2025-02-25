<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/png" href="img/favicon.png">
    <title>Sistema de Gestión de Almacén</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/components.css">
    <link rel="stylesheet" href="css/layout.css">
</head>
<body>
    <!-- Barra de Navegación -->
    <nav class="navbar navbar-expand-lg">
        <div class="container-fluid">
            <div class="navbar-logo">
                <img src="img/logo.svg" alt="EnvioGo" class="img-fluid">
            </div>
            <a class="navbar-brand" href="#">Sistema de Gestión de Almacén</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="acceso">Iniciar Sesión</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="registro">Registrarse</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Contenedor Principal -->
    <div class="container my-4">
        <div class="row text-center">
            <div class="col-12">
                <h1 class="display-4">Sistema de Gestión de Almacén</h1>
                <p class="lead">Gestiona tu almacén de manera eficiente y segura</p>
            </div>
        </div>
    </div>

    <!-- Carrusel -->
    <div id="carouselAlmacen" class="carousel slide" data-bs-ride="carousel">
        <!-- Indicadores -->
        <div class="carousel-indicators">
            <button type="button" data-bs-target="#carouselAlmacen" data-bs-slide-to="0" class="active"></button>
            <button type="button" data-bs-target="#carouselAlmacen" data-bs-slide-to="1"></button>
            <button type="button" data-bs-target="#carouselAlmacen" data-bs-slide-to="2"></button>
        </div>

        <!-- Slides -->
        <div class="carousel-inner">
            <div class="carousel-item active">
                <img src="https://images.pexels.com/photos/4483610/pexels-photo-4483610.jpeg" class="d-block w-100" alt="">
                <div class="carousel-caption">
                    <h2 class="d-none d-sm-block">Gestión Moderna de Almacenes</h2>
                    <p class="d-none d-sm-block">Optimiza tus operaciones con nuestra solución integral</p>
                </div>
            </div>
            <div class="carousel-item">
                <img src="https://images.pexels.com/photos/7706434/pexels-photo-7706434.jpeg" class="d-block w-100" alt="">
                <div class="carousel-caption">
                    <h2 class="d-none d-sm-block">Servicios Logísticos Avanzados</h2>
                    <p class="d-none d-sm-block">Control total de tu inventario en tiempo real</p>
                </div>
            </div>
            <div class="carousel-item">
                <img src="https://images.pexels.com/photos/7706444/pexels-photo-7706444.jpeg" class="d-block w-100" alt="">
                <div class="carousel-caption">
                    <h2 class="d-none d-sm-block">Tecnología de Última Generación</h2>
                    <p class="d-none d-sm-block">Sistemas automatizados para mayor eficiencia</p>
                </div>
            </div>
        </div>

        <!-- Controles -->
        <button class="carousel-control-prev" type="button" data-bs-target="#carouselAlmacen" data-bs-slide="prev">
            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
            <span class="visually-hidden">Anterior</span>
        </button>
        <button class="carousel-control-next" type="button" data-bs-target="#carouselAlmacen" data-bs-slide="next">
            <span class="carousel-control-next-icon" aria-hidden="true"></span>
            <span class="visually-hidden">Siguiente</span>
        </button>
    </div>

    <!-- Footer -->
    <footer class="container-fluid py-3 text-center">
        <p class="mb-0">&copy; 2025 EnvioGo Sistema de Gestión de Almacén</p>
    </footer>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>