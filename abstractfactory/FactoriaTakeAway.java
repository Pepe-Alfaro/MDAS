package abstractfactory;
import java.util.List;

public class FactoriaTakeAway extends FactoriaAbstracta{

public Semanal crearMenuSemanal(List<Plato>platos,Acompanamiento side){

    for (Plato plato : platos){
        if( plato.getTipo() == TipoPlato.PRINCIPAL ){
            plato.setSide(side);
        };
        double precio = plato.getPrecio()*1.02;
        plato.setPrecio(precio);

    }

    Semanal menu = new Semanal(platos);   
    return menu;


}

public Temporada crearMenuTemporada(List<Plato> platos, Acompanamiento side){

    for(Plato plato : platos){
        if(plato.getTipo() == TipoPlato.PRINCIPAL){
            plato.setSide(side); 
        }
        double precio = plato.getPrecio()*1.02;
        plato.setPrecio(precio);
    }
    
    Temporada menu = new Temporada(platos);
    return menu;
}

}