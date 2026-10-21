module ni.edu.uam.practica_sincronizacionycontroldehilos {
    requires javafx.controls;
    requires javafx.fxml;


    opens ni.edu.uam.practica_sincronizacionycontroldehilos to javafx.fxml;
    exports ni.edu.uam.practica_sincronizacionycontroldehilos;
}