package facade;
import java.util.*;

public class OficinaTurismoFecha implements IOficinaTurismo {
    @Override
    public List<String> buscarActividades(String ciudad, String fecha) {
     
        return Arrays.asList("Visita Museo del Prado (Fecha: " + fecha + ")", "Tour Nocturno");
    }
}