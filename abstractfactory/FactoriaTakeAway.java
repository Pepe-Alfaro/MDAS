package abstractfactory;

public class FactoriaTakeAway extends FactoriaAbstracta {
    private Plato entrante;
    private Plato principal;

    public FactoriaTakeAway(Plato e, Plato p) {
        this.entrante = e;
        this.principal = p;
    }

    @Override
    public Semanal crearMenuSemanal(Acompanamiento side) {
        
        principal.setSide(side);

        
        entrante.setPrecio(entrante.getPrecio() * 1.02);
        principal.setPrecio(principal.getPrecio() * 1.02);

    
        Semanal menu = new Semanal(entrante);
        menu.addPlato(principal);

        return menu;
    }

    @Override
    public Temporada crearMenuTemporada(Acompanamiento side){
          principal.setSide(side);

        
        entrante.setPrecio(entrante.getPrecio() * 1.02);
        principal.setPrecio(principal.getPrecio() * 1.02);

    
        Temporada menu = new Temporada(entrante);
        menu.addPlato(principal);

        return menu;
    }
}