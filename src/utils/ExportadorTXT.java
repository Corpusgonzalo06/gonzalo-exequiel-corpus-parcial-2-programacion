package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import models.Producto;

public class ExportadorTXT {
    private static final String RUTA_EXPORTADO = "productos_caros.txt";

    public void exportarProductosCaros(List<Producto> productos) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_EXPORTADO))) {
            bw.write("=== REPORTE DE PRODUCTOS CON PRECIO SUPERIOR A $500.000 ===");
            bw.newLine();
            bw.write("==========================================================");
            bw.newLine();
            bw.newLine();

            for (Producto p : productos) {
                if (p.getPrecio() > 500000) {
                    String linea = String.format("Código: %s | %s %s | Precio: $%.2f | Stock: %d",
                            p.getCodigo(),
                            p.getMarca(),
                            p.getModelo(),
                            p.getPrecio(),
                            p.getStock()
                    );
                    bw.write(linea);
                    bw.newLine();
                }
            }
            System.out.println("Reporte .txt exportado correctamente.");
        }
    }

    public String obtenerContenidoReporte() {
        StringBuilder contenido = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_EXPORTADO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
        } catch (IOException e) {
            return "No se encontró el archivo de reporte generado. Primero debe exportar los datos.";
        }
        return contenido.toString();
    }
}