package facade;
import java.util.*;

public class ServicioAlojamiento {
    private List<Alojamiento> catalogo = new ArrayList<>();

    public ServicioAlojamiento() {
        catalogo.add(new Alojamiento("Madrid", "Hotel Sol", "10/05", 85.0));
        catalogo.add(new Alojamiento("Madrid", "Hostal Central", "10/05", 40.0));
        catalogo.add(new Alojamiento("Barcelona", "Hotel Mar", "12/05", 120.0));
    }

    public List<Alojamiento> buscar(String ciudad, String fecha) {
        List<Alojamiento> resultados = new ArrayList<>();
        for (Alojamiento a : catalogo) {
            if (a.ciudad.equals(ciudad) && a.fechaEntrada.equals(fecha)) {
                resultados.add(a);
            }
        }
        return resultados;
    }
}