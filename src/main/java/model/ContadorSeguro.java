package model;

public class ContadorSeguro {
    private int contador = 0;
    private int inventario = 25; // Reto 4: Inventario inicial de 25 unidades

    public synchronized int incrementar() {
        return ++contador;
    }

    public synchronized int obtener() {
        return contador;
    }

    public synchronized int retirarUnidad() {
        if (inventario > 0) {
            inventario--;
            return inventario;
        }
        return -1;
    }

    public synchronized int obtenerInventario() {
        return inventario;
    }

    // Reto 1: Boton Reiniciar
    public synchronized void reiniciar() {
        this.contador = 0;
        this.inventario = 25;
    }
}
