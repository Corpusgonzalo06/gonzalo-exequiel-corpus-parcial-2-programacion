package utils;

import exceptions.CodigoDuplicadoException;
import exceptions.PrecioInvalidoException;
import exceptions.StockInvalidoException;
import java.util.ArrayList;
import java.util.List;
import models.Producto;

public class GestorProductos {
    private List<Producto> listaProductos;
    private PersistenciaProductos persistencia;

    public GestorProductos() {
        this.listaProductos = new ArrayList<>();
        this.persistencia = new PersistenciaProductos();
    }

    public List<Producto> getListaProductos() {
        return listaProductos;
    }


    public void cargarProductosPrevios(GestorProveedores gestorProveedores) {
        this.listaProductos = persistencia.recuperarProductos(gestorProveedores);
    }

  
    public void agregarProducto(Producto nuevoProducto) 
            throws CodigoDuplicadoException, PrecioInvalidoException, StockInvalidoException {
        
  
        if (nuevoProducto.getPrecio() <= 0) {
            throw new PrecioInvalidoException("El precio del producto debe ser mayor a cero.");
        }
        if (nuevoProducto.getStock() < 0) {
            throw new StockInvalidoException("El stock inicial no puede ser un número negativo.");
        }
        if (buscarPorCodigo(nuevoProducto.getCodigo()) != null) {
            throw new CodigoDuplicadoException("Ya existe un producto registrado con el código: " + nuevoProducto.getCodigo());
        }

        listaProductos.add(nuevoProducto);
        persistencia.guardarProductos(listaProductos);
    }


    public void modificarProducto(String codigo, String nuevaMarca, String nuevoModelo, double nuevoPrecio, int nuevoStock) 
            throws PrecioInvalidoException, StockInvalidoException {
        
        if (nuevoPrecio <= 0) {
            throw new PrecioInvalidoException("El precio modificado debe ser mayor a cero.");
        }
        if (nuevoStock < 0) {
            throw new StockInvalidoException("El stock modificado no puede ser negativo.");
        }

        Producto p = buscarPorCodigo(codigo);
        if (p != null) {
            p.setMarca(nuevaMarca);
            p.setModelo(nuevoModelo);
            p.setPrecio(nuevoPrecio);
            p.setStock(nuevoStock);
            
            persistencia.guardarProductos(listaProductos); // Guardado automático
        }
    }

 
    public void eliminarProducto(String codigo) {
        Producto p = buscarPorCodigo(codigo);
        if (p != null) {
            listaProductos.remove(p);
            persistencia.guardarProductos(listaProductos); // Guardado automático
        }
    }

    public Producto buscarPorCodigo(String codigo) {
        for (Producto p : listaProductos) {
            if (p.getCodigo().equalsIgnoreCase(codigo)) {
                return p;
            }
        }
        return null;
    }
}