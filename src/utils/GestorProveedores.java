package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import models.Proveedor;

public class GestorProveedores {
    private List<Proveedor> listaProveedores;

    public GestorProveedores() {
        this.listaProveedores = new ArrayList<>();
    }

    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    public void cargarProveedoresDesdeCSV(String rutaArchivo) {
        listaProveedores.clear(); // Limpiamos la lista por seguridad antes de cargar
        
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            boolean esPrimeraLinea = true;

            while ((linea = br.readLine()) != null) {
                // Saltamos la línea del encabezado (id, razonSocial, telefono...)
                if (esPrimeraLinea) {
                    esPrimeraLinea = false;
                    continue;
                }

              
                String[] datos = linea.split(",");
                if (datos.length >= 5) {
                    int id = Integer.parseInt(datos[0].trim());
                    String razonSocial = datos[1].trim();
                    String telefono = datos[2].trim();
                    String email = datos[3].trim();
                    String ciudad = datos[4].trim();

                 
                    Proveedor proveedor = new Proveedor(id, razonSocial, telefono, email, ciudad);
                    listaProveedores.add(proveedor);
                }
            }
            System.out.println("Proveedores cargados con éxito. Total: " + listaProveedores.size());
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al cargar el archivo de proveedores: " + e.getMessage());
        }
    }


    public Proveedor buscarPorId(int id) {
        for (Proveedor p : listaProveedores) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }
}