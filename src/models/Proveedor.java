package models;

public class Proveedor {
    private int id;
    private String razonSocial;
    private String telefono;
    private String email;
    private String ciudad;

 
    public Proveedor() {
    }


    public Proveedor(int id, String razonSocial, String telefono, String email, String ciudad) {
        this.id = id;
        this.razonSocial = razonSocial;
        this.telefono = telefono;
        this.email = email;
        this.ciudad = ciudad;
    }


    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }

    public String getRazonSocial() { 
        return razonSocial; 
    }
    
    public void setRazonSocial(String razonSocial) { 
        this.razonSocial = razonSocial; 
    }

    public String getTelefono() { 
        return telefono; 
    }
    
    public void setTelefono(String telefono) { 
        this.telefono = telefono; 
    }

    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }

    public String getCiudad() { 
        return ciudad; 
    }
    
    public void setCiudad(String ciudad) { 
        this.ciudad = ciudad; 
    }


    @Override
    public String toString() {
        return this.razonSocial != null ? this.razonSocial : "";
    }
}