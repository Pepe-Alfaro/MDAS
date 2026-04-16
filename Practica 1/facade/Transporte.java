package facade;

public class Transporte {
    public String origen, destino, fecha, medio;
    public double precio;

    public Transporte(String origen, String destino, String fecha, String medio, double precio) {
        this.origen = origen;
        this.destino = destino;
        this.fecha = fecha;
        this.medio = medio;
        this.precio = precio;
    }
}