package bridge;
import java.util.ArrayList;
import java.util.List;

public class EmpresaA implements IProveedor {
    private List<Producto> catalogo;

    public EmpresaA() {
        catalogo = new ArrayList<>();
        catalogo.add(new Sofa("S-A1", "Sofá Chaise Longue", 850.0, 5, "Tela", "Gris", 4));
        catalogo.add(new Sofa("S-A2", "Sofá Cama Moderno", 450.0, 2, "Cuero", "Negro", 3));
    }

    @Override
    public List<Producto> obtenerProductos() {
        return catalogo;
    }
}