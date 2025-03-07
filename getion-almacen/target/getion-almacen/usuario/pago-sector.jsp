<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Pago de Sector</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h3>Pago de Sector: ${sectorSeleccionado.nombre}</h3>
            </div>
            <div class="card-body">
                <h5>Precio mensual: €${sectorSeleccionado.precioMensual}</h5>
                <form action="${pageContext.request.contextPath}/usuario/procesarPago" method="post">
                    <input type="hidden" name="sectorId" value="${sectorSeleccionado.id}">
                    <input type="hidden" name="total" value="${sectorSeleccionado.precioMensual}">
                    <button type="submit" class="btn btn-primary btn-lg mt-3">
                        <i class="fab fa-paypal me-2"></i> Pagar con PayPal
                    </button>
                </form>
            </div>
        </div>
    </div>
</body>
</html>