package bridge;
public class Mesa extends Producto {
    private String dimensiones;

    public Mesa(String id, String nombre, double precio, int unidadesDisponibles, String material, String color, String dimensiones) {
        super(id, nombre, precio, unidadesDisponibles, material, color);
        this.dimensiones = dimensiones;
    }

    public String getDimensiones() {
        return dimensiones;
    }
}