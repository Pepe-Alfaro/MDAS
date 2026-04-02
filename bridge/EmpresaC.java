package bridge;
import java.util.ArrayList;
import java.util.List;

public class EmpresaC implements IProveedor {
    private List<Producto> catalogo;

    public EmpresaC() {
        catalogo = new ArrayList<>();
        catalogo.add(new Sofa("S-C1", "Sofá Modular", 600.0, 8, "Terciopelo", "Azul", 5));
        // Mismo ID que en EmpresaA para comprobar la suma de unidades
        catalogo.add(new Sofa("S-A2", "Sofá Cama Moderno", 450.0, 4, "Cuero", "Negro", 3)); 
        catalogo.add(new Mesa("M-C1", "Mesa de Comedor", 300.0, 3, "Roble", "Marrón", "200x100"));
    }

    @Override
    public List<Producto> obtenerProductos() {
        return catalogo;
    }
}