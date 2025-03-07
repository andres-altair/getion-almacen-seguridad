// Función para alternar la visibilidad de la contraseña
function alternarContrasena(idInput, boton) {
    const inputContrasena = document.getElementById(idInput);
    const iconoOjo = boton.querySelector('i');

    if (inputContrasena.type === "password") {
        inputContrasena.type = "text";
        iconoOjo.classList.remove("fa-eye");
        iconoOjo.classList.add("fa-eye-slash");
    } else {
        inputContrasena.type = "password";
        iconoOjo.classList.remove("fa-eye-slash");
        iconoOjo.classList.add("fa-eye");
    }
}

// Función para manejar la respuesta de autenticación de Google
function manejarRespuestaGoogle(respuesta) {
    console.log('Iniciando proceso de login con Google');
    const rutaContexto = document.querySelector('meta[name="context-path"]').content;
    
    fetch(rutaContexto + '/GoogleAccesoServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'idToken=' + respuesta.credential
    })
    .then(respuestaHttp => {
        console.log('Respuesta recibida:', respuestaHttp.status);
        return respuestaHttp.text().then(texto => {
            if (!respuestaHttp.ok) {
                if (texto.includes("Usuario no encontrado")) {
                    console.log('Usuario no encontrado, redirigiendo a registro');
                    window.location.href = rutaContexto + '/registro';
                    return;
                }
                throw new Error(texto);
            }
            return texto;
        });
    })
    .then(texto => {
        if (texto === "OK") {
            console.log('Login exitoso, redirigiendo...');
            window.location.href = rutaContexto + '/usuario/panel';
        }
    })
    .catch(error => {
        console.error('Error:', error);
        mostrarError('Error al iniciar sesión con Google');
    });
}

// Función para mostrar mensajes de error
function mostrarError(mensaje) {
    const contenedorError = document.createElement('div');
    contenedorError.className = 'alert alert-danger alert-dismissible fade show';
    contenedorError.role = 'alert';
    contenedorError.innerHTML = `
        ${mensaje}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    
    // Insertar al principio del formulario
    const formulario = document.querySelector('form');
    formulario.parentNode.insertBefore(contenedorError, formulario);
    
    // Auto-ocultar después de 5 segundos
    setTimeout(() => {
        contenedorError.remove();
    }, 5000);
}

// Inicializar Google Sign-In
function inicializarGoogle() {
    const idCliente = document.querySelector('meta[name="google-signin-client_id"]').content;
    google.accounts.id.initialize({
        client_id: idCliente,
        callback: manejarRespuestaGoogle
    });
    google.accounts.id.renderButton(
        document.getElementById('g_id_signin'),
        {
            type: 'standard',
            shape: 'rectangular',
            theme: 'outline',
            text: 'signin_with',
            size: 'large',
            logo_alignment: 'left',
            width: '100%'
        }
    );
}

// Ejecutar cuando el DOM esté cargado
document.addEventListener('DOMContentLoaded', function() {
    inicializarGoogle();
});