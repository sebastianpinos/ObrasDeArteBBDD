package modelo.data;

import bbdd.DBConnection;
import modelo.entity.Exposicion;
import modelo.entity.Obra;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExposicionDAO {
    private DBConnection dbConnection;

    public ExposicionDAO() {
        this.dbConnection = DBConnection.getInstance();
    }

    public boolean save(Exposicion exposicion) {
        String sql = "INSERT INTO exposicion (idArtista, titulo, numeroObras, " +
                    "duracionDias, fechaInicio, idGaleria) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, exposicion.getIdArtista());
            pstmt.setString(2, exposicion.getTitulo());
            pstmt.setInt(3, exposicion.getNumeroObras());
            pstmt.setInt(4, exposicion.getDuracionDias());
            pstmt.setDate(5, Date.valueOf(exposicion.getFechaInicio()));
            pstmt.setInt(6, exposicion.getIdGaleria());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    exposicion.setIdExposicion(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error guardando exposición: " + e.getMessage());
        }
        return false;
    }

    public List<Exposicion> findAll() {
        List<Exposicion> exposiciones = new ArrayList<>();
        String sql = "SELECT * FROM exposicion";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Exposicion exposicion = mapResultSetToExposicion(rs);
                exposiciones.add(exposicion);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo exposiciones: " + e.getMessage());
        }
        return exposiciones;
    }

    public Exposicion findById(int idExposicion) {
        String sql = "SELECT * FROM exposicion WHERE idExposicion = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idExposicion);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToExposicion(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error buscando exposición por ID: " + e.getMessage());
        }
        return null;
    }

    public boolean update(Exposicion exposicion) {
        String sql = "UPDATE exposicion SET idArtista=?, titulo=?, numeroObras=?, " +
                    "duracionDias=?, fechaInicio=?, idGaleria=? WHERE idExposicion=?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, exposicion.getIdArtista());
            pstmt.setString(2, exposicion.getTitulo());
            pstmt.setInt(3, exposicion.getNumeroObras());
            pstmt.setInt(4, exposicion.getDuracionDias());
            pstmt.setDate(5, Date.valueOf(exposicion.getFechaInicio()));
            pstmt.setInt(6, exposicion.getIdGaleria());
            pstmt.setInt(7, exposicion.getIdExposicion());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error actualizando exposición: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int idExposicion) {
        String sql = "DELETE FROM exposicion WHERE idExposicion = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idExposicion);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error eliminando exposición: " + e.getMessage());
        }
        return false;
    }

    public List<Exposicion> search(String keyword) {
        List<Exposicion> exposiciones = new ArrayList<>();
        String sql = "SELECT * FROM exposicion WHERE titulo LIKE ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Exposicion exposicion = mapResultSetToExposicion(rs);
                exposiciones.add(exposicion);
            }

        } catch (SQLException e) {
            System.err.println("Error buscando exposiciones: " + e.getMessage());
        }
        return exposiciones;
    }

    public List<Exposicion> findByArtista(int idArtista) {
        List<Exposicion> exposiciones = new ArrayList<>();
        String sql = "SELECT * FROM exposicion WHERE idArtista = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idArtista);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Exposicion exposicion = mapResultSetToExposicion(rs);
                exposiciones.add(exposicion);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo exposiciones por artista: " + e.getMessage());
        }
        return exposiciones;
    }

    public List<Exposicion> findByGaleria(int idGaleria) {
        List<Exposicion> exposiciones = new ArrayList<>();
        String sql = "SELECT * FROM exposicion WHERE idGaleria = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idGaleria);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Exposicion exposicion = mapResultSetToExposicion(rs);
                exposiciones.add(exposicion);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo exposiciones por galería: " + e.getMessage());
        }
        return exposiciones;
    }

    public List<Exposicion> findActivas() {
        List<Exposicion> exposiciones = new ArrayList<>();
        String sql = "SELECT * FROM exposicion WHERE " +
                    "fechaInicio <= CURDATE() AND " +
                    "DATE_ADD(fechaInicio, INTERVAL duracionDias DAY) >= CURDATE()";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Exposicion exposicion = mapResultSetToExposicion(rs);
                exposiciones.add(exposicion);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo exposiciones activas: " + e.getMessage());
        }
        return exposiciones;
    }

    public List<Exposicion> findFuturas() {
        List<Exposicion> exposiciones = new ArrayList<>();
        String sql = "SELECT * FROM exposicion WHERE fechaInicio > CURDATE() " +
                    "ORDER BY fechaInicio ASC";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Exposicion exposicion = mapResultSetToExposicion(rs);
                exposiciones.add(exposicion);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo exposiciones futuras: " + e.getMessage());
        }
        return exposiciones;
    }

    public boolean existeExposicionArtista(int idArtista, String titulo) {
        try {
            Object result = dbConnection.callFunction("existeExposicionArtista", idArtista, titulo);
            return result != null && ((Number) result).intValue() == 1;
        } catch (Exception e) {
            System.err.println("Error verificando existencia de exposición: " + e.getMessage());
            return false;
        }
    }

    private Exposicion mapResultSetToExposicion(ResultSet rs) throws SQLException {
        Exposicion exposicion = new Exposicion();
        exposicion.setIdExposicion(rs.getInt("idExposicion"));
        exposicion.setIdArtista(rs.getInt("idArtista"));
        exposicion.setTitulo(rs.getString("titulo"));
        exposicion.setNumeroObras(rs.getInt("numeroObras"));
        exposicion.setDuracionDias(rs.getInt("duracionDias"));

        Date fechaInicio = rs.getDate("fechaInicio");
        if (fechaInicio != null) {
            exposicion.setFechaInicio(fechaInicio.toLocalDate());
        }

        exposicion.setIdGaleria(rs.getInt("idGaleria"));

        return exposicion;
    }

    public int count() {
        String sql = "SELECT COUNT(*) as total FROM exposicion";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Error contando exposiciones: " + e.getMessage());
        }
        return 0;
    }
}
