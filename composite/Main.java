package composite;

public class Main {
    public static void main(String[] args) {
        // 1. Creamos aparatos eléctricos individuales (Hojas)
        // Aparatos para la Sala 1
        AparatoElectrico pc1 = new AparatoElectrico("Ordenador Sobremesa 1", 0.5, 8);
        AparatoElectrico pc2 = new AparatoElectrico("Ordenador Sobremesa 2", 0.5, 8);
        AparatoElectrico proyector = new AparatoElectrico("Proyector Sala 1", 0.3, 4);

        // Aparato suelto para el pasillo del Edificio
        AparatoElectrico vending = new AparatoElectrico("Máquina Vending Pasillo", 1.2, 24);

        // 2. Creamos los contenedores (Composites)
        EspacioComposite sala1 = new EspacioComposite("Sala de Informática 1");
        EspacioComposite edificioA = new EspacioComposite("Edificio A (Politécnica)");
        EspacioComposite campus = new EspacioComposite("Campus Rabanales");

        // 3. Montamos la estructura jerárquica (Parte-Todo)
        
        // Añadimos aparatos a la Sala 1
        sala1.agregarComponente(pc1);
        sala1.agregarComponente(pc2);
        sala1.agregarComponente(proyector);

        // Añadimos la Sala y la máquina de vending al Edificio A
        edificioA.agregarComponente (sala1);
        edificioA.agregarComponente(vending);

        // Añadimos el Edificio al Campus
        campus.agregarComponente(edificioA);

        // 4. Mostramos los resultados en diferentes niveles
        System.out.println("=== CONTROL DE GASTO ENERGÉTICO UNIVERSITARIO ===");
        
        // Gasto de una hoja individual
        System.out.println("Gasto " + pc1.getNombre() + ": " + pc1.calcularConsumo() + "€");

        // Gasto de una sala (Suma de sus aparatos)
        System.out.println("Gasto total " + sala1.getNombre() + ": " + sala1.calcularConsumo() + "€");

        // Gasto de un edificio (Suma de sus salas + aparatos propios)
        System.out.println("Gasto total " + edificioA.getNombre() + ": " + edificioA.calcularConsumo () + "€");

        // Gasto del campus entero (Suma de todos sus edificios)
        System.out.println("-------------------------------------------------");
        System.out.println("GASTO GLOBAL DEL " + campus.getNombre() + ": " + campus.calcularConsumo() + "€");
        System.out.println("=================================================");
    }
}