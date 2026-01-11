package modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa una Exposición de Arte
 * Equivalente a la tabla 'exposicion' en la base de datos
 */
public class Exposicion {
    private Integer idExposicion;
    private Integer idArtista;
    private String titulo;
    private Integer numeroObras;
    private Integer duracionDias;
    private LocalDate fechaInicio;
    private Integer idGaleria;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Objetos relacionados
    private Artista artista;
    private Galeria galeria;

    public Exposicion() {
    }

    public Exposicion(Integer idArtista, String titulo, Integer numeroObras, 
                      Integer duracionDias, LocalDate fechaInicio, Integer idGaleria) {
        this.idArtista = idArtista;
        this.titulo = titulo;
        this.numeroObras = numeroObras;
        this.duracionDias = duracionDias;
        this.fechaInicio = fechaInicio;
        this.idGaleria = idGaleria;
    }

    // Getters y Setters
    public Integer getIdExposicion() { return idExposicion; }
    public void setIdExposicion(Integer idExposicion) { this.idExposicion = idExposicion; }

    public Integer getIdArtista() { return idArtista; }
    public void setIdArtista(Integer idArtista) { this.idArtista = idArtista; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Integer getNumeroObras() { return numeroObras; }
    public void setNumeroObras(Integer numeroObras) { this.numeroObras = numeroObras; }

    public Integer getDuracionDias() { return duracionDias; }
    public void setDuracionDias(Integer duracionDias) { this.duracionDias = duracionDias; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public Integer getIdGaleria() { return idGaleria; }
    public void setIdGaleria(Integer idGaleria) { this.idGaleria = idGaleria; }

    public Artista getArtista() { return artista; }
    public void setArtista(Artista artista) { this.artista = artista; }

    public Galeria getGaleria() { return galeria; }
    public void setGaleria(Galeria galeria) { this.galeria = galeria; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Exposicion{" +
                "idExposicion=" + idExposicion +
                ", titulo='" + titulo + '\'' +
                ", numeroObras=" + numeroObras +
                ", duracionDias=" + duracionDias +
                ", fechaInicio=" + fechaInicio +
                '}';
    }
}
