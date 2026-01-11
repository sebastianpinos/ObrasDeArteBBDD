package vista;

import modelo.Model;
import modelo.entity.Obra;
import modelo.entity.enums.Tecnica;
import util.Utilities;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ObrasVista extends JPanel {
    private Model model;

    private JPanel panelPrincipal;
    private JPanel topPanel;
    private JLabel lblTituloPrincipal;
    private JPanel searchPanel;
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JPanel tablePanel;
    private JScrollPane scrollPane;
    private JTable tableObras;
    private JPanel formPanel;
    private JTextField txtTitulo;
    private JComboBox<Tecnica> comboTecnica;
    private JComboBox<String> comboArtista;
    private JComboBox<String> comboGaleria;
    private JComboBox<String> comboExposicion;
    private JSpinner spinnerColaboradores;
    private JTextField txtDimensiones;
    private JTextField txtUbicacion;
    private JSpinner spinnerValoracion;
    private JPanel buttonPanel;
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private DefaultTableModel dtmObras;
    private Obra obraSeleccionada;

    public ObrasVista(Model model) {
        this.model = model;
        setLayout(new BorderLayout());
        add(panelPrincipal, BorderLayout.CENTER);

        initializeComponents();
        cargarCombos();
        cargarTabla();
    }

    private void initializeComponents() {
        String[] columnas = {"ID", "Título", "Técnica", "Artista", "Galería",
                "Colaboradores", "Dimensiones", "Ubicación", "Valoración"};
        dtmObras = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableObras.setModel(dtmObras);

        // Centrar contenido de las columnas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tableObras.getColumnCount(); i++) {
            tableObras.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Configurar spinners
        spinnerColaboradores.setModel(new SpinnerNumberModel(1, 1, 50, 1));
        spinnerValoracion.setModel(new SpinnerNumberModel(5, 1, 10, 1));

        // Action Listeners
        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarObra());
        btnActualizar.addActionListener(e -> actualizarObra());
        btnEliminar.addActionListener(e -> eliminarObra());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnBuscar.addActionListener(e -> buscarObras());

        // Listener para selección de fila
        tableObras.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarObraSeleccionada();
            }
        });

        btnActualizar.setEnabled(false);
    }

    private void cargarCombos() {
        // Cargar técnicas desde el enum
        comboTecnica.removeAllItems();
        for (Tecnica tecnica : Tecnica.values()) {
            comboTecnica.addItem(tecnica);
        }

        // Cargar artistas
        comboArtista.removeAllItems();
        comboArtista.addItem("Selecciona un artista");
        model.getAllArtistas().forEach(artista ->
                comboArtista.addItem(artista.getIdArtista() + " - " + artista.getNombreArtistico())
        );

        // Cargar galerías
        comboGaleria.removeAllItems();
        comboGaleria.addItem("Selecciona una galería");
        model.getAllGalerias().forEach(galeria ->
                comboGaleria.addItem(galeria.getIdGaleria() + " - " + galeria.getNombre())
        );

        // Cargar exposiciones
        comboExposicion.removeAllItems();
        comboExposicion.addItem("Selecciona una exposición");
        model.getAllExposiciones().forEach(exposicion ->
                comboExposicion.addItem(exposicion.getIdExposicion() + " - " + exposicion.getTitulo())
        );
    }

    private void cargarTabla() {
        dtmObras.setRowCount(0);
        List<Obra> obras = model.getAllObras();

        for (Obra obra : obras) {
            Object[] fila = {
                    obra.getIdObra(),
                    obra.getTitulo(),
                    obra.getTecnica(),
                    obtenerNombreArtista(obra.getIdArtista()),
                    obtenerNombreGaleria(obra.getIdGaleria()),
                    obra.getColaboradores(),
                    obra.getDimensiones() + " m²",
                    obra.getUbicacion(),
                    obra.getValoracion() + "/10"
            };
            dtmObras.addRow(fila);
        }
    }

    private void buscarObras() {
        String keyword = txtBuscar.getText().trim();

        if (keyword.isEmpty()) {
            cargarTabla();
            return;
        }

        dtmObras.setRowCount(0);
        List<Obra> obras = model.searchObras(keyword);

        if (obras.isEmpty()) {
            Utilities.showInfoAlert("No se encontraron obras con: " + keyword);
            return;
        }

        for (Obra obra : obras) {
            Object[] fila = {
                    obra.getIdObra(),
                    obra.getTitulo(),
                    obra.getTecnica(),
                    obtenerNombreArtista(obra.getIdArtista()),
                    obtenerNombreGaleria(obra.getIdGaleria()),
                    obra.getColaboradores(),
                    obra.getDimensiones() + " m²",
                    obra.getUbicacion(),
                    obra.getValoracion() + "/10"
            };
            dtmObras.addRow(fila);
        }
    }

    private void cargarObraSeleccionada() {
        int selectedRow = tableObras.getSelectedRow();

        if (selectedRow == -1) {
            return;
        }

        int idObra = (int) tableObras.getValueAt(selectedRow, 0);
        obraSeleccionada = model.findObraById(idObra);

        if (obraSeleccionada != null) {
            txtTitulo.setText(obraSeleccionada.getTitulo());

            try {
                Tecnica tecnica = Tecnica.valueOf(obraSeleccionada.getTecnica());
                comboTecnica.setSelectedItem(tecnica);
            } catch (IllegalArgumentException e) {
                comboTecnica.setSelectedIndex(0);
            }

            seleccionarEnCombo(comboArtista, obraSeleccionada.getIdArtista());
            seleccionarEnCombo(comboGaleria, obraSeleccionada.getIdGaleria());
            seleccionarEnCombo(comboExposicion, obraSeleccionada.getIdExposicion());

            spinnerColaboradores.setValue(obraSeleccionada.getColaboradores());
            txtDimensiones.setText(String.valueOf(obraSeleccionada.getDimensiones()));
            txtUbicacion.setText(obraSeleccionada.getUbicacion());
            spinnerValoracion.setValue(obraSeleccionada.getValoracion());

            btnActualizar.setEnabled(true);
            btnGuardar.setEnabled(false);
        }
    }

    private void guardarObra() {
        if (!validarFormulario()) {
            return;
        }

        Obra obra = new Obra();
        obra.setTitulo(txtTitulo.getText().trim());

        Tecnica tecnica = (Tecnica) comboTecnica.getSelectedItem();
        obra.setTecnica(tecnica.name());

        obra.setIdArtista(extraerIdDeCombo(comboArtista));
        obra.setIdGaleria(extraerIdDeCombo(comboGaleria));
        obra.setIdExposicion(extraerIdDeCombo(comboExposicion));
        obra.setColaboradores((int) spinnerColaboradores.getValue());
        obra.setDimensiones(Float.parseFloat(txtDimensiones.getText()));
        obra.setUbicacion(txtUbicacion.getText().trim());
        obra.setValoracion((int) spinnerValoracion.getValue());

        model.insertObra(obra);
        limpiarFormulario();
        cargarTabla();
    }

    private void actualizarObra() {
        if (obraSeleccionada == null) {
            Utilities.showErrorAlert("Selecciona una obra de la tabla");
            return;
        }

        if (!validarFormulario()) {
            return;
        }

        obraSeleccionada.setTitulo(txtTitulo.getText().trim());

        Tecnica tecnica = (Tecnica) comboTecnica.getSelectedItem();
        obraSeleccionada.setTecnica(tecnica.name());

        obraSeleccionada.setIdArtista(extraerIdDeCombo(comboArtista));
        obraSeleccionada.setIdGaleria(extraerIdDeCombo(comboGaleria));
        obraSeleccionada.setIdExposicion(extraerIdDeCombo(comboExposicion));
        obraSeleccionada.setColaboradores((int) spinnerColaboradores.getValue());
        obraSeleccionada.setDimensiones(Float.parseFloat(txtDimensiones.getText()));
        obraSeleccionada.setUbicacion(txtUbicacion.getText().trim());
        obraSeleccionada.setValoracion((int) spinnerValoracion.getValue());

        model.updateObra(obraSeleccionada);
        limpiarFormulario();
        cargarTabla();
    }

    private void eliminarObra() {
        int selectedRow = tableObras.getSelectedRow();

        if (selectedRow == -1) {
            Utilities.showErrorAlert("Selecciona una obra de la tabla");
            return;
        }

        int confirm = Utilities.confirmMessage(
                "¿Estás seguro de eliminar esta obra?",
                "Confirmar eliminación"
        );

        if (confirm == JOptionPane.YES_OPTION) {
            int idObra = (int) tableObras.getValueAt(selectedRow, 0);
            model.deleteObra(idObra);
            limpiarFormulario();
            cargarTabla();
        }
    }

    private void limpiarFormulario() {
        txtTitulo.setText("");
        comboTecnica.setSelectedIndex(0);
        comboArtista.setSelectedIndex(0);
        comboGaleria.setSelectedIndex(0);
        comboExposicion.setSelectedIndex(0);
        spinnerColaboradores.setValue(1);
        txtDimensiones.setText("");
        txtUbicacion.setText("");
        spinnerValoracion.setValue(5);

        obraSeleccionada = null;
        btnActualizar.setEnabled(false);
        btnGuardar.setEnabled(true);
        tableObras.clearSelection();
    }

    private boolean validarFormulario() {
        if (txtTitulo.getText().trim().isEmpty()) {
            Utilities.showErrorAlert("El título es obligatorio");
            txtTitulo.requestFocus();
            return false;
        }

        if (comboArtista.getSelectedIndex() == 0) {
            Utilities.showErrorAlert("Selecciona un artista");
            return false;
        }

        if (comboGaleria.getSelectedIndex() == 0) {
            Utilities.showErrorAlert("Selecciona una galería");
            return false;
        }

        if (comboExposicion.getSelectedIndex() == 0) {
            Utilities.showErrorAlert("Selecciona una exposición");
            return false;
        }

        if (txtDimensiones.getText().trim().isEmpty()) {
            Utilities.showErrorAlert("Las dimensiones son obligatorias");
            txtDimensiones.requestFocus();
            return false;
        }

        try {
            float dim = Float.parseFloat(txtDimensiones.getText().trim());
            if (dim <= 0) {
                Utilities.showErrorAlert("Las dimensiones deben ser mayores a 0");
                return false;
            }
        } catch (NumberFormatException e) {
            Utilities.showErrorAlert("Las dimensiones deben ser un número válido");
            txtDimensiones.requestFocus();
            return false;
        }

        if (txtUbicacion.getText().trim().isEmpty()) {
            Utilities.showErrorAlert("La ubicación es obligatoria");
            txtUbicacion.requestFocus();
            return false;
        }

        return true;
    }

    private int extraerIdDeCombo(JComboBox<String> combo) {
        String selected = (String) combo.getSelectedItem();
        if (selected == null || !selected.contains(" - ")) {
            return 0;
        }
        return Integer.parseInt(selected.split(" - ")[0]);
    }

    private void seleccionarEnCombo(JComboBox<String> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            String item = combo.getItemAt(i);
            if (item.startsWith(id + " - ")) {
                combo.setSelectedIndex(i);
                break;
            }
        }
    }

    private String obtenerNombreArtista(int idArtista) {
        var artista = model.findArtistaById(idArtista);
        return artista != null ? artista.getNombreArtistico() : "Desconocido";
    }

    private String obtenerNombreGaleria(int idGaleria) {
        var galeria = model.findGaleriaById(idGaleria);
        return galeria != null ? galeria.getNombre() : "Desconocida";
    }
}
