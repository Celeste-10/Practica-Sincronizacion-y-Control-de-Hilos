package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import model.ContadorSeguro;

public class ProcesadorController {
    @FXML private Label lblTotal;
    @FXML private Label lblEstado;
    @FXML private ProgressBar barra1;
    @FXML private ProgressBar barra2;
    @FXML private ListView<String> listHistorial;
    @FXML private Button btnIniciar;
    @FXML private Button btnCancelar;
    @FXML private Button btnCancelarA;
    @FXML private Button btnReiniciar;

    private final ContadorSeguro contadorModel = new ContadorSeguro();
    private Task<Void> tarea1;
    private Task<Void> tarea2;

    @FXML
    public void initialize() {
        btnCancelar.setDisable(true);
        btnCancelarA.setDisable(true);
        btnReiniciar.setDisable(true);
    }

    @FXML
    private void iniciarProcesamiento() {
        btnIniciar.setDisable(true);
        btnCancelar.setDisable(false);
        btnCancelarA.setDisable(false);
        btnReiniciar.setDisable(true);
        lblEstado.setText("Estado: Procesando...");

        tarea1 = crearTarea("Operador A");
        tarea2 = crearTarea("Operador B");

        barra1.progressProperty().bind(tarea1.progressProperty());
        barra2.progressProperty().bind(tarea2.progressProperty());

        Runnable actualizarEstadoUI = () -> {
            if ((tarea1 == null || tarea1.isDone()) && (tarea2 == null || tarea2.isDone())) {
                btnCancelar.setDisable(true);
                btnCancelarA.setDisable(true);
                btnReiniciar.setDisable(false); // Reto 1: Habilita reiniciar solo cuando las tareas anteriores hayan terminado
                lblEstado.setText("Estado: Tareas finalizadas o canceladas.");
            }
        };

        tarea1.setOnSucceeded(ev -> actualizarEstadoUI.run());
        tarea1.setOnCancelled(ev -> {
            btnCancelarA.setDisable(true); // Reto 3: Deshabilita el botón si la Tarea A se canceló
            actualizarEstadoUI.run();
        });
        tarea1.setOnFailed(ev -> actualizarEstadoUI.run());

        tarea2.setOnSucceeded(ev -> actualizarEstadoUI.run());
        tarea2.setOnCancelled(ev -> actualizarEstadoUI.run());
        tarea2.setOnFailed(ev -> actualizarEstadoUI.run());

        Thread hilo1 = new Thread(tarea1, "operador-a");
        Thread hilo2 = new Thread(tarea2, "operador-b");
        hilo1.setDaemon(true);
        hilo2.setDaemon(true);

        hilo1.start();
        hilo2.start();
    }

    private Task<Void> crearTarea(String operador) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 1; i <= 20; i++) {
                    if (isCancelled()) break;

                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException ex) {
                        if (isCancelled()) break;
                        Thread.currentThread().interrupt();
                        throw ex;
                    }

                    if (isCancelled()) break;

                    // Intentar procesar stock e incrementar contador de forma atómica
                    int[] resultado = contadorModel.procesarSolicitud();

                    // Si es null, el inventario llegó a 0; salimos sin alterar contadores
                    if (resultado == null) {
                        break;
                    }

                    int totalActual = resultado[0];
                    int stockActual = resultado[1];

                    updateProgress(i, 20);

                    final int numSolicitud = i;

                    // Actualización segura del hilo gráfico
                    Platform.runLater(() -> {
                        String estadoStock = (stockActual > 0) ? String.valueOf(stockActual) : "AGOTADO";
                        lblTotal.setText(String.format("Procesadas: %d | Inventario: %s", totalActual, estadoStock));

                        listHistorial.getItems().add(
                                String.format("[%s] Solicitud #%d procesada. (Total: %d)", operador, numSolicitud, totalActual)
                        );
                    });
                }
                return null;
            }
        };
    }

    @FXML
    private void cancelarTodo() {
        if (tarea1 != null) tarea1.cancel(true);
        if (tarea2 != null) tarea2.cancel(true);
        btnCancelar.setDisable(true);
        btnCancelarA.setDisable(true);
        lblEstado.setText("Estado: Cancelación solicitada...");
    }

    // Reto 3: Cancelar únicamente el Operador A
    @FXML
    private void cancelarOperadorA() {
        if (tarea1 != null) {
            tarea1.cancel(true);
            btnCancelarA.setDisable(true);
            lblEstado.setText("Estado: Operador A cancelado. Operador B continúa...");
        }
    }

    // Reto 1: Reiniciar contador y GUI
    @FXML
    private void reiniciarConteo() {
        contadorModel.reiniciar();
        barra1.progressProperty().unbind();
        barra2.progressProperty().unbind();
        barra1.setProgress(0);
        barra2.setProgress(0);
        listHistorial.getItems().clear();
        lblTotal.setText("Procesadas: 0 | Inventario: 25");
        lblEstado.setText("Estado: Sin iniciar");

        btnIniciar.setDisable(false);
        btnReiniciar.setDisable(true);
    }

    public void detenerTareasAlCerrar() {
        if (tarea1 != null) tarea1.cancel(true);
        if (tarea2 != null) tarea2.cancel(true);
    }
}
