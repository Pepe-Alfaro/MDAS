package abstractfactory;

public class Semanal extends Menu {

    // Constructor que recibe el primer plato
    public Semanal(Plato plato) {
        super(plato);
    }

    @Override
    public String toString() {
        return "Menú Semanal compuesto por: " + getPlatos().size() + " platos. " +
               "Precio total: " + calcularPrecioTotal() + "€";
    }
}
