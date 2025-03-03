// Función para mostrar vista previa de la imagen
function mostrarVistaPrevia(event) {
    const vistaPrevia = document.getElementById('vistaPrevia');
    const file = event.target.files[0];
    
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            vistaPrevia.src = e.target.result;
            vistaPrevia.style.display = 'block';
        }
        reader.readAsDataURL(file);
    } else {
        vistaPrevia.src = '#';
        vistaPrevia.style.display = 'none';
    }
}

// Función para alternar la visibilidad de la contraseña
function verContasena(inputId, button) {
    const passwordInput = document.getElementById(inputId);
    const eyeIcon = button.querySelector('i');

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