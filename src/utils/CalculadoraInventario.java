package utils;

import java.util.List;
import models.Producto;

public class CalculadoraInventario {

    public double calcularValorTotal(List<Producto> productos) {
        double total = 0;
        for (Producto p : productos) {
            total += p.getPrecio() * p.getStock();
        }
        return total;
    }
}