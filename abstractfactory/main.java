package abstractfactory;

public class main {
    public static void main(String[] args) {
        // 1. Probamos el pedido en el Restaurante (Local)
        System.out.println("--- PEDIDO EN LOCAL ---");
        FactoriaAbstracta factoryLocal = new FactoriaRestaurante();
        Menu menuLocal = factoryLocal.crearMenuSemanal(Acompanamiento.PATATAS);
        
        imprimirMenu(menuLocal);

        System.out.println("\n--- PEDIDO PARA LLEVAR ---");
        // 2. Probamos el pedido Para Llevar (TakeAway)
        FactoriaAbstracta factoryLlevar = new FactoriaTakeAway();
        Menu menuLlevar = factoryLlevar.crearMenuSemanal(Acompanamiento.ENSALADA);
        
        imprimirMenu(menuLlevar);
    }

    public static void imprimirMenu(Menu menu) {
        System.out.println("Platos incluidos:");
        for (Plato p : menu.getPlatos()) {
            System.out.println("- " + p.getNombre() + " (" + p.getTipo() + ") | Acompañamiento: " + p.getSide());
        }
        System.out.println("PRECIO TOTAL: " + menu.calcularPrecioTotal() + "€");
    }
}