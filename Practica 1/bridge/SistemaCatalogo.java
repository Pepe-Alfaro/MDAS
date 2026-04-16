package bridge;
import java.util.ArrayList;
import java.util.List;

public abstract class SistemaCatalogo {
    protected List<IProveedor> proveedores;

    public SistemaCatalogo() {
        this.proveedores = new ArrayList<>();
    }

    public void agregarProveedor(IProveedor proveedor) {
        this.proveedores.add(proveedor);
    }

    // Método tradicional para unir inventarios
    protected List<Producto> obtenerInventarioAgregado() {
        List<Producto> inventario = new ArrayList<>();

        // Recorremos todos los proveedores
        for (int i = 0; i < proveedores.size(); i++) {
            IProveedor proveedor = proveedores.get(i);
            List<Producto> productosProveedor = proveedor.obtenerProductos();

            // Recorremos los productos de ese proveedor
            for (int j = 0; j < productosProveedor.size(); j++) {
                Producto productoActual = productosProveedor.get(j);

                // Solo nos interesan los que tienen stock
                if (productoActual.getUnidadesDisponibles() > 0) {
                    
                    boolean productoEncontrado = false;

                    // Buscamos si ya hemos metido este producto en nuestra lista final
                    for (int k = 0; k < inventario.size(); k++) {
                        Producto productoExistente = inventario.get(k);
                        
                        // Si tienen el mismo ID, es el mismo producto
                        if (productoExistente.getId().equals(productoActual.getId())) {
                            // Sumamos las unidades
                            int nuevasUnidades = productoExistente.getUnidadesDisponibles() + productoActual.getUnidadesDisponibles();
                            productoExistente.setUnidadesDisponibles(nuevasUnidades);
                            productoEncontrado = true;
                            break; // Salimos del bucle de búsqueda
                        }
                    }

                    // Si hemos terminado de buscar y no estaba, lo añadimos como nuevo
                    if (!productoEncontrado) {
                        inventario.add(productoActual);
                    }
                }
            }
        }
        return inventario;
    }

    // Ordenación por el método de la burbuja (Precio ascendente: menor a mayor)
    public List<Producto> buscarPorPrecioAscendente() {
        List<Producto> lista = obtenerInventarioAgregado();
        
        for (int i = 0; i < lista.size() - 1; i++) {
            for (int j = 0; j < lista.size() - i - 1; j++) {
                if (lista.get(j).getPrecio() > lista.get(j + 1).getPrecio()) {
                    // Intercambiamos los productos
                    Producto temporal = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, temporal);
                }
            }
        }
        return lista;
    }

    // Ordenación por el método de la burbuja (Stock descendente: mayor a menor)
    public List<Producto> buscarPorStockDescendente() {
        List<Producto> lista = obtenerInventarioAgregado();
        
        for (int i = 0; i < lista.size() - 1; i++) {
            for (int j = 0; j < lista.size() - i - 1; j++) {
                if (lista.get(j).getUnidadesDisponibles() < lista.get(j + 1).getUnidadesDisponibles()) {
                    // Intercambiamos los productos
                    Producto temporal = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, temporal);
                }
            }
        }
        return lista;
    }
}