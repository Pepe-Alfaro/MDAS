package bridge;
import java.util.ArrayList;
import java.util.List;

public class CatalogoMesas extends SistemaCatalogo {

    public List<Mesa> buscarPorDimensiones(String dimensiones) {
        List<Producto> todosLosProductos = obtenerInventarioAgregado();
        List<Mesa> mesasFiltradas = new ArrayList<>();

        for (int i = 0; i < todosLosProductos.size(); i++) {
            Producto p = todosLosProductos.get(i);
            
            // Comprobamos si el producto es de tipo Mesa
            if (p instanceof Mesa) {
                Mesa mesa = (Mesa) p; // Lo convertimos a Mesa para leer sus dimensiones
                
                if (mesa.getDimensiones().equals(dimensiones)) {
                    mesasFiltradas.add(mesa);
                }
            }
        }
        return mesasFiltradas;
    }
}