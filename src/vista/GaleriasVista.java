package vista;

import modelo.Model;
import modelo.entity.Galeria;
import modelo.entity.enums.Localizacion;
import util.Utilities;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Vista para gestionar galerías
 * Estilo visual idéntico a ObrasVista
 */
public class GaleriasVista extends JPanel {
    private Model model;

    // Componentes vinculados al .form
    private JPanel panelPrincipal;
    private JPanel topPanel;
    private JLabel lblTituloPrincipal;
    private JPanel searchPanel;
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JPanel tablePanel;
    private JScrollPane scrollPane;
    private JTable tableGalerias;
    private JPanel formPanel;
    private JTextField txtNombre;
    private JComboBox<Localizacion> comboLocalizacion;
    private JSpinner spinnerEmpleados;
    private JTextField txtFechaFundacion;
    private JTextField txtDirector;
    private JPanel buttonPanel;
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private DefaultTableModel dtmGalerias;
    private Galeria galeriaSeleccionada;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public GaleriasVista(Model model) {
        this.model = model;
        setLayout(new BorderLayout());
        add(panelPrincipal, BorderLayout.CENTER);

        initializeComponents();
        cargarCombos();
        cargarTabla();
    }

    private void initializeComponents() {
        // Configurar tabla
        String[] columnas = {"ID", "Nombre", "Localización", "Empleados", 
                            "Fecha Fundación", "Director"};
        dtmGalerias = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableGalerias.setModel(dtmGalerias);
        tableGalerias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Spinner
        spinnerEmpleados.setModel(new SpinnerNumberModel(10, 1, 500, 1));

        // Listeners
        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarGaleria());
        btnActualizar.addActionListener(e -> actualizarGaleria());
        btnEliminar.addActionListener(e -> eliminarGaleria());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnBuscar.addActionListener(e -> buscarGalerias());

        tableGalerias.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarGaleriaSeleccionada();
            }
        });

        btnActualizar.setEnabled(false);
    }

    private void cargarCombos() {
        comboLocalizacion.removeAllItems();
        for (Localizacion loc : Localizacion.values()) {
            comboLocalizacion.addItem(loc);
        }
    }

    private void cargarTabla() {
        dtmGalerias.setRowCount(0);
        List<Galeria> galerias = model.getAllGalerias();

        for (Galeria galeria : galerias) {
            Object[] fila = {
                galeria.getIdGaleria(),
                galeria.getNombre(),
                galeria.getLocalizacion(),
                galeria.getEmpleados(),
                galeria.getFechaFundacion() != null ? 
                    galeria.getFechaFundacion().format(dateFormatter) : "N/A",
                galeria.getDirector()
            };
            dtmGalerias.addRow(fila);
        }
    }

    private void buscarGalerias() {
        String keyword = txtBuscar.getText().trim();

        if (keyword.isEmpty()) {
            cargarTabla();
            return;
        }

        dtmGalerias.setRowCount(0);
        List<Galeria> galerias = model.searchGalerias(keyword);

        if (galerias.isEmpty()) {
            Utilities.showInfoAlert("No se encontraron galerías con: " + keyword);
            return;
        }

        for (Galeria galeria : galerias) {
            Object[] fila = {
                galeria.getIdGaleria(),
                galeria.getNombre(),
                galeria.getLocalizacion(),
                galeria.getEmpleados(),
                galeria.getFechaFundacion() != null ? 
                    galeria.getFechaFundacion().format(dateFormatter) : "N/A",
                galeria.getDirector()
            };
            dtmGalerias.addRow(fila);
        }
    }

    private void cargarGaleriaSeleccionada() {
        int selectedRow = tableGalerias.getSelectedRow();

        if (selectedRow == -1) {
            return;
        }

        int idGaleria = (int) tableGalerias.getValueAt(selectedRow, 0);
        galeriaSeleccionada = model.findGaleriaById(idGaleria);

        if (galeriaSeleccionada != null) {
            txtNombre.setText(galeriaSeleccionada.getNombre());

            try {
                Localizacion loc = Localizacion.fromNombre(galeriaSeleccionada.getLocalizacion());
                if (loc != null) {
                    comboLocalizacion.setSelectedItem(loc);
                }
            } catch (Exception e) {
                comboLocalizacion.setSelectedIndex(0);
            }

            spinnerEmpleados.setValue(galeriaSeleccionada.getEmpleados());

            if (galeriaSeleccionada.getFechaFundacion() != null) {
                txtFechaFundacion.setText(galeriaSeleccionada.getFechaFundacion().format(dateFormatter));
            } else {
                txtFechaFundacion.setText("");
            }

            txtDirector.setText(galeriaSeleccionada.getDirector());

            btnActualizar.setEnabled(true);
            btnGuardar.setEnabled(false);
        }
    }

    private void guardarGaleria() {
        if (!validarFormulario()) {
            return;
        }

        Galeria galeria = new Galeria();
        galeria.setNombre(txtNombre.getText().trim());

        Localizacion loc = (Localizacion) comboLocalizacion.getSelectedItem();
        galeria.setLocalizacion(loc.getNombre());

        galeria.setEmpleados((Integer) spinnerEmpleados.getValue());

        // Parsear fecha
        try {
            if (!txtFechaFundacion.getText().trim().isEmpty()) {
                LocalDate fecha = LocalDate.parse(txtFechaFundacion.getText().trim(), dateFormatter);
                galeria.setFechaFundacion(fecha);
            }
        } catch (Exception e) {
            Utilities.showErrorAlert("Fecha inválida. Usa el formato: yyyy-MM-dd");
            return;
        }

        galeria.setDirector(txtDirector.getText().trim());

        model.insertGaleria(galeria);
        Utilities.showInfoAlert("Galería guardada exitosamente");
        limpiarFormulario();
        cargarTabla();
    }

    private void actualizarGaleria() {
        if (galeriaSeleccionada == null) {
            Utilities.showErrorAlert("Selecciona una galería de la tabla");
            return;
        }

        if (!validarFormulario()) {
            return;
        }

        galeriaSeleccionada.setNombre(txtNombre.getText().trim());

        Localizacion loc = (Localizacion) comboLocalizacion.getSelectedItem();
        galeriaSeleccionada.setLocalizacion(loc.getNombre());

        galeriaSeleccionada.setEmpleados((Integer) spinnerEmpleados.getValue());

        // Parsear fecha
        try {
            if (!txtFechaFundacion.getText().trim().isEmpty()) {
                LocalDate fecha = LocalDate.parse(txtFechaFundacion.getText().trim(), dateFormatter);
                galeriaSeleccionada.setFechaFundacion(fecha);
            }
        } catch (Exception e) {
            Utilities.showErrorAlert("Fecha inválida. Usa el formato: yyyy-MM-dd");
            return;
        }

        galeriaSeleccionada.setDirector(txtDirector.getText().trim());

        model.updateGaleria(galeriaSeleccionada);
        Utilities.showInfoAlert("Galería actualizada exitosamente");
        limpiarFormulario();
        cargarTabla();
    }

    private void eliminarGaleria() {
        int selectedRow = tableGalerias.getSelectedRow();

        if (selectedRow == -1) {
            Utilities.showErrorAlert("Selecciona una galería de la tabla");
            return;
        }

        int confirm = Utilities.confirmMessage(
            "¿Estás seguro de eliminar esta galería?\nEsta acción no se puede deshacer.", 
            "Confirmar eliminación"
        );

        if (confirm == JOptionPane.YES_OPTION) {
            int idGaleria = (int) tableGalerias.getValueAt(selectedRow, 0);
            model.deleteGaleria(idGaleria);
            Utilities.showInfoAlert("Galería eliminada exitosamente");
            limpiarFormulario();
            cargarTabla();
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        comboLocalizacion.setSelectedIndex(0);
        spinnerEmpleados.setValue(10);
        txtFechaFundacion.setText("");
        txtDirector.setText("");

        galeriaSeleccionada = null;
        btnActualizar.setEnabled(false);
        btnGuardar.setEnabled(true);
        tableGalerias.clearSelection();
    }

    private boolean validarFormulario() {
        if (txtNombre.getText().trim().isEmpty()) {
            Utilities.showErrorAlert("El nombre es obligatorio");
            txtNombre.requestFocus();
            return false;
        }

        if (txtDirector.getText().trim().isEmpty()) {
            Utilities.showErrorAlert("El director es obligatorio");
            txtDirector.requestFocus();
            return false;
        }

        if (!txtFechaFundacion.getText().trim().isEmpty()) {
            try {
                LocalDate.parse(txtFechaFundacion.getText().trim(), dateFormatter);
            } catch (Exception e) {
                Utilities.showErrorAlert("Fecha inválida. Usa el formato: yyyy-MM-dd");
                txtFechaFundacion.requestFocus();
                return false;
            }
        }

        return true;
    }

    public void refrescarDatos() {
        cargarTabla();
    }

}
