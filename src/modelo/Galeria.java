package modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa una Galería de Arte
 * Equivalente a la tabla 'galeria' en la base de datos
 */
public class Galeria {
    private Integer idGaleria;
    private String nombre;
    private String localizacion;
    private Integer empleados;
    private LocalDate fechaFundacion;
    private String director;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Galeria() {
    }

    public Galeria(String nombre, String localizacion, Integer empleados, 
                   LocalDate fechaFundacion, String director) {
        this.nombre = nombre;
        this.localizacion = localizacion;
        this.empleados = empleados;
        this.fechaFundacion = fechaFundacion;
        this.director = director;
    }

    // Getters y Setters
    public Integer getIdGaleria() { return idGaleria; }
    public void setIdGaleria(Integer idGaleria) { this.idGaleria = idGaleria; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getLocalizacion() { return localizacion; }
    public void setLocalizacion(String localizacion) { this.localizacion = localizacion; }

    public Integer getEmpleados() { return empleados; }
    public void setEmpleados(Integer empleados) { this.empleados = empleados; }

    public LocalDate getFechaFundacion() { return fechaFundacion; }
    public void setFechaFundacion(LocalDate fechaFundacion) { this.fechaFundacion = fechaFundacion; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Galeria{" +
                "idGaleria=" + idGaleria +
                ", nombre='" + nombre + '\'' +
                ", localizacion='" + localizacion + '\'' +
                ", director='" + director + '\'' +
                '}';
    }
}
