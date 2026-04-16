package composite;

import java.util.ArrayList;
import java.util.List;

public class EspacioComposite extends ComponenteEnergia {
    private List<ComponenteEnergia> componentes;

    public EspacioComposite(String nombre) {
        super(nombre);
        this.componentes = new ArrayList<>();
    }

    public void agregarComponente(ComponenteEnergia componente) {
        this.componentes.add(componente);
    }
    public void eliminarComponente(ComponenteEnergia componente) {
        this.componentes.remove(componente);
    }

    @Override
    public double calcularConsumo() {
        double gastoTotal = 0;
        for (ComponenteEnergia c : componentes) {
            gastoTotal += c.calcularConsumo();
        }
        return gastoTotal;
    }
    
}
