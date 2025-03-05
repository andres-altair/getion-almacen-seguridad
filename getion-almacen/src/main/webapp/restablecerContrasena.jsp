<div class="container">
    <div class="card">
        <div class="card-body">
            <h3>Restablecer Contraseña</h3>
            <form action="restablecerContrasena" method="post">
                <input type="hidden" name="token" value="${token}">
                <div class="form-group">
                    <label>Nueva Contraseña:</label>
                    <input type="password" name="nuevaContrasena" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Confirmar Contraseña:</label>
                    <input type="password" name="confirmarContrasena" class="form-control" required>
                </div>
                <button type="submit" class="btn btn-primary">Guardar</button>
            </form>
        </div>
    </div>
</div>