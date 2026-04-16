package bridge;
import java.util.ArrayList;
import java.util.List;

public class CatalogoSofas extends SistemaCatalogo {

    public List<Sofa> buscarPorPlazas(int plazasRequeridas) {
        List<Producto> todosLosProductos = obtenerInventarioAgregado();
        List<Sofa> sofasFiltrados = new ArrayList<>();

        for (int i = 0; i < todosLosProductos.size(); i++) {
            Producto p = todosLosProductos.get(i);
            
            // Comprobamos si el producto es de tipo Sofa
            if (p instanceof Sofa) {
                Sofa sofa = (Sofa) p; // Lo convertimos a Sofa para leer sus plazas
                
                if (sofa.getNumeroPlazas() == plazasRequeridas) {
                    sofasFiltrados.add(sofa);
                }
            }
        }
        return sofasFiltrados;
    }
}