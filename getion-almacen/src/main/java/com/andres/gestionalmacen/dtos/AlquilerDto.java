package com.andres.gestionalmacen.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AlquilerDto {
    private Long id;
    private Long sectorId;
    private String sectorNombre;
    private Long usuarioId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private BigDecimal montoPagado;
    private String ordenId;
    private String estado;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSectorId() { return sectorId; }
    public void setSectorId(Long sectorId) { this.sectorId = sectorId; }

    public String getSectorNombre() { return sectorNombre; }
    public void setSectorNombre(String sectorNombre) { this.sectorNombre = sectorNombre; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }

    public BigDecimal getMontoPagado() { return montoPagado; }
    public void setMontoPagado(BigDecimal montoPagado) { this.montoPagado = montoPagado; }

    public String getOrdenId() { return ordenId; }
    public void setOrdenId(String ordenId) { this.ordenId = ordenId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}