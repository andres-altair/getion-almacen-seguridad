<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDateTime" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Usuarios</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
    <style>
        .empty-state {
            text-align: center;
            padding: 40px;
            background: #f8f9fa;
            border-radius: 8px;
            margin: 20px 0;
        }
        .empty-state i {
            font-size: 48px;
            color: #6c757d;
            margin-bottom: 20px;
        }
        .table th {
            background-color: #f8f9fa;
            font-weight: 600;
        }
        .table td {
            vertical-align: middle;
        }
        .table img.rounded-circle {
            border: 2px solid #dee2e6;
            transition: transform 0.2s;
        }
        .table img.rounded-circle:hover {
            transform: scale(1.1);
        }
        .badge {
            font-size: 0.85em;
            padding: 0.5em 0.8em;
        }
        .btn-group .btn {
            padding: 0.25rem 0.5rem;
        }
        .btn-group .btn i {
            font-size: 1.1rem;
        }
    </style>
</head>
<body>
    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Gestión de Usuarios</h2>
            <div class="d-flex gap-2">
                <a href="${pageContext.request.contextPath}/admin/panel.jsp" class="btn btn-secondary">
                    <i class='bx bx-arrow-back'></i> Panel de Control
                </a>
                <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modalUsuario">
                    <i class='bx bx-user-plus'></i> Nuevo Usuario
                </button>
            </div>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                Usuario creado exitosamente
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <div class="card">
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty usuarios}">
                        <div class="empty-state">
                            <i class='bx bx-user-x'></i>
                            <h3>No hay usuarios registrados</h3>
                            <p>Actualmente no hay usuarios en el sistema.</p>
                            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modalUsuario">
                                <i class='bx bx-user-plus'></i> Crear Primer Usuario
                            </button>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Foto</th>
                                    <th>ID</th>
                                    <th>Nombre Completo</th>
                                    <th>Correo</th>
                                    <th>Móvil</th>
                                    <th>Rol</th>
                                    <th>Fecha Creación</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${usuarios}" var="usuario">
                                    <tr>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty usuario.foto}">
                                                    <img src="${usuario.foto}" alt="Foto de ${usuario.nombre}" 
                                                         class="rounded-circle" style="width: 50px; height: 50px; object-fit: cover;">
                                                </c:when>
                                                <c:otherwise>
                                                    <img src="${pageContext.request.contextPath}/assets/img/default-user.png" 
                                                         alt="Foto por defecto"
                                                         class="rounded-circle" style="width: 50px; height: 50px; object-fit: cover;">
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${usuario.id}</td>
                                        <td>${usuario.nombre}</td>
                                        <td>${usuario.email}</td>
                                        <td>${usuario.movil}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${usuario.rol == 1}">
                                                    <span class="badge bg-primary">Administrador</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">Usuario</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:set var="pattern" value="dd/MM/yyyy HH:mm" />
                                            ${usuario.fechaCreacion.format(DateTimeFormatter.ofPattern(pattern))}
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Modal para crear usuario -->
    <div class="modal fade" id="modalUsuario" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Nuevo Usuario</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form id="formUsuario" action="gestion-usuarios" method="post" enctype="multipart/form-data">
                        <div class="mb-3">
                            <label class="form-label">Nombre Completo</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class='bx bx-user'></i></span>
                                <input type="text" class="form-control" name="nombreCompleto" maxlength="50" required>
                            </div>
                            <div class="form-text">Máximo 50 caracteres</div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Email</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class='bx bx-envelope'></i></span>
                                <input type="email" class="form-control" name="email" maxlength="50" required>
                            </div>
                            <div class="form-text">Máximo 50 caracteres</div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Móvil</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class='bx bx-phone'></i></span>
                                <input type="tel" class="form-control" name="movil" maxlength="15" pattern="[0-9+]{9,15}">
                            </div>
                            <div class="form-text">Formato: +34123456789 (opcional)</div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Rol</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class='bx bx-user-circle'></i></span>
                                <select class="form-select" name="rolId" required>
                                    <option value="">Selecciona un rol</option>
                                    <option value="1">Administrador</option>
                                    <option value="2">Usuario</option>
                                </select>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Contraseña</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class='bx bx-lock-alt'></i></span>
                                <input type="password" class="form-control" name="contrasena" required>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Foto</label>
                            <div class="input-group">
                                <input type="file" class="form-control" name="foto" 
                                       accept="image/jpeg,image/png,image/gif,image/bmp"
                                       onchange="previewImage(this)">
                            </div>
                            <div class="form-text">Formatos permitidos: JPEG, PNG, GIF, BMP</div>
                        </div>

                        <div class="d-grid">
                            <button type="submit" class="btn btn-primary">
                                <i class='bx bx-save'></i> Guardar Usuario
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function previewImage(input) {
            if (input.files && input.files[0]) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    document.getElementById('previewFoto').src = e.target.result;
                };
                reader.readAsDataURL(input.files[0]);
            }
        }

        function editarUsuario(id) {
            // TODO: Implementar edición
            console.log('Editar usuario:', id);
        }
        
        function eliminarUsuario(id) {
            if (confirm('¿Estás seguro de que deseas eliminar este usuario?')) {
                // TODO: Implementar eliminación
                console.log('Eliminar usuario:', id);
            }
        }
    </script>
</body>
</html>
