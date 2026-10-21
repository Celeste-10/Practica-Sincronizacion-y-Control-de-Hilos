module ni.edu.uam.practica_sincronizacionycontroldehilos {
    requires javafx.controls;
    requires javafx.fxml;

    opens controller to javafx.fxml;
    opens model to java.base;
    opens ni.edu.uam.practica_sincronizacionycontroldehilos to javafx.fxml;
    exports ni.edu.uam.practica_sincronizacionycontroldehilos;
}