package abstractfactory;

public class Temporada extends Menu {

    public Temporada(Plato plato) {
        super(plato);
    }

    @Override
    public String toString() {
        return "Menú de Temporada compuesto por: " + getPlatos().size() + " platos. " +
               "Precio total: " + calcularPrecioTotal() + "€";
    }
}