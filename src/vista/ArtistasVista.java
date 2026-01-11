package vista;

import modelo.Model;
import modelo.entity.Artista;
import modelo.entity.enums.Pais;
import util.Utilities;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Vista para gestionar artistas
 * Campos: nombreArtistico, nombreReal, edad, pais, fechaPrimeraObra, exposicionActiva
 */
public class ArtistasVista extends JPanel {
    private Model model;

    // Componentes vinculados al .form
    private JPanel panelPrincipal;
    private JPanel topPanel;
    private JLabel lblTituloPrincipal;
    private JPanel searchPanel;
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JButton btnFiltrarActivos;
    private JPanel tablePanel;
    private JScrollPane scrollPane;
    private JTable tableArtistas;
    private JPanel formPanel;
    private JTextField txtNombreArtistico;
    private JTextField txtNombreReal;
    private JSpinner spinnerEdad;
    private JComboBox<Pais> comboPais;
    private JTextField txtFechaPrimeraObra;
    private JCheckBox checkExposicionActiva;
    private JPanel buttonPanel;
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private DefaultTableModel dtmArtistas;
    private Artista artistaSeleccionado;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ArtistasVista(Model model) {
        this.model = model;
        setLayout(new BorderLayout());
        add(panelPrincipal, BorderLayout.CENTER);

        initializeComponents();
        cargarCombos();
        cargarTabla();
    }

    private void initializeComponents() {
        // Configurar tabla
        String[] columnas = {"ID", "Nombre Artístico", "Nombre Real", "Edad", 
                            "País", "Primera Obra", "Exposición Activa"};
        dtmArtistas = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableArtistas.setModel(dtmArtistas);
        tableArtistas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Spinner
        spinnerEdad.setModel(new SpinnerNumberModel(25, 18, 100, 1));

        // Listeners
        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarArtista());
        btnActualizar.addActionListener(e -> actualizarArtista());
        btnEliminar.addActionListener(e -> eliminarArtista());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnBuscar.addActionListener(e -> buscarArtistas());
        btnFiltrarActivos.addActionListener(e -> filtrarActivos());

        tableArtistas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarArtistaSeleccionado();
            }
        });

        btnActualizar.setEnabled(false);
    }

    private void cargarCombos() {
        comboPais.removeAllItems();
        for (Pais pais : Pais.values()) {
            comboPais.addItem(pais);
        }
    }

    private void cargarTabla() {
        dtmArtistas.setRowCount(0);
        List<Artista> artistas = model.getAllArtistas();

        for (Artista artista : artistas) {
            Object[] fila = {
                artista.getIdArtista(),
                artista.getNombreArtistico(),
                artista.getNombreReal(),
                artista.getEdad() + " años",
                artista.getPais(),
                artista.getFechaPrimeraObra() != null ? 
                    artista.getFechaPrimeraObra().format(dateFormatter) : "N/A",
                artista.getExposicionActiva() ? "Sí" : "No"
            };
            dtmArtistas.addRow(fila);
        }
    }

    private void buscarArtistas() {
        String keyword = txtBuscar.getText().trim();

        if (keyword.isEmpty()) {
            cargarTabla();
            return;
        }

        dtmArtistas.setRowCount(0);
        List<Artista> artistas = model.searchArtistas(keyword);

        if (artistas.isEmpty()) {
            Utilities.showInfoAlert("No se encontraron artistas con: " + keyword);
            return;
        }

        for (Artista artista : artistas) {
            Object[] fila = {
                artista.getIdArtista(),
                artista.getNombreArtistico(),
                artista.getNombreReal(),
                artista.getEdad() + " años",
                artista.getPais(),
                artista.getFechaPrimeraObra() != null ? 
                    artista.getFechaPrimeraObra().format(dateFormatter) : "N/A",
                artista.getExposicionActiva() ? "Sí" : "No"
            };
            dtmArtistas.addRow(fila);
        }
    }

    private void filtrarActivos() {
        dtmArtistas.setRowCount(0);
        List<Artista> artistas = model.getArtistasActivos();

        if (artistas.isEmpty()) {
            Utilities.showInfoAlert("No hay artistas con exposiciones activas");
            return;
        }

        for (Artista artista : artistas) {
            Object[] fila = {
                artista.getIdArtista(),
                artista.getNombreArtistico(),
                artista.getNombreReal(),
                artista.getEdad() + " años",
                artista.getPais(),
                artista.getFechaPrimeraObra() != null ? 
                    artista.getFechaPrimeraObra().format(dateFormatter) : "N/A",
                artista.getExposicionActiva() ? "Sí" : "No"
            };
            dtmArtistas.addRow(fila);
        }

        Utilities.showInfoAlert("Mostrando " + artistas.size() + " artistas activos");
    }

    private void cargarArtistaSeleccionado() {
        int selectedRow = tableArtistas.getSelectedRow();

        if (selectedRow == -1) {
            return;
        }

        int idArtista = (int) tableArtistas.getValueAt(selectedRow, 0);
        artistaSeleccionado = model.findArtistaById(idArtista);

        if (artistaSeleccionado != null) {
            txtNombreArtistico.setText(artistaSeleccionado.getNombreArtistico());
            txtNombreReal.setText(artistaSeleccionado.getNombreReal());
            spinnerEdad.setValue(artistaSeleccionado.getEdad());

            try {
                Pais pais = Pais.fromNombre(artistaSeleccionado.getPais());
                if (pais != null) {
                    comboPais.setSelectedItem(pais);
                }
            } catch (Exception e) {
                comboPais.setSelectedIndex(0);
            }

            if (artistaSeleccionado.getFechaPrimeraObra() != null) {
                txtFechaPrimeraObra.setText(artistaSeleccionado.getFechaPrimeraObra().format(dateFormatter));
            } else {
                txtFechaPrimeraObra.setText("");
            }

            checkExposicionActiva.setSelected(artistaSeleccionado.getExposicionActiva());

            btnActualizar.setEnabled(true);
            btnGuardar.setEnabled(false);
        }
    }

    private void guardarArtista() {
        if (!validarFormulario()) {
            return;
        }

        Artista artista = new Artista();
        artista.setNombreArtistico(txtNombreArtistico.getText().trim());
        artista.setNombreReal(txtNombreReal.getText().trim());
        artista.setEdad((Integer) spinnerEdad.getValue());

        Pais pais = (Pais) comboPais.getSelectedItem();
        artista.setPais(pais.getNombre());

        // Parsear fecha
        try {
            if (!txtFechaPrimeraObra.getText().trim().isEmpty()) {
                LocalDate fecha = LocalDate.parse(txtFechaPrimeraObra.getText().trim(), dateFormatter);
                artista.setFechaPrimeraObra(fecha);
            }
        } catch (Exception e) {
            Utilities.showErrorAlert("Fecha inválida. Usa el formato: yyyy-MM-dd");
            return;
        }

        artista.setExposicionActiva(checkExposicionActiva.isSelected());

        model.insertArtista(artista);
        Utilities.showInfoAlert("Artista guardado exitosamente");
        limpiarFormulario();
        cargarTabla();
    }

    private void actualizarArtista() {
        if (artistaSeleccionado == null) {
            Utilities.showErrorAlert("Selecciona un artista de la tabla");
            return;
        }

        if (!validarFormulario()) {
            return;
        }

        artistaSeleccionado.setNombreArtistico(txtNombreArtistico.getText().trim());
        artistaSeleccionado.setNombreReal(txtNombreReal.getText().trim());
        artistaSeleccionado.setEdad((Integer) spinnerEdad.getValue());

        Pais pais = (Pais) comboPais.getSelectedItem();
        artistaSeleccionado.setPais(pais.getNombre());

        // Parsear fecha
        try {
            if (!txtFechaPrimeraObra.getText().trim().isEmpty()) {
                LocalDate fecha = LocalDate.parse(txtFechaPrimeraObra.getText().trim(), dateFormatter);
                artistaSeleccionado.setFechaPrimeraObra(fecha);
            }
        } catch (Exception e) {
            Utilities.showErrorAlert("Fecha inválida. Usa el formato: yyyy-MM-dd");
            return;
        }

        artistaSeleccionado.setExposicionActiva(checkExposicionActiva.isSelected());

        model.updateArtista(artistaSeleccionado);
        Utilities.showInfoAlert("Artista actualizado exitosamente");
        limpiarFormulario();
        cargarTabla();
    }

    private void eliminarArtista() {
        int selectedRow = tableArtistas.getSelectedRow();

        if (selectedRow == -1) {
            Utilities.showErrorAlert("Selecciona un artista de la tabla");
            return;
        }

        int confirm = Utilities.confirmMessage(
            "¿Estás seguro de eliminar este artista?\nEsta acción no se puede deshacer.", 
            "Confirmar eliminación"
        );

        if (confirm == JOptionPane.YES_OPTION) {
            int idArtista = (int) tableArtistas.getValueAt(selectedRow, 0);
            model.deleteArtista(idArtista);
            Utilities.showInfoAlert("Artista eliminado exitosamente");
            limpiarFormulario();
            cargarTabla();
        }
    }

    private void limpiarFormulario() {
        txtNombreArtistico.setText("");
        txtNombreReal.setText("");
        spinnerEdad.setValue(25);
        comboPais.setSelectedIndex(0);
        txtFechaPrimeraObra.setText("");
        checkExposicionActiva.setSelected(false);

        artistaSeleccionado = null;
        btnActualizar.setEnabled(false);
        btnGuardar.setEnabled(true);
        tableArtistas.clearSelection();
    }

    private boolean validarFormulario() {
        if (txtNombreArtistico.getText().trim().isEmpty()) {
            Utilities.showErrorAlert("El nombre artístico es obligatorio");
            txtNombreArtistico.requestFocus();
            return false;
        }

        if (txtNombreReal.getText().trim().isEmpty()) {
            Utilities.showErrorAlert("El nombre real es obligatorio");
            txtNombreReal.requestFocus();
            return false;
        }

        if (!txtFechaPrimeraObra.getText().trim().isEmpty()) {
            try {
                LocalDate.parse(txtFechaPrimeraObra.getText().trim(), dateFormatter);
            } catch (Exception e) {
                Utilities.showErrorAlert("Fecha inválida. Usa el formato: yyyy-MM-dd");
                txtFechaPrimeraObra.requestFocus();
                return false;
            }
        }

        return true;
    }

    public void refrescarDatos() {
        cargarTabla();
    }

}
