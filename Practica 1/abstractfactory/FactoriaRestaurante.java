package abstractfactory;
import java.util.List;

public class FactoriaRestaurante extends FactoriaAbstracta{



public Semanal crearMenuSemanal(List<Plato>platos,Acompanamiento side){

    for (Plato plato : platos){
        if( plato.getTipo() == TipoPlato.PRINCIPAL ){
            plato.setSide(side);
        }
    }

    Semanal menu = new Semanal(platos);    
    return menu;

}

public Temporada crearMenuTemporada(List<Plato> platos, Acompanamiento side){

    for(Plato plato : platos){
        if(plato.getTipo() == TipoPlato.PRINCIPAL){
            plato.setSide(side); 
        }
    }

    Temporada menu = new Temporada(platos);
    return menu;
}

}