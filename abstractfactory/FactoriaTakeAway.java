package abstractfactory;

public class FactoriaTakeAway {

    private Menu menu;
    private Plato plato;
    
    public FactoriaTakeAway(Menu menu, Plato plato){
        this.menu = menu;
        this.plato = plato;
    }

    public Semanal crearMenuSemanal(Acompanamiento Side){
        //plato
        plato.setSide(Side);
        double precioTW = plato.getPrecio()*0.02;
        plato.setPrecio(precioTW);
        //menu
        menu.addPlato(plato);
        

       
        
    }
}