package com.andres.gestionalmacen.dtos;

import java.math.BigDecimal;
import java.util.Objects;

public class SectorDto {
    private Long id;
    private String nombre;
    private Integer metrosCuadrados;
    private BigDecimal precioMensual;
    private String caracteristicas;
    private String estado;
    private boolean disponible;

    // Constructor
    public SectorDto() {}
    
    // Getters y Setters con validación
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.nombre = nombre.trim();
    }
    
    public Integer getMetrosCuadrados() { return metrosCuadrados; }
    public void setMetrosCuadrados(Integer metrosCuadrados) {
        if (metrosCuadrados == null || metrosCuadrados <= 0) {
            throw new IllegalArgumentException("Los metros cuadrados deben ser mayores que 0");
        }
        this.metrosCuadrados = metrosCuadrados;
    }
    
    public BigDecimal getPrecioMensual() { return precioMensual; }
    public void setPrecioMensual(BigDecimal precioMensual) {
        if (precioMensual == null || precioMensual.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio mensual debe ser mayor que 0");
        }
        this.precioMensual = precioMensual;
    }
    
    public String getCaracteristicas() { return caracteristicas; }
    public void setCaracteristicas(String caracteristicas) { 
        this.caracteristicas = caracteristicas != null ? caracteristicas.trim() : "";
    }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { 
        this.estado = estado;
        this.disponible = "DISPONIBLE".equals(estado);
    }
    
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectorDto sectorDto = (SectorDto) o;
        return Objects.equals(id, sectorDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SectorDto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", metrosCuadrados=" + metrosCuadrados +
                ", precioMensual=" + precioMensual +
                ", estado='" + estado + '\'' +
                ", disponible=" + disponible +
                '}';
    }
}