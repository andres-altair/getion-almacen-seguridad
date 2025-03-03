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