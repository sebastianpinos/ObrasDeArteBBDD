package modelo.data;

import bbdd.DBConnection;
import modelo.entity.Obra;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObraDAO {
    private DBConnection dbConnection;

    public ObraDAO() {
        this.dbConnection = DBConnection.getInstance();
    }

    public boolean save(Obra obra) {
        String sql = "INSERT INTO obra (titulo, idArtista, tecnica, idGaleria, " +
                    "colaboradores, dimensiones, ubicacion, valoracion, idExposicion) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, obra.getTitulo());
            pstmt.setInt(2, obra.getIdArtista());
            pstmt.setString(3, obra.getTecnica());
            pstmt.setInt(4, obra.getIdGaleria());
            pstmt.setInt(5, obra.getColaboradores());
            pstmt.setFloat(6, obra.getDimensiones());
            pstmt.setString(7, obra.getUbicacion());
            pstmt.setInt(8, obra.getValoracion());
            pstmt.setInt(9, obra.getIdExposicion());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Obtener el ID generado
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    obra.setIdObra(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error guardando obra: " + e.getMessage());
        }
        return false;
    }

    public List<Obra> findAll() {
        List<Obra> obras = new ArrayList<>();
        String sql = "SELECT * FROM obra";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Obra obra = mapResultSetToObra(rs);
                obras.add(obra);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo obras: " + e.getMessage());
        }
        return obras;
    }

    public Obra findById(int idObra) {
        String sql = "SELECT * FROM obra WHERE idObra = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idObra);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToObra(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error buscando obra por ID: " + e.getMessage());
        }
        return null;
    }

    public boolean update(Obra obra) {
        String sql = "UPDATE obra SET titulo=?, idArtista=?, tecnica=?, idGaleria=?, " +
                    "colaboradores=?, dimensiones=?, ubicacion=?, valoracion=?, idExposicion=? " +
                    "WHERE idObra=?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, obra.getTitulo());
            pstmt.setInt(2, obra.getIdArtista());
            pstmt.setString(3, obra.getTecnica());
            pstmt.setInt(4, obra.getIdGaleria());
            pstmt.setInt(5, obra.getColaboradores());
            pstmt.setFloat(6, obra.getDimensiones());
            pstmt.setString(7, obra.getUbicacion());
            pstmt.setInt(8, obra.getValoracion());
            pstmt.setInt(9, obra.getIdExposicion());
            pstmt.setInt(10, obra.getIdObra());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error actualizando obra: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int idObra) {
        String sql = "DELETE FROM obra WHERE idObra = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idObra);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error eliminando obra: " + e.getMessage());
        }
        return false;
    }

    public List<Obra> search(String keyword) {
        List<Obra> obras = new ArrayList<>();
        String sql = "SELECT * FROM obra WHERE " +
                    "titulo LIKE ? OR tecnica LIKE ? OR ubicacion LIKE ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Obra obra = mapResultSetToObra(rs);
                obras.add(obra);
            }

        } catch (SQLException e) {
            System.err.println("Error buscando obras: " + e.getMessage());
        }
        return obras;
    }

    public List<Obra> findByArtista(int idArtista) {
        List<Obra> obras = new ArrayList<>();
        String sql = "SELECT * FROM obra WHERE idArtista = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idArtista);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Obra obra = mapResultSetToObra(rs);
                obras.add(obra);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo obras por artista: " + e.getMessage());
        }
        return obras;
    }

    public List<Obra> findByExposicion(int idExposicion) {
        List<Obra> obras = new ArrayList<>();
        String sql = "SELECT * FROM obra WHERE idExposicion = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idExposicion);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Obra obra = mapResultSetToObra(rs);
                obras.add(obra);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo obras por exposición: " + e.getMessage());
        }
        return obras;
    }

    public List<Obra> findByValoracion(int valoracionMinima) {
        List<Obra> obras = new ArrayList<>();
        String sql = "SELECT * FROM obra WHERE valoracion >= ? ORDER BY valoracion DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, valoracionMinima);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Obra obra = mapResultSetToObra(rs);
                obras.add(obra);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo obras por valoración: " + e.getMessage());
        }
        return obras;
    }

    public boolean existeObraEnExposicion(int idExposicion, String titulo) {
        try {
            Object result = dbConnection.callFunction("existeObraExposicion", idExposicion, titulo);
            return result != null && ((Number) result).intValue() == 1;
        } catch (Exception e) {
            System.err.println("Error verificando existencia de obra: " + e.getMessage());
            return false;
        }
    }

    private Obra mapResultSetToObra(ResultSet rs) throws SQLException {
        Obra obra = new Obra();
        obra.setIdObra(rs.getInt("idObra"));
        obra.setTitulo(rs.getString("titulo"));
        obra.setIdArtista(rs.getInt("idArtista"));
        obra.setTecnica(rs.getString("tecnica"));
        obra.setIdGaleria(rs.getInt("idGaleria"));
        obra.setColaboradores(rs.getInt("colaboradores"));
        obra.setDimensiones(rs.getFloat("dimensiones"));
        obra.setUbicacion(rs.getString("ubicacion"));
        obra.setValoracion(rs.getInt("valoracion"));
        obra.setIdExposicion(rs.getInt("idExposicion"));

        // Fechas si existen en la BD
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            obra.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            obra.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return obra;
    }

    public int count() {
        String sql = "SELECT COUNT(*) as total FROM obra";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Error contando obras: " + e.getMessage());
        }
        return 0;
    }
}
