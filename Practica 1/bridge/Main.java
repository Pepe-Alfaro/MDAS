package bridge;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        
        // 1. Instanciamos las implementaciones (los proveedores reales)
        IProveedor empresaA = new EmpresaA(); 
        IProveedor empresaB = new EmpresaB(); 
        IProveedor empresaC = new EmpresaC(); 

        System.out.println("--- CONFIGURACIÓN DEL SISTEMA BRIDGE ---");

        // 2. Instanciamos las abstracciones y las configuramos (el puente)
        
        CatalogoMesas catalogoMesas = new CatalogoMesas();
        catalogoMesas.agregarProveedor(empresaA);
        catalogoMesas.agregarProveedor(empresaB);

        CatalogoSofas catalogoSofas = new CatalogoSofas();
        catalogoSofas.agregarProveedor(empresaB);
        catalogoSofas.agregarProveedor(empresaC);

        CatalogoGeneral catalogoGeneral = new CatalogoGeneral();
        catalogoGeneral.agregarProveedor(empresaA);
        catalogoGeneral.agregarProveedor(empresaB);
        catalogoGeneral.agregarProveedor(empresaC);

        // 3. Casos de Uso y Pruebas

        System.out.println("\n--- BÚSQUEDA DE MESAS (Stock por unidades descendente) ---");
        List<Producto> mesasEnStock = catalogoMesas.buscarPorStockDescendente();
        imprimirLista(mesasEnStock);

        System.out.println("\n--- BÚSQUEDA ESPECÍFICA DE SOFÁS (Filtro por 5 plazas) ---");
        List<Sofa> sofasDe5Plazas = catalogoSofas.buscarPorPlazas(5);
        for (int i = 0; i < sofasDe5Plazas.size(); i++) {
            System.out.println(sofasDe5Plazas.get(i).toString());
        }

        System.out.println("\n--- CATÁLOGO GENERAL (Ordenado por precio de menor a mayor) ---");
        List<Producto> todoElCatalogo = catalogoGeneral.buscarPorPrecioAscendente();
        imprimirLista(todoElCatalogo);
    }

    // Método auxiliar para imprimir las listas (usando el for clásico que vimos)
    private static void imprimirLista(List<Producto> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay productos disponibles.");
        } else {
            for (int i = 0; i < lista.size(); i++) {
                System.out.println("- " + lista.get(i).toString());
            }
        }
    }
}