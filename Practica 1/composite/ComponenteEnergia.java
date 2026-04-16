package composite;

public abstract class ComponenteEnergia {
    protected String nombre;

    public ComponenteEnergia(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre(){
        return nombre;
    }

    public abstract double calcularConsumo();
    
}
