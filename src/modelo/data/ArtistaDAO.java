package modelo.data;

import bbdd.DBConnection;
import modelo.entity.Artista;
import modelo.entity.Obra;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArtistaDAO {
    private DBConnection dbConnection;

    public ArtistaDAO() {
        this.dbConnection = DBConnection.getInstance();
    }

    public boolean save(Artista artista) {
        String sql = "INSERT INTO artista (nombreArtistico, nombreReal, edad, pais, " +
                    "fechaPrimeraObra, exposicionActiva) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, artista.getNombreArtistico());
            pstmt.setString(2, artista.getNombreReal());
            pstmt.setInt(3, artista.getEdad());
            pstmt.setString(4, artista.getPais());
            pstmt.setDate(5, artista.getFechaPrimeraObra() != null ? 
                         Date.valueOf(artista.getFechaPrimeraObra()) : null);
            pstmt.setBoolean(6, artista.getExposicionActiva());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    artista.setIdArtista(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error guardando artista: " + e.getMessage());
        }
        return false;
    }

    public List<Artista> findAll() {
        List<Artista> artistas = new ArrayList<>();
        String sql = "SELECT * FROM artista";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Artista artista = mapResultSetToArtista(rs);
                artistas.add(artista);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo artistas: " + e.getMessage());
        }
        return artistas;
    }

    public Artista findById(int idArtista) {
        String sql = "SELECT * FROM artista WHERE idArtista = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idArtista);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToArtista(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error buscando artista por ID: " + e.getMessage());
        }
        return null;
    }

    public Artista findByNombreArtistico(String nombreArtistico) {
        String sql = "SELECT * FROM artista WHERE nombreArtistico = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreArtistico);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToArtista(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error buscando artista por nombre artístico: " + e.getMessage());
        }
        return null;
    }

    public boolean update(Artista artista) {
        String sql = "UPDATE artista SET nombreArtistico=?, nombreReal=?, edad=?, pais=?, " +
                    "fechaPrimeraObra=?, exposicionActiva=? WHERE idArtista=?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, artista.getNombreArtistico());
            pstmt.setString(2, artista.getNombreReal());
            pstmt.setInt(3, artista.getEdad());
            pstmt.setString(4, artista.getPais());
            pstmt.setDate(5, artista.getFechaPrimeraObra() != null ? 
                         Date.valueOf(artista.getFechaPrimeraObra()) : null);
            pstmt.setBoolean(6, artista.getExposicionActiva());
            pstmt.setInt(7, artista.getIdArtista());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error actualizando artista: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int idArtista) {
        String sql = "DELETE FROM artista WHERE idArtista = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idArtista);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error eliminando artista: " + e.getMessage());
        }
        return false;
    }

    public List<Artista> search(String keyword) {
        List<Artista> artistas = new ArrayList<>();
        String sql = "SELECT * FROM artista WHERE " +
                    "nombreArtistico LIKE ? OR nombreReal LIKE ? OR pais LIKE ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Artista artista = mapResultSetToArtista(rs);
                artistas.add(artista);
            }

        } catch (SQLException e) {
            System.err.println("Error buscando artistas: " + e.getMessage());
        }
        return artistas;
    }

    public List<Artista> findActivos() {
        List<Artista> artistas = new ArrayList<>();
        String sql = "SELECT * FROM artista WHERE exposicionActiva = true";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Artista artista = mapResultSetToArtista(rs);
                artistas.add(artista);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo artistas activos: " + e.getMessage());
        }
        return artistas;
    }

    public List<Artista> findByPais(String pais) {
        List<Artista> artistas = new ArrayList<>();
        String sql = "SELECT * FROM artista WHERE pais = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pais);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Artista artista = mapResultSetToArtista(rs);
                artistas.add(artista);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo artistas por país: " + e.getMessage());
        }
        return artistas;
    }

    public boolean existeArtista(String nombreArtistico) {
        try {
            Object result = dbConnection.callFunction("existeArtista", nombreArtistico);
            return result != null && ((Number) result).intValue() == 1;
        } catch (Exception e) {
            System.err.println("Error verificando existencia de artista: " + e.getMessage());
            return false;
        }
    }

    private Artista mapResultSetToArtista(ResultSet rs) throws SQLException {
        Artista artista = new Artista();
        artista.setIdArtista(rs.getInt("idArtista"));
        artista.setNombreArtistico(rs.getString("nombreArtistico"));
        artista.setNombreReal(rs.getString("nombreReal"));
        artista.setEdad(rs.getInt("edad"));
        artista.setPais(rs.getString("pais"));

        Date fechaPrimeraObra = rs.getDate("fechaPrimeraObra");
        if (fechaPrimeraObra != null) {
            artista.setFechaPrimeraObra(fechaPrimeraObra.toLocalDate());
        }

        artista.setExposicionActiva(rs.getBoolean("exposicionActiva"));

        return artista;
    }

    public int count() {
        String sql = "SELECT COUNT(*) as total FROM artista";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Error contando artistas: " + e.getMessage());
        }
        return 0;
    }
}
