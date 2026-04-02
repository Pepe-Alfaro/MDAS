package bridge;
public class Sofa extends Producto {
    private int numeroPlazas;

    public Sofa(String id, String nombre, double precio, int unidadesDisponibles, String material, String color, int numeroPlazas) {
        super(id, nombre, precio, unidadesDisponibles, material, color);
        this.numeroPlazas = numeroPlazas;
    }

    public int getNumeroPlazas() {
        return numeroPlazas;
    }
}