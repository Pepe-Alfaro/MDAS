package facade;
import java.util.*;

public class OficinaTurismoEvento implements IOficinaTurismo {
    @Override
    public List<String> buscarActividades(String ciudad, String tipoEvento) {
    
        return Arrays.asList("Concierto de Rock en " + ciudad, "Festival Gastronómico");
    }
}