package bridge;
public abstract class Producto {
    private String id;
    private String nombre;
    private double precio;
    private int unidadesDisponibles;
    private String material;
    private String color;

    public Producto(String id, String nombre, double precio, int unidadesDisponibles, String material, String color) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.unidadesDisponibles = unidadesDisponibles;
        this.material = material;
        this.color = color;
    }

    public String getId() { 
        return id; 
    }
    public String getNombre() { 
        return nombre; 
    }
    public double getPrecio() { 
        return precio; 
    }
    public int getUnidadesDisponibles() { 
        return unidadesDisponibles; 
    }
    public String getMaterial() { 
        return material; 
    }
    public String getColor() { 
        return color; 
    }

    public void setUnidadesDisponibles(int unidadesDisponibles) {
        this.unidadesDisponibles = unidadesDisponibles;
    }
    
    @Override
    public String toString() {
        return nombre + " (Precio: " + precio + "€, Stock: " + unidadesDisponibles + ")";
    }
}