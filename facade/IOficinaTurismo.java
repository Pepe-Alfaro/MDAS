package facade;
import java.util.List;

public interface IOficinaTurismo {
    List<String> buscarActividades(String ciudad, String parametro);
}