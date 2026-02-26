package abstractfactory;

public class main {
    public static void main(String[] args) {
        // 1. Probamos el pedido en el Restaurante (Local)
        System.out.println("--- PEDIDO EN LOCAL ---");
        FactoriaAbstracta factoryLocal = new FactoriaRestaurante();
        Menu menuLocal = factoryLocal.crearMenuSemanal(Acompanamiento.PATATAS);
        
        imprimirMenu(menuLocal);
        //Ejemplo para probar TakeAway
        Plato principal = new Plato("Ensalada César", TipoPlato.ENTRANTE, 8.50, Acompanamiento.NINGUNO);
        Plato entrante = new Plato("Entrecot de ternera", TipoPlato.ENTRANTE, 15.00, Acompanamiento.PATATAS);


        System.out.println("\n--- PEDIDO PARA LLEVAR ---");
        // 2. Probamos el pedido Para Llevar (TakeAway)
        FactoriaAbstracta factoryLlevar = new FactoriaTakeAway(principal,entrante);
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