package modelo;

import modelo.data.ArtistaDAO;
import modelo.data.ExposicionDAO;
import modelo.data.GaleriaDAO;
import modelo.data.ObraDAO;

import bbdd.DBConnection;
import modelo.entity.Artista;
import modelo.entity.Exposicion;
import modelo.entity.Galeria;
import modelo.entity.Obra;
import util.Utilities;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Model {
    private ArtistaDAO artistaDAO;
    private ObraDAO obraDAO;
    private GaleriaDAO galeriaDAO;
    private ExposicionDAO exposicionDAO;

    public Model() {
        this.artistaDAO = new ArtistaDAO();
        this.obraDAO = new ObraDAO();
        this.galeriaDAO = new GaleriaDAO();
        this.exposicionDAO = new ExposicionDAO();
    }

    public void insertObra(Obra obra) {
        // Validar que no exista una obra con el mismo título en la misma exposición
        if (obraDAO.existeObraEnExposicion(obra.getIdExposicion(), obra.getTitulo())) {
            Utilities.showInfoAlert("Ya existe una obra con ese título en esta exposición");
            return;
        }

        boolean saved = obraDAO.save(obra);
        if (saved) {
            Utilities.showInfoAlert("Obra guardada exitosamente");
        } else {
            Utilities.showErrorAlert("Error al guardar la obra");
        }
    }

    public void updateObra(Obra obra) {
        boolean updated = obraDAO.update(obra);
        if (updated) {
            Utilities.showInfoAlert("Obra actualizada exitosamente");
        } else {
            Utilities.showErrorAlert("Error al actualizar la obra");
        }
    }

    public void deleteObra(int idObra) {
        boolean deleted = obraDAO.delete(idObra);
        if (deleted) {
            Utilities.showInfoAlert("Obra eliminada exitosamente");
        } else {
            Utilities.showErrorAlert("Error al eliminar la obra");
        }
    }

    public List<Obra> getAllObras() {
        return obraDAO.findAll();
    }

    public Obra findObraById(int idObra) {
        return obraDAO.findById(idObra);
    }

    public List<Obra> searchObras(String keyword) {
        return obraDAO.search(keyword);
    }

    public List<Obra> getObrasByArtista(int idArtista) {
        return obraDAO.findByArtista(idArtista);
    }

    public List<Obra> getObrasByExposicion(int idExposicion) {
        return obraDAO.findByExposicion(idExposicion);
    }

    public List<Obra> getObrasByValoracion(int valoracionMinima) {
        return obraDAO.findByValoracion(valoracionMinima);
    }

    public void insertArtista(Artista artista) {

        if (artistaDAO.existeArtista(artista.getNombreArtistico())) {
            Utilities.showInfoAlert("Ya existe un artista con ese nombre artístico");
            return;
        }

        boolean saved = artistaDAO.save(artista);
        if (saved) {
            Utilities.showInfoAlert("Artista guardado exitosamente");
        } else {
            Utilities.showErrorAlert("Error al guardar el artista");
        }
    }

    public void updateArtista(Artista artista) {
        boolean updated = artistaDAO.update(artista);
        if (updated) {
            Utilities.showInfoAlert("Artista actualizado exitosamente");
        } else {
            Utilities.showErrorAlert("Error al actualizar el artista");
        }
    }

    public void deleteArtista(int idArtista) {
        boolean deleted = artistaDAO.delete(idArtista);
        if (deleted) {
            Utilities.showInfoAlert("Artista eliminado exitosamente");
        } else {
            Utilities.showErrorAlert("Error al eliminar el artista");
        }
    }

    public List<Artista> getAllArtistas() {
        return artistaDAO.findAll();
    }

    public Artista findArtistaById(int idArtista) {
        return artistaDAO.findById(idArtista);
    }

    public List<Artista> searchArtistas(String keyword) {
        return artistaDAO.search(keyword);
    }

    public List<Artista> getArtistasActivos() {
        return artistaDAO.findActivos();
    }

    public List<Artista> getArtistasByPais(String pais) {
        return artistaDAO.findByPais(pais);
    }

    public void insertGaleria(Galeria galeria) {
        // Validar que no exista una galería con el mismo nombre
        if (galeriaDAO.existeGaleria(galeria.getNombre())) {
            Utilities.showInfoAlert("Ya existe una galería con ese nombre");
            return;
        }

        boolean saved = galeriaDAO.save(galeria);
        if (saved) {
            Utilities.showInfoAlert("Galería guardada exitosamente");
        } else {
            Utilities.showErrorAlert("Error al guardar la galería");
        }
    }

    public void updateGaleria(Galeria galeria) {
        boolean updated = galeriaDAO.update(galeria);
        if (updated) {
            Utilities.showInfoAlert("Galería actualizada exitosamente");
        } else {
            Utilities.showErrorAlert("Error al actualizar la galería");
        }
    }

    public void deleteGaleria(int idGaleria) {
        boolean deleted = galeriaDAO.delete(idGaleria);
        if (deleted) {
            Utilities.showInfoAlert("Galería eliminada exitosamente");
        } else {
            Utilities.showErrorAlert("Error al eliminar la galería");
        }
    }

    public List<Galeria> getAllGalerias() {
        return galeriaDAO.findAll();
    }

    public Galeria findGaleriaById(int idGaleria) {
        return galeriaDAO.findById(idGaleria);
    }

    public List<Galeria> searchGalerias(String keyword) {
        return galeriaDAO.search(keyword);
    }

    public List<Galeria> getGaleriasByLocalizacion(String localizacion) {
        return galeriaDAO.findByLocalizacion(localizacion);
    }

    public void insertExposicion(Exposicion exposicion) {
        // Validar que no exista una exposición con el mismo título del mismo artista
        if (exposicionDAO.existeExposicionArtista(exposicion.getIdArtista(), exposicion.getTitulo())) {
            Utilities.showInfoAlert("El artista ya tiene una exposición con ese título");
            return;
        }

        boolean saved = exposicionDAO.save(exposicion);
        if (saved) {
            Utilities.showInfoAlert("Exposición guardada exitosamente");
        } else {
            Utilities.showErrorAlert("Error al guardar la exposición");
        }
    }

    public void updateExposicion(Exposicion exposicion) {
        boolean updated = exposicionDAO.update(exposicion);
        if (updated) {
            Utilities.showInfoAlert("Exposición actualizada exitosamente");
        } else {
            Utilities.showErrorAlert("Error al actualizar la exposición");
        }
    }

    public void deleteExposicion(int idExposicion) {
        boolean deleted = exposicionDAO.delete(idExposicion);
        if (deleted) {
            Utilities.showInfoAlert("Exposición eliminada exitosamente");
        } else {
            Utilities.showErrorAlert("Error al eliminar la exposición");
        }
    }

    public List<Exposicion> getAllExposiciones() {
        return exposicionDAO.findAll();
    }

    public Exposicion findExposicionById(int idExposicion) {
        return exposicionDAO.findById(idExposicion);
    }

    public List<Exposicion> searchExposiciones(String keyword) {
        return exposicionDAO.search(keyword);
    }

    public List<Exposicion> getExposicionesByArtista(int idArtista) {
        return exposicionDAO.findByArtista(idArtista);
    }

    public List<Exposicion> getExposicionesByGaleria(int idGaleria) {
        return exposicionDAO.findByGaleria(idGaleria);
    }

    public List<Exposicion> getExposicionesActivas() {
        return exposicionDAO.findActivas();
    }

    public List<Exposicion> getExposicionesFuturas() {
        return exposicionDAO.findFuturas();
    }

    public int getTotalObras() {
        return obraDAO.count();
    }

    public int getTotalArtistas() {
        return artistaDAO.count();
    }

    public int getTotalGalerias() {
        return galeriaDAO.count();
    }

    public int getTotalExposiciones() {
        return exposicionDAO.count();
    }
}
