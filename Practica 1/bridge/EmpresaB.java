package bridge;
import java.util.ArrayList;
import java.util.List;

public class EmpresaB implements IProveedor {
    private List<Producto> catalogo;

    public EmpresaB() {
        catalogo = new ArrayList<>();
        catalogo.add(new Mesa("M-B1", "Mesa de Oficina", 150.0, 10, "Madera", "Blanco", "120x60"));
        catalogo.add(new Mesa("M-B2", "Mesa de Centro", 80.0, 0, "Cristal", "Transparente", "90x90")); 
    }

    @Override
    public List<Producto> obtenerProductos() {
        return catalogo;
    }
}