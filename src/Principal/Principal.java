package Principal;

import bbdd.DBConnection;
import modelo.Model;
import vista.ArtistasVista;
import vista.GaleriasVista;
import vista.ObrasVista;

import javax.swing.*;

public class Principal {
    public static void main(String[] args) {
        try {
            // UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            // UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");



        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            DBConnection conexion = DBConnection.getInstance();

            if (!conexion.isConnected()) {
                JOptionPane.showMessageDialog(
                    null,
                    "No se pudo conectar a la base de datos.\n" +
                    "Verifica el archivo config.properties",
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Crear el modelo
            Model modelo = new Model();

            // Crear y mostrar la ventana principal
            crearVentanaPrincipal(modelo);
        });
    }

    private static void crearVentanaPrincipal(Model modelo) {
        JFrame ventana = new JFrame("Arte Urbano - Gestión de Obras");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(1400, 800);

        // Crear JTabbedPane para las diferentes vistas
        JTabbedPane tabbedPane = new JTabbedPane();

        // Agregar vista de Obras
        ObrasVista vistaObras = new ObrasVista(modelo);
        tabbedPane.addTab("Obras", vistaObras);

        ArtistasVista vistaArtistas = new ArtistasVista(modelo);
        tabbedPane.addTab("Artistas", vistaArtistas);

        GaleriasVista vistaGalerias = new GaleriasVista(modelo);
        tabbedPane.addTab("Galerías", vistaGalerias);

        ventana.add(tabbedPane);
        ventana.setLocationRelativeTo(null); // Centrar ventana
        ventana.setVisible(true);
    }
}
