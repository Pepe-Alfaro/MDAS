package facade;
import java.util.*;

public class ServicioTransporte {
    private List<Transporte> catalogo = new ArrayList<>();

    public ServicioTransporte() {
        catalogo.add(new Transporte("Cordoba", "Madrid", "10/05", "AVE", 60.0));
        catalogo.add(new Transporte("Cordoba", "Madrid", "10/05", "Bus", 20.0));
        catalogo.add(new Transporte("Sevilla", "Barcelona", "12/05", "Avion", 90.0));
    }

    public List<Transporte> buscar(String origen, String destino, String fecha) {
        List<Transporte> resultados = new ArrayList<>();
        for (Transporte t : catalogo) {
            if (t.origen.equals(origen) && t.destino.equals(destino) && t.fecha.equals(fecha)) {
                resultados.add(t);
            }
        }
        return resultados;
    }
}