// Código para gestionar las contraseñas
document.addEventListener('DOMContentLoaded', function() {
    const nuevaContrasena = document.getElementById('nuevaContrasena');
    const confirmarContrasena = document.getElementById('confirmarContrasena');
    const btnMostrarContrasena = document.getElementById('mostrarContrasena');
    const btnMostrarConfirmarContrasena = document.getElementById('mostrarConfirmarContrasena');

    // Mostrar/ocultar contraseña nueva
    btnMostrarContrasena.addEventListener('click', function() {
        if (nuevaContrasena.type === "password") {
            nuevaContrasena.type = "text";
            this.querySelector('i').classList.replace('fa-eye', 'fa-eye-slash');
        } else {
            nuevaContrasena.type = "password";
            this.querySelector('i').classList.replace('fa-eye-slash', 'fa-eye');
        }
    });

    // Mostrar/ocultar confirmación de contraseña
    btnMostrarConfirmarContrasena.addEventListener('click', function() {
        if (confirmarContrasena.type === "password") {
            confirmarContrasena.type = "text";
            this.querySelector('i').classList.replace('fa-eye', 'fa-eye-slash');
        } else {
            confirmarContrasena.type = "password";
            this.querySelector('i').classList.replace('fa-eye-slash', 'fa-eye');
        }
    });

    // Verificar que las contraseñas coincidan
    confirmarContrasena.addEventListener('input', function() {
        if (nuevaContrasena.value !== confirmarContrasena.value) {
            confirmarContrasena.setCustomValidity('Las contraseñas no coinciden');
        } else {
            confirmarContrasena.setCustomValidity('');
        }
    });

    nuevaContrasena.addEventListener('input', function() {
        if (nuevaContrasena.value !== confirmarContrasena.value) {
            confirmarContrasena.setCustomValidity('Las contraseñas no coinciden');
        } else {
            confirmarContrasena.setCustomValidity('');
        }
    });
});