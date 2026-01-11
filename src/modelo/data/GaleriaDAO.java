package modelo.data;

import bbdd.DBConnection;
import modelo.entity.Galeria;
import modelo.entity.Obra;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class GaleriaDAO {
    private DBConnection dbConnection;

    public GaleriaDAO() {
        this.dbConnection = DBConnection.getInstance();
    }

    public boolean save(Galeria galeria) {
        String sql = "INSERT INTO galeria (nombre, localizacion, empleados, " +
                    "fechaFundacion, director) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, galeria.getNombre());
            pstmt.setString(2, galeria.getLocalizacion());
            pstmt.setInt(3, galeria.getEmpleados());
            pstmt.setDate(4, galeria.getFechaFundacion() != null ? 
                         Date.valueOf(galeria.getFechaFundacion()) : null);
            pstmt.setString(5, galeria.getDirector());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    galeria.setIdGaleria(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error guardando galería: " + e.getMessage());
        }
        return false;
    }

    public List<Galeria> findAll() {
        List<Galeria> galerias = new ArrayList<>();
        String sql = "SELECT * FROM galeria";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Galeria galeria = mapResultSetToGaleria(rs);
                galerias.add(galeria);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo galerías: " + e.getMessage());
        }
        return galerias;
    }

    public Galeria findById(int idGaleria) {
        String sql = "SELECT * FROM galeria WHERE idGaleria = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idGaleria);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToGaleria(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error buscando galería por ID: " + e.getMessage());
        }
        return null;
    }

    public Galeria findByNombre(String nombre) {
        String sql = "SELECT * FROM galeria WHERE nombre = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToGaleria(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error buscando galería por nombre: " + e.getMessage());
        }
        return null;
    }

    public boolean update(Galeria galeria) {
        String sql = "UPDATE galeria SET nombre=?, localizacion=?, empleados=?, " +
                    "fechaFundacion=?, director=? WHERE idGaleria=?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, galeria.getNombre());
            pstmt.setString(2, galeria.getLocalizacion());
            pstmt.setInt(3, galeria.getEmpleados());
            pstmt.setDate(4, galeria.getFechaFundacion() != null ? 
                         Date.valueOf(galeria.getFechaFundacion()) : null);
            pstmt.setString(5, galeria.getDirector());
            pstmt.setInt(6, galeria.getIdGaleria());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error actualizando galería: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int idGaleria) {
        String sql = "DELETE FROM galeria WHERE idGaleria = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idGaleria);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error eliminando galería: " + e.getMessage());
        }
        return false;
    }

    public List<Galeria> search(String keyword) {
        List<Galeria> galerias = new ArrayList<>();
        String sql = "SELECT * FROM galeria WHERE " +
                    "nombre LIKE ? OR localizacion LIKE ? OR director LIKE ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Galeria galeria = mapResultSetToGaleria(rs);
                galerias.add(galeria);
            }

        } catch (SQLException e) {
            System.err.println("Error buscando galerías: " + e.getMessage());
        }
        return galerias;
    }

    public List<Galeria> findByLocalizacion(String localizacion) {
        List<Galeria> galerias = new ArrayList<>();
        String sql = "SELECT * FROM galeria WHERE localizacion LIKE ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + localizacion + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Galeria galeria = mapResultSetToGaleria(rs);
                galerias.add(galeria);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo galerías por localización: " + e.getMessage());
        }
        return galerias;
    }

    public boolean existeGaleria(String nombre) {
        try {
            Object result = dbConnection.callFunction("existeGaleria", nombre);
            return result != null && ((Number) result).intValue() == 1;
        } catch (Exception e) {
            System.err.println("Error verificando existencia de galería: " + e.getMessage());
            return false;
        }
    }

    private Galeria mapResultSetToGaleria(ResultSet rs) throws SQLException {
        Galeria galeria = new Galeria();
        galeria.setIdGaleria(rs.getInt("idGaleria"));
        galeria.setNombre(rs.getString("nombre"));
        galeria.setLocalizacion(rs.getString("localizacion"));
        galeria.setEmpleados(rs.getInt("empleados"));

        Date fechaFundacion = rs.getDate("fechaFundacion");
        if (fechaFundacion != null) {
            galeria.setFechaFundacion(fechaFundacion.toLocalDate());
        }

        galeria.setDirector(rs.getString("director"));

        return galeria;
    }

    public int count() {
        String sql = "SELECT COUNT(*) as total FROM galeria";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Error contando galerías: " + e.getMessage());
        }
        return 0;
    }
}
