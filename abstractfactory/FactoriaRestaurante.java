package abstractfactory;

public class FactoriaRestaurante extends FactoriaAbstracta {

    public Menu crearMenuSemanal(Acompanamiento side) {
        // En el restaurante (local), el menú se compone de entrante, principal y postre 
        
        // Creamos los platos con precios normales
        Plato e = new Plato("Ensalada Mixta", TipoPlato.ENTRANTE, 6.0, Acompanamiento.NINGUNO);
        Plato p = new Plato("Pollo Asado", TipoPlato.PRINCIPAL, 12.0, side); // Se aplica el acompañamiento elegido 
        Plato d = new Plato("Natillas", TipoPlato.POSTRE, 4.0, Acompanamiento.NINGUNO);
        
        // Creamos el menú Semanal. 
        // Nota: Como tu clase Menu pide un Plato en el constructor, se lo pasamos aquí.
        Semanal menu = new Semanal(e);
        menu.addPlato(p);
        menu.addPlato(d); // En local SÍ se incluye postre 
        
        return menu;
    }


    public Menu crearMenuTemporada(Acompanamiento side) {
        // Estructura para el menú de temporada en el local
        Plato e = new Plato("Sopa de Marisco", TipoPlato.ENTRANTE, 8.5, Acompanamiento.NINGUNO);
        Plato p = new Plato("Cordero Lechal", TipoPlato.PRINCIPAL, 18.0, side);
        Plato d = new Plato("Tarta de la Abuela", TipoPlato.POSTRE, 5.0, Acompanamiento.NINGUNO);
        
        Temporada menu = new Temporada(e);
        menu.addPlato(p);
        menu.addPlato(d);
        
        return menu;
    }
}