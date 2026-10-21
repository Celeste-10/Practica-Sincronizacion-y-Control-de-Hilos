package ni.edu.uam.practica_sincronizacionycontroldehilos;

import controller.ProcesadorController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProcesadorApp extends Application {
    @Override
    public void start(Stage ventana) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("procesador.fxml"));
        Scene scene = new Scene(loader.load(), 480, 600);

        ProcesadorController controller = loader.getController();

        ventana.setTitle("Procesamiento Concurrente - JavaFX");
        ventana.setScene(scene);

        ventana.setOnCloseRequest(e -> controller.detenerTareasAlCerrar());

        ventana.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
