package modelo;

import java.time.LocalDateTime;

/**
 * Entidad que representa una Obra de Arte Urbano
 * Equivalente a la tabla 'obra' en la base de datos
 */
public class Obra {
    private Integer idObra;
    private String titulo;
    private Integer idArtista;
    private String tecnica;
    private Integer idGaleria;
    private Integer colaboradores;
    private Float dimensiones;
    private String ubicacion;
    private Integer valoracion;
    private Integer idExposicion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Objetos relacionados
    private Artista artista;
    private Galeria galeria;
    private Exposicion exposicion;

    public Obra() {
    }

    public Obra(String titulo, Integer idArtista, String tecnica, Integer idGaleria, 
                Integer colaboradores, Float dimensiones, String ubicacion, 
                Integer valoracion, Integer idExposicion) {
        this.titulo = titulo;
        this.idArtista = idArtista;
        this.tecnica = tecnica;
        this.idGaleria = idGaleria;
        this.colaboradores = colaboradores;
        this.dimensiones = dimensiones;
        this.ubicacion = ubicacion;
        this.valoracion = valoracion;
        this.idExposicion = idExposicion;
    }

    // Getters y Setters
    public Integer getIdObra() { return idObra; }
    public void setIdObra(Integer idObra) { this.idObra = idObra; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Integer getIdArtista() { return idArtista; }
    public void setIdArtista(Integer idArtista) { this.idArtista = idArtista; }

    public String getTecnica() { return tecnica; }
    public void setTecnica(String tecnica) { this.tecnica = tecnica; }

    public Integer getIdGaleria() { return idGaleria; }
    public void setIdGaleria(Integer idGaleria) { this.idGaleria = idGaleria; }

    public Integer getColaboradores() { return colaboradores; }
    public void setColaboradores(Integer colaboradores) { this.colaboradores = colaboradores; }

    public Float getDimensiones() { return dimensiones; }
    public void setDimensiones(Float dimensiones) { this.dimensiones = dimensiones; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public Integer getValoracion() { return valoracion; }
    public void setValoracion(Integer valoracion) { this.valoracion = valoracion; }

    public Integer getIdExposicion() { return idExposicion; }
    public void setIdExposicion(Integer idExposicion) { this.idExposicion = idExposicion; }

    public Artista getArtista() { return artista; }
    public void setArtista(Artista artista) { this.artista = artista; }

    public Galeria getGaleria() { return galeria; }
    public void setGaleria(Galeria galeria) { this.galeria = galeria; }

    public Exposicion getExposicion() { return exposicion; }
    public void setExposicion(Exposicion exposicion) { this.exposicion = exposicion; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Obra{" +
                "idObra=" + idObra +
                ", titulo='" + titulo + '\'' +
                ", tecnica='" + tecnica + '\'' +
                ", dimensiones=" + dimensiones +
                ", ubicacion='" + ubicacion + '\'' +
                ", valoracion=" + valoracion +
                '}';
    }
}
