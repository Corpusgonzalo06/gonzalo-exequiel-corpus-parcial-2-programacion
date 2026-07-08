package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import models.Alimenticio;
import models.Electronico;
import models.Producto;
import models.Proveedor;

public class PersistenciaProductos {
    private static final String RUTA_PRODUCTOS = "productos.csv";


    public void guardarProductos(List<Producto> productos) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_PRODUCTOS))) {
        
            bw.write("tipo,codigo,marca,modelo,precio,stock,idProveedor,atributoEspecial");
            bw.newLine();

            for (Producto p : productos) {
                String tipo = (p instanceof Alimenticio) ? "ALIMENTICIO" : "ELECTRONICO";
                String atributoEspecial = (p instanceof Alimenticio) 
                        ? ((Alimenticio) p).getFechaVencimiento() 
                        : String.valueOf(((Electronico) p).getGarantiaMeses());

                String linea = String.format("%s,%s,%s,%s,%.2f,%d,%d,%s",
                        tipo,
                        p.getCodigo(),
                        p.getMarca(),
                        p.getModelo(),
                        p.getPrecio(),
                        p.getStock(),
                        p.getProveedor().getId(),
                        atributoEspecial
                );
                bw.write(linea);
                bw.newLine();
            }
            System.out.println("Productos guardados automáticamente.");
        } catch (IOException e) {
            System.err.println("Error al guardar productos: " + e.getMessage());
        }
    }


    public List<Producto> recuperarProductos(GestorProveedores gestorProveedores) {
        List<Producto> productosRecuperados = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_PRODUCTOS))) {
            String linea;
            boolean esPrimeraLinea = true;

            while ((linea = br.readLine()) != null) {
                if (esPrimeraLinea) {
                    esPrimeraLinea = false;
                    continue;
                }

                String[] datos = linea.split(",");
                if (datos.length >= 8) {
                    String tipo = datos[0].trim();
                    String codigo = datos[1].trim();
                    String marca = datos[2].trim();
                    String modelo = datos[3].trim();
                   
                    double precio = Double.parseDouble(datos[4].trim().replace(",", "."));
                    int stock = Integer.parseInt(datos[5].trim());
                    int idProveedor = Integer.parseInt(datos[6].trim());
                    String atributoEspecial = datos[7].trim();

                   
                    Proveedor prov = gestorProveedores.buscarPorId(idProveedor);
                    if (prov == null) continue; // 

                    if (tipo.equals("ALIMENTICIO")) {
                        productosRecuperados.add(new Alimenticio(codigo, marca, modelo, precio, stock, prov, atributoEspecial));
                    } else if (tipo.equals("ELECTRONICO")) {
                        int garantia = Integer.parseInt(atributoEspecial);
                        productosRecuperados.add(new Electronico(codigo, marca, modelo, precio, stock, prov, garantia));
                    }
                }
            }
            System.out.println("Productos recuperados con éxito.");
        } catch (IOException | NumberFormatException e) {
            System.out.println("No se encontró archivo previo de productos o está vacío. Se iniciará una lista nueva.");
        }
        
        return productosRecuperados;
    }
}