package abstractfactory;
import java.util.ArrayList;
import java.util.List;  

public abstract class Menu {

    private List<Plato> platos;
    
    public Menu(List<Plato> plato) {
        this.platos = plato;
        
    }

    public void addPlato(Plato plato) {
        this.platos.add(plato);
        
    }

    public double calcularPrecioTotal(){
        double total = 0;
        for(Plato plato : this.platos) {
            total += plato.getPrecio();
        }
        return total;
    }

    public List<Plato> getPlatos() {
        return platos;
    }

    
}