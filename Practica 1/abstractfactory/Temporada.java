package abstractfactory;
import java.util.List;
public class Temporada extends Menu {

    public Temporada(List<Plato> plato) {
        super(plato);
    }

    @Override
    public String toString() {
        return "Menú de Temporada compuesto por: " + getPlatos().size() + " platos. " +
               "Precio total: " + calcularPrecioTotal() + "€";
    }
}