package abstractfactory;

import java.util.ArrayList;
import java.util.List;

public class FactoriaTakeAway extends FactoriaAbstracta {

    @Override
    public Semanal crearMenuSemanal(List<Plato> platos, Acompanamiento side) {
        
        List<Plato> platosFiltrados = new ArrayList<>();

        for (Plato plato : platos) {
            if (plato.getTipo() != TipoPlato.POSTRE) {
                
                if (plato.getTipo() == TipoPlato.PRINCIPAL) {
                    plato.setSide(side);
                }
                
                double precio = plato.getPrecio() * 1.02;
                plato.setPrecio(precio);
                
                platosFiltrados.add(plato);
            }
        }

        Semanal menu = new Semanal(platosFiltrados);
        return menu;
    }

    @Override
    public Temporada crearMenuTemporada(List<Plato> platos, Acompanamiento side) {
        List<Plato> platosFiltrados = new ArrayList<>();

        for (Plato plato : platos) {
            if (plato.getTipo() != TipoPlato.POSTRE) {
                
                if (plato.getTipo() == TipoPlato.PRINCIPAL) {
                    plato.setSide(side);
                }
                
                double precio = plato.getPrecio() * 1.02;
                plato.setPrecio(precio);
                
                platosFiltrados.add(plato);
            }
        }

        Temporada menu = new Temporada(platosFiltrados);
        return menu;
    }
}