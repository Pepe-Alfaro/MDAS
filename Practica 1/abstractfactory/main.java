package abstractfactory;

import java.util.ArrayList;
import java.util.List;

public class main {
    public static void main(String[] args) {
        
        // 1. Pruebas para Factoria Restaurante (Local)
        System.out.println("==========================================");
        System.out.println("--- PRUEBAS EN RESTAURANTE (LOCAL) ---");
        System.out.println("==========================================");
        FactoriaAbstracta factoryLocal = new FactoriaRestaurante();
        
        // Menú Semanal Local
        System.out.println("\n> Creando Menú Semanal (Acompañamiento para Principal: PATATAS)...");
        Menu menuSemanalLocal = factoryLocal.crearMenuSemanal(generarPlatosBase(), Acompanamiento.PATATAS);
        imprimirMenu(menuSemanalLocal);

        // Menú Temporada Local
        System.out.println("\n> Creando Menú de Temporada (Acompañamiento para Principal: ENSALADA)...");
        Menu menuTemporadaLocal = factoryLocal.crearMenuTemporada(generarPlatosBase(), Acompanamiento.ENSALADA);
        imprimirMenu(menuTemporadaLocal);


        // 2. Pruebas para Factoria TakeAway (Para Llevar)
        System.out.println("\n==========================================");
        System.out.println("--- PRUEBAS TAKE AWAY (PARA LLEVAR - +2% Precio) ---");
        System.out.println("==========================================");
        FactoriaAbstracta factoryLlevar = new FactoriaTakeAway();
        
        // Menú Semanal TakeAway
        System.out.println("\n> Creando Menú Semanal Take Away (Acompañamiento para Principal: ENSALADA)...");
        Menu menuSemanalLlevar = factoryLlevar.crearMenuSemanal(generarPlatosBase(), Acompanamiento.ENSALADA);
        imprimirMenu(menuSemanalLlevar);

        // Menú Temporada TakeAway
        System.out.println("\n> Creando Menú de Temporada Take Away (Acompañamiento para Principal: NINGUNO)...");
        Menu menuTemporadaLlevar = factoryLlevar.crearMenuTemporada(generarPlatosBase(), Acompanamiento.NINGUNO);
        imprimirMenu(menuTemporadaLlevar);
    }

    /**
     * Método auxiliar para generar una lista fresca de platos en cada prueba.
     * Al crear los platos de cero cada vez, evitamos que la subida del 2% del Take Away 
     * o los cambios de acompañamiento de una prueba modifiquen los datos de la siguiente.
     */
    private static List<Plato> generarPlatosBase() {
        List<Plato> lista = new ArrayList<>();
        lista.add(new Plato("Ensalada Mixta", TipoPlato.ENTRANTE, 5.00, Acompanamiento.NINGUNO));
        lista.add(new Plato("Solomillo a la brasa", TipoPlato.PRINCIPAL, 15.00, Acompanamiento.NINGUNO));
        lista.add(new Plato("Tarta de Queso", TipoPlato.POSTRE, 4.00, Acompanamiento.NINGUNO));
        return lista;
    }

    /**
     * Imprime los detalles del menú recorriendo la lista de platos.
     */
    public static void imprimirMenu(Menu menu) {
        System.out.println(menu.toString()); // Usamos el toString que definiste en Semanal/Temporada
        System.out.println("Detalle de platos:");
        for (Plato p : menu.getPlatos()) {
            // Imprimimos el nombre, tipo, acompañamiento y precio con 2 decimales
            System.out.printf("  - %-20s (%-9s) | Guarnición: %-8s | Precio: %.2f€%n", 
                              p.getNombre(), p.getTipo(), p.getSide(), p.getPrecio());
        }
        System.out.println("------------------------------------------------------------------");
    }
}