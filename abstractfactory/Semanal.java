package abstractfactory;
import java.util.List;
public class Semanal extends Menu {

    // Constructor que recibe el primer plato
    public Semanal(List<Plato> platos) {
        super(platos);
    }

    @Override
    public String toString() {
        return "Menú Semanal compuesto por: " + getPlatos().size() + " platos. " +
               "Precio total: " + calcularPrecioTotal() + "€";
    }
}
