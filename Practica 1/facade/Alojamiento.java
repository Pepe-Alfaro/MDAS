package facade;

public class Alojamiento {
    public String ciudad, hotel, fechaEntrada;
    public double precioNoche;

    public Alojamiento(String ciudad, String hotel, String fechaEntrada, double precioNoche) {
        this.ciudad = ciudad;
        this.hotel = hotel;
        this.fechaEntrada = fechaEntrada;
        this.precioNoche = precioNoche;
    }
}