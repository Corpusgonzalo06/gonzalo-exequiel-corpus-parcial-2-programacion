package parcialdos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Principal.fxml"));
            Parent root = loader.load();

        
            Scene scene = new Scene(root, 900, 600);

            stage.setTitle("Sistema de Gestión de Inventario - Parcial 2");
            stage.setScene(scene);
            stage.show();
            
        } catch (Exception e) {
            System.err.println("Error al levantar la interfaz Principal.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}