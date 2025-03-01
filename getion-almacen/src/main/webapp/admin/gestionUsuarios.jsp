<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.Year" %>
<%@ page import="java.util.List" %>
<%@ page import="com.andres.gestionalmacen.dtos.UsuarioDto" %>
<%
    List<UsuarioDto> usuariosList = (List<UsuarioDto>) request.getAttribute("usuarios");
    System.out.println("JSP - Lista de usuarios: " + (usuariosList != null ? usuariosList.size() : "null"));
    if (usuariosList != null) {
        for (UsuarioDto user : usuariosList) {
            System.out.println("JSP - Usuario: " + user.getId() + " - " + user.getNombreCompleto() + " - Rol: " + user.getRolId());
        }
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/png" href="img/favicon.png">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Gestión de Usuarios</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
       <!-- Estilos propios -->
       <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
       <link rel="stylesheet" href="${pageContext.request.contextPath}/css/components.css">
       <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css">
</head>
<body>
     <!-- Barra de Navegación -->
    <nav class="navbar">
        <div class="navbar-logo">
            <img src="../img/logo.svg" alt="EnvioGo" class="img-fluid">
        </div>
        <div class="nav-links-container-gestion">
            <a href="${pageContext.request.contextPath}/admin/usuarios" class="nav-link usuarios">
                <i class="fas fa-users"></i> Gestión de Usuarios
            </a>
            <a href="${pageContext.request.contextPath}/admin/backup" class="nav-link">
                <i class="fas fa-database"></i> Backup
            </a>
            <a href="${pageContext.request.contextPath}/admin/incidencias" class="nav-link">
                <i class="fas fa-exclamation-triangle"></i> Incidencia
            </a>
            <a href="${pageContext.request.contextPath}/admin/menu" class="nav-link">
                <i class="fas fa-bars"></i> Menú
            </a>
            <a href="${pageContext.request.contextPath}/cerrarSesion" class="nav-link">
                <i class="fas fa-sign-out-alt"></i> 
            </a>
        </div>
    </nav>

    <!-- Contenedor Principal -->
    <div class="container mt-4">
        <div class="d-flex align-items-center gap-3 mb-4">
            <a href="${pageContext.request.contextPath}/admin/panel" class="btn admin">
                <i class='bx bx-arrow-back'></i> Panel de Control
            </a>
            <button class="btn crear" data-bs-toggle="modal" data-bs-target="#modalCrearUsuario">
                <i class='bx bx-user-plus'></i> Nuevo Usuario
            </button>
        </div>

        <c:if test="${not empty error}">
            <div id="alertaError" class="alert alert-danger alert-dismissible fade show" role="alert">
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
                            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modalCrearUsuario">
                                <i class='bx bx-user-plus'></i> Crear Primer Usuario
                            </button>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Foto</th>
                                    <th>ID</th>
                                    <th>Nombre Completo</th>
                                    <th>Correo</th>
                                    <th>Móvil</th>
                                    <th>Rol</th>
                                    <th>Fecha Creación</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${usuarios}" var="usuario">
                                    <tr>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty fotosBase64[usuario.id]}">
                                                    <img src="${fotosBase64[usuario.id]}" 
                                                    alt="Foto de ${usuario.nombreCompleto}"
                                                    class="rounded-circle"
                                                    style="width: 50px; height: 50px; object-fit: cover;"
                                                    lazy="true">
                                                </c:when>
                                                <c:otherwise>
                                                    <img src="${pageContext.request.contextPath}/assets/img/default-user.png" 
                                                         alt="Foto por defecto"
                                                         class="rounded-circle" style="width: 50px; height: 50px; object-fit: cover;">
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${usuario.id}</td>
                                        <td>${usuario.nombreCompleto}</td>
                                        <td>${usuario.correoElectronico}</td>
                                        <td>${usuario.movil}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${usuario.rolId == 1}">Administrador</c:when>
                                                <c:when test="${usuario.rolId == 2}">Gestor de almacén</c:when>
                                                <c:when test="${usuario.rolId == 3}">Operario de almacén</c:when>
                                                <c:otherwise>Rol desconocido</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${usuario.fechaCreacion}</td>
                                        <td>
                                            <button type="button" class="btn btn-warning btn-sm"
                                                data-bs-toggle="modal" 
                                                data-bs-target="#modalModificarUsuario"
                                                onclick="editarUsuario('${usuario.id}', '${usuario.nombreCompleto}', '${usuario.correoElectronico}', '${usuario.movil}', '${usuario.rolId}', '${fotosBase64[usuario.id]}')">
                                            <i class='bx bx-edit'></i> 
                                            </button>
                                            <button type="button" class="btn btn-danger btn-sm" 
                                                    data-bs-toggle="modal" 
                                                    data-bs-target="#modalEliminarUsuario" 
                                                    data-usuario-id="${usuario.id}">
                                                <i class='bx bx-trash'></i> 
                                            </button>
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
    <div class="modal fade" id="modalCrearUsuario" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Crear Nuevo Usuario</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form action="${pageContext.request.contextPath}/admin/usuarios/crear" method="POST" enctype="multipart/form-data">
                        <input type="hidden" name="action" value="crear">
                        <div class="mb-3">
                            <label for="nombreCompleto" class="form-label">Nombre Completo</label>
                            <input type="text" class="form-control" name="nombreCompleto" required>
                        </div>
                        <div class="mb-3">
                            <label for="correoElectronico" class="form-label">Correo Electrónico</label>
                            <input type="email" class="form-control" name="correoElectronico" required>
                        </div>
                        <div class="mb-3">
                            <label for="movil" class="form-label">Móvil</label>
                            <input type="tel" class="form-control" name="movil" 
                                   required 
                                   pattern="[0-9]{9,15}"
                                   maxlength="15"
                                   placeholder="Ej: 612345678 +34612345678"
                                   title="Introduce un número de móvil válido (entre 9 y 15 dígitos)">
                            <div class="form-text">Formato: 612345678 (entre 9 y 15 dígitos)</div>
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
                        <div class="mb-3">
                            <label for="rolId" class="form-label">Rol</label>
                            <select class="form-select" name="rolId" required>
                                <option value="1">Administrador</option>
                                <option value="2">Gestor de almacén</option>
                                <option value="3">Operario de almacén</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="foto" class="form-label">Foto</label>
                            <input type="file" class="form-control" name="foto" id="foto" accept="image/*" onchange="mostrarVistaPrevia(event)">
                            <!-- Vista previa de la imagen -->
                            <div class="mt-3 text-center">
                                <img id="vistaPrevia" src="#" alt="Vista previa de la imagen" style="max-width: 100%; max-height: 200px; display: none;">
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                            <button type="submit" class="btn btn-primary">Crear Usuario</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal Modificar Usuario -->
    <div class="modal fade" id="modalModificarUsuario" tabindex="-1" aria-labelledby="modalModificarUsuarioLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalModificarUsuarioLabel">Modificar Usuario</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="formModificarUsuario" action="${pageContext.request.contextPath}/admin/usuarios/modificar" method="POST" enctype="multipart/form-data">
                        <input type="hidden" id="usuarioId" name="id">
                        <input type="hidden" name="JSESSIONID" value="${pageContext.session.id}">
                        
                        <div class="mb-3">
                            <label for="nombreCompleto" class="form-label">Nombre Completo</label>
                            <input type="text" class="form-control" id="nombreCompleto" name="nombreCompleto" required>
                        </div>
                        
                        <div class="mb-3">
                            <label for="correoElectronico" class="form-label">Correo Electrónico</label>
                            <input type="email" class="form-control" id="correoElectronico" name="correoElectronico" required>
                        </div>
                        
                        <div class="mb-3">
                            <label for="movil" class="form-label">Móvil</label>
                            <input type="tel" class="form-control" id="movil" name="movil" 
                                   required 
                                   pattern="[0-9]{9,15}"
                                   maxlength="15"
                                   placeholder="Ej: 612345678  +34612345678"
                                   title="Introduce un número de móvil válido (entre 9 y 15 dígitos)">
                            <div class="form-text">Formato: 612345678 (entre 9 y 15 dígitos)</div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="rolId" class="form-label">Rol</label>
                            <select class="form-select" id="rolId" name="rolId" required>
                                <option value="1">Administrador</option>
                                <option value="2">Gestor de almacén</option>
                                 <option value="3">Operario de almacén</option>
                            </select>
                        </div>
                        
                        <div class="mb-3">
                            <label for="foto" class="form-label">Foto</label>
                            <input type="file" class="form-control" id="foto" name="foto" accept="image/*" onchange="mostrarVistaPreviaModificar(event)">
                            <!-- Vista previa de la imagen -->
                            <div id="fotoPreview" class="mt-2">
                                <img id="imagenActual" src="" alt="Foto actual" style="max-width: 100px; display: none;" class="img-thumbnail">
                                <img id="vistaPreviaModificar" src="#" alt="Vista previa de la imagen" style="max-width: 100px; display: none;" class="img-thumbnail">
                            </div>
                        </div>
                        
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                            <button type="submit" class="btn btn-primary">Guardar Cambios</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal de Confirmación de Eliminación -->
    <div class="modal fade" id="modalEliminarUsuario" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-danger text-white">
                    <h5 class="modal-title">Confirmar Eliminación de Usuario</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="alert alert-warning" role="alert">
                        <i class='bx bx-error-circle'></i>
                        <strong>Advertencia:</strong> Esta acción no se puede deshacer.
                    </div>
                    
                    <form id="formEliminarUsuario" action="${pageContext.request.contextPath}/admin/usuarios/eliminar" method="POST">
                        <input type="hidden" id="usuarioIdEliminar" name="id">
                        
                        <div class="mb-3">
                            <label for="confirmacionId" class="form-label">
                                Para confirmar, escriba el ID del usuario: <span id="idUsuarioAEliminar" class="text-danger fw-bold"></span>
                            </label>
                            <input type="text" class="form-control" id="confirmacionId" name="confirmacionId" 
                                   required 
                                   pattern="[0-9]+"
                                   title="Por favor, ingrese el ID exactamente como se muestra arriba">
                            <div class="invalid-feedback">
                                El ID ingresado no coincide con el ID del usuario a eliminar.
                            </div>
                        </div>
                        
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                            <button type="submit" class="btn btn-danger" id="btnConfirmarEliminar" disabled>
                                <i class='bx bx-trash'></i> Eliminar Usuario
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer>
        <p>&copy; <%= Year.now() %> EnvioGo - Todos los derechos reservados</p>
    </footer>

    <script>
        function editarUsuario(id, nombre, correo, movil, rolId, fotoBase64) {
            console.log("ID recibido:", id);
            document.getElementById('usuarioId').value = id;
            document.getElementById('nombreCompleto').value = nombre;
            document.getElementById('correoElectronico').value = correo;
            
            // Validar longitud del móvil antes de asignarlo
            if (movil && movil.length > 15) {
                movil = movil.substring(0, 15);
                console.log("Móvil truncado a 15 caracteres:", movil);
            }
            document.getElementById('movil').value = movil;
            
            document.getElementById('rolId').value = rolId;

            // Manejo de la foto
            const imagenActual = document.getElementById('imagenActual');
            if (fotoBase64) {
                imagenActual.src = fotoBase64;
                imagenActual.style.display = 'block'; // Mostrar la imagen si existe
            } else {
                imagenActual.src = ""; // Limpiar la imagen si no hay foto
                imagenActual.style.display = 'none'; // Ocultar el contenedor si no hay foto
            }
        }
        
        // Evento para el modal de eliminación
        document.getElementById('modalEliminarUsuario').addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const usuarioId = button.getAttribute('data-usuario-id');
            
            document.getElementById('usuarioIdEliminar').value = usuarioId;
            document.getElementById('idUsuarioAEliminar').textContent = usuarioId;
            
            // Resetear el formulario
            document.getElementById('confirmacionId').value = '';
            document.getElementById('btnConfirmarEliminar').disabled = true;
        });

        // Validación en tiempo real del ID de confirmación
        document.getElementById('confirmacionId').addEventListener('input', function(event) {
            const idAEliminar = document.getElementById('usuarioIdEliminar').value;
            const idConfirmacion = this.value;
            const btnConfirmar = document.getElementById('btnConfirmarEliminar');
            
            if (idConfirmacion === idAEliminar) {
                this.classList.remove('is-invalid');
                this.classList.add('is-valid');
                btnConfirmar.disabled = false;
            } else {
                this.classList.remove('is-valid');
                this.classList.add('is-invalid');
                btnConfirmar.disabled = true;
            }
        });

        // Validación final antes de enviar
        document.getElementById('formEliminarUsuario').addEventListener('submit', function(event) {
            const idAEliminar = document.getElementById('usuarioIdEliminar').value;
            const idConfirmacion = document.getElementById('confirmacionId').value;
            
            if (idAEliminar !== idConfirmacion) {
                event.preventDefault();
                document.getElementById('confirmacionId').classList.add('is-invalid');
            } else {
                // Mostrar mensaje de carga
                document.getElementById('btnConfirmarEliminar').disabled = true;
                document.getElementById('btnConfirmarEliminar').innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Eliminando...';
            }
        });
      

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
        
     // Función para mostrar la vista previa de la imagen
        function mostrarVistaPrevia(event) {
            const input = event.target; // Obtener el input de tipo file
            const vistaPrevia = document.getElementById('vistaPrevia'); // Obtener el elemento img

            if (input.files && input.files[0]) {
                const reader = new FileReader(); // Crear un FileReader

                // Cuando el archivo se cargue, actualizar la vista previa
                reader.onload = function (e) {
                    vistaPrevia.src = e.target.result; // Establecer la imagen
                    vistaPrevia.style.display = 'block'; // Mostrar la imagen
                };

                reader.readAsDataURL(input.files[0]); // Leer el archivo como URL
            } else {
                vistaPrevia.src = "#"; // Limpiar la vista previa
                vistaPrevia.style.display = 'none'; // Ocultar la imagen
            }
        }
        // Función para mostrar la vista previa de la imagen en el modal de modificar
        function mostrarVistaPreviaModificar(event) {
            const input = event.target; // Obtener el input de tipo file
            const vistaPrevia = document.getElementById('vistaPreviaModificar'); // Obtener el elemento img de vista previa
            const imagenActual = document.getElementById('imagenActual'); // Obtener el elemento img de la imagen actual

            if (input.files && input.files[0]) {
                const reader = new FileReader(); // Crear un FileReader

                // Cuando el archivo se cargue, actualizar la vista previa
                reader.onload = function (e) {
                    vistaPrevia.src = e.target.result; // Establecer la imagen
                    vistaPrevia.style.display = 'block'; // Mostrar la vista previa
                    imagenActual.style.display = 'none'; // Ocultar la imagen actual
                };

                reader.readAsDataURL(input.files[0]); // Leer el archivo como URL
            } else {
                vistaPrevia.src = "#"; // Limpiar la vista previa
                vistaPrevia.style.display = 'none'; // Ocultar la vista previa
                imagenActual.style.display = 'block'; // Mostrar la imagen actual
            }
        }
        setTimeout(function() {
        var alerta = document.getElementById('alertaError');
        if (alerta) {
            // Cierra la alerta usando Bootstrap
            var bsAlert = new bootstrap.Alert(alerta);
            bsAlert.close();
        }
    }, 6000);
    </script>

    <!-- Bootstrap y Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>