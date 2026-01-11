package modelo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Artista {
    private Integer idArtista;
    private String nombreArtistico;
    private String nombreReal;
    private Integer edad;
    private String pais;
    private LocalDate fechaPrimeraObra;
    private Boolean exposicionActiva;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Artista() {
    }

    public Artista(String nombreArtistico, String nombreReal, Integer edad, 
                   String pais, LocalDate fechaPrimeraObra, Boolean exposicionActiva) {
        this.nombreArtistico = nombreArtistico;
        this.nombreReal = nombreReal;
        this.edad = edad;
        this.pais = pais;
        this.fechaPrimeraObra = fechaPrimeraObra;
        this.exposicionActiva = exposicionActiva;
    }

    // Getters y Setters
    public Integer getIdArtista() { return idArtista; }
    public void setIdArtista(Integer idArtista) { this.idArtista = idArtista; }

    public String getNombreArtistico() { return nombreArtistico; }
    public void setNombreArtistico(String nombreArtistico) { this.nombreArtistico = nombreArtistico; }

    public String getNombreReal() { return nombreReal; }
    public void setNombreReal(String nombreReal) { this.nombreReal = nombreReal; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public LocalDate getFechaPrimeraObra() { return fechaPrimeraObra; }
    public void setFechaPrimeraObra(LocalDate fechaPrimeraObra) { this.fechaPrimeraObra = fechaPrimeraObra; }

    public Boolean getExposicionActiva() { return exposicionActiva; }
    public void setExposicionActiva(Boolean exposicionActiva) { this.exposicionActiva = exposicionActiva; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Artista{" +
                "idArtista=" + idArtista +
                ", nombreArtistico='" + nombreArtistico + '\'' +
                ", nombreReal='" + nombreReal + '\'' +
                ", pais='" + pais + '\'' +
                ", exposicionActiva=" + exposicionActiva +
                '}';
    }
}
