package parcialdos;

import exceptions.CodigoDuplicadoException;
import exceptions.PrecioInvalidoException;
import exceptions.StockInvalidoException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Alimenticio;
import models.Electronico;
import models.Producto;
import models.Proveedor;
import utils.CalculadoraInventario;
import utils.ExportadorTXT;
import utils.GestorProductos;
import utils.GestorProveedores;

public class PrincipalController implements Initializable {

    //==================== COMPONENTES DE LA INTERFAZ (FXML) ====================

    // Campos del formulario
    @FXML private TextField txtCodigo;
    @FXML private TextField txtMarca;
    @FXML private TextField txtModelo;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;

    // Proveedor seleccionado desde el ComboBox
    @FXML private ComboBox<Proveedor> cmbProveedor;

    // Selección del tipo de producto
    @FXML private RadioButton rbAlimenticio;
    @FXML private RadioButton rbElectronico;

    // Campo que cambia entre vencimiento o garantía
    @FXML private TextField txtAtributoEspecial;
    @FXML private Label lblAtributoEspecial;

    // Tabla donde se muestran los productos del inventario
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colMarca;
    @FXML private TableColumn<Producto, String> colModelo;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, String> colProveedor;


    //==================== CLASES QUE MANEJAN LA LÓGICA DEL SISTEMA ====================

    private GestorProductos gestorProductos;
    private GestorProveedores gestorProveedores;
    private CalculadoraInventario calculadora;
    private ExportadorTXT exportador;


    //==================== CONFIGURACIÓN INICIAL DE LA VENTANA ====================

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Inicializa las clases encargadas de la lógica del sistema
        gestorProductos = new GestorProductos();
        gestorProveedores = new GestorProveedores();
        calculadora = new CalculadoraInventario();
        exportador = new ExportadorTXT();

        // Configura las columnas de la TableView
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        // Muestra la razón social del proveedor en lugar del objeto completo
        colProveedor.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getProveedor().getRazonSocial())
        );

        // Carga los proveedores desde el archivo CSV al ComboBox
        gestorProveedores.cargarProveedoresDesdeCSV("proveedores.csv");
        cmbProveedor.setItems(
                FXCollections.observableArrayList(
                        gestorProveedores.getListaProveedores()));

        // Recupera los productos guardados anteriormente y actualiza la tabla
        gestorProductos.cargarProductosPrevios(gestorProveedores);
        actualizarTabla();

        // Listener: cuando se selecciona un producto, carga sus datos en el formulario
        tblProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarCamposDesdeTabla(newSelection);
            }
        });
    }

    @FXML
    private void handleCambioTipo() {
        if (rbAlimenticio.isSelected()) {
            lblAtributoEspecial.setText("Vencimiento (DD/MM/AAAA):");
        } else {
            lblAtributoEspecial.setText("Garantía (Meses):");
        }
    }

    @FXML
    private void handleAgregar() {
        try {
            String codigo = txtCodigo.getText();
            String marca = txtMarca.getText();
            String modelo = txtModelo.getText();
            double precio = Double.parseDouble(txtPrecio.getText());
            int stock = Integer.parseInt(txtStock.getText());
            Proveedor prov = cmbProveedor.getValue();
            String espec = txtAtributoEspecial.getText();

            if (prov == null || codigo.isEmpty() || espec.isEmpty()) {
                mostrarAlerta("Error", "Por favor completa todos los campos requeridos.", Alert.AlertType.WARNING);
                return;
            }

            Producto nuevo;
            if (rbAlimenticio.isSelected()) {
                nuevo = new Alimenticio(codigo, marca, modelo, precio, stock, prov, espec);
            } else {
                int garantia = Integer.parseInt(espec);
                nuevo = new Electronico(codigo, marca, modelo, precio, stock, prov, garantia);
            }

            gestorProductos.agregarProducto(nuevo);
            actualizarTabla();
            limpiarCampos();
            mostrarAlerta("Éxito", "Producto agregado correctamente.", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "Precio, Stock o Garantía deben ser números válidos.", Alert.AlertType.ERROR);
        } catch (CodigoDuplicadoException | PrecioInvalidoException | StockInvalidoException e) {
            mostrarAlerta("Validación de Negocio", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    
    @FXML
    private void handleModificar() {
        try {
            Producto seleccionado = tblProductos.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                mostrarAlerta("Atención", "Selecciona un producto de la tabla para modificar.", Alert.AlertType.WARNING);
                return;
            }

            String marca = txtMarca.getText();
            String modelo = txtModelo.getText();
            double precio = Double.parseDouble(txtPrecio.getText());
            int stock = Integer.parseInt(txtStock.getText());

            gestorProductos.modificarProducto(seleccionado.getCodigo(), marca, modelo, precio, stock);
            
         
            if (seleccionado instanceof Alimenticio) {
                ((Alimenticio) seleccionado).setFechaVencimiento(txtAtributoEspecial.getText());
            } else if (seleccionado instanceof Electronico) {
                ((Electronico) seleccionado).setGarantiaMeses(Integer.parseInt(txtAtributoEspecial.getText()));
            }

            actualizarTabla();
            limpiarCampos();
            mostrarAlerta("Éxito", "Producto modificado correctamente.", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "Verifica los campos numéricos.", Alert.AlertType.ERROR);
        } catch (PrecioInvalidoException | StockInvalidoException e) {
            mostrarAlerta("Validación", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleEliminar() {
        Producto seleccionado = tblProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Selecciona un producto de la tabla para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        gestorProductos.eliminarProducto(seleccionado.getCodigo());
        actualizarTabla();
        limpiarCampos();
        mostrarAlerta("Éxito", "Producto eliminado correctamente.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleVerTotalInventario() {
        double total = calculadora.calcularValorTotal(gestorProductos.getListaProductos());
        mostrarAlerta("Total Inventario", "El valor total acumulado del inventario es:\n$" + String.format("%.2f", total), Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleExportarCaros() {
        try {
            exportador.exportarProductosCaros(gestorProductos.getListaProductos());
            mostrarAlerta("Exportación", "Productos de más de $500.000 exportados a productos_caros.txt", Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo escribir el archivo .txt", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleVisualizarReporte() {
        String reporte = exportador.obtenerContenidoReporte(); 
        mostrarAlerta("Contenido de productos_caros.txt", reporte, Alert.AlertType.INFORMATION);
    }

    private void actualizarTabla() {
        tblProductos.setItems(FXCollections.observableArrayList(gestorProductos.getListaProductos()));
    }

    private void cargarCamposDesdeTabla(Producto p) {
        txtCodigo.setText(p.getCodigo());
        txtCodigo.setEditable(false); 
        txtMarca.setText(p.getMarca());
        txtModelo.setText(p.getModelo());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtStock.setText(String.valueOf(p.getStock()));
        cmbProveedor.setValue(p.getProveedor()); // Carga el objeto directamente

        if (p instanceof Alimenticio) {
            rbAlimenticio.setSelected(true);
            txtAtributoEspecial.setText(((Alimenticio) p).getFechaVencimiento());
            lblAtributoEspecial.setText("Vencimiento (DD/MM/AAAA):");
        } else {
            rbElectronico.setSelected(true);
            txtAtributoEspecial.setText(String.valueOf(((Electronico) p).getGarantiaMeses()));
            lblAtributoEspecial.setText("Garantía (Meses):");
        }
    }

    private void limpiarCampos() {
        txtCodigo.setText("");
        txtCodigo.setEditable(true);
        txtMarca.setText("");
        txtModelo.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        txtAtributoEspecial.setText("");
        cmbProveedor.setValue(null);
        tblProductos.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}