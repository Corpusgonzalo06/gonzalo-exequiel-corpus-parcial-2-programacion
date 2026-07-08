package models;

public class Alimenticio extends Producto {
    private String fechaVencimiento;

    public Alimenticio() {
        super();
    }

    public Alimenticio(String codigo, String marca, String modelo, double precio, int stock, Proveedor proveedor, String fechaVencimiento) {
        super(codigo, marca, modelo, precio, stock, proveedor);
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
}