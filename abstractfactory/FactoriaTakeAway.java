package abstractfactory;

public class FactoriaTakeAway {

  
    private Plato plato;
    
    public FactoriaTakeAway(Plato plato){
       
        this.plato = plato;
    }

    public Semanal crearMenuSemanal(Acompanamiento Side){
        
        plato.setSide(Side);
        double precioTW = plato.getPrecio()*0.02;
        plato.setPrecio(precioTW);
      
        Semanal PlatoSemanal = new Semanal(plato);
        return PlatoSemanal;
       
        
    }

    public Temporada crearMenuTemporada(Acompanamiento Side){
        plato.setSide(Side);
        double precioTW = plato.getPrecio()*0.02;
        plato.setPrecio(precioTW);
      
        Temporada PlatoSemanal = new Temporada(plato);
        return PlatoSemanal;
    }


}