package bridge;
import java.util.List;


public interface IProveedor {

    public List<Producto> getProductos();

    public List<Producto> buscarPorPrecio(double min, double max);

    public List<Producto> buscarPorColor(String color);
} 

