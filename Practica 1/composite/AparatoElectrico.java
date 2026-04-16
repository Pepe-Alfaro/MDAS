package composite;

public class AparatoElectrico extends ComponenteEnergia
{ 
    private double consumoPorHora;
    private int horasUso;

    public AparatoElectrico(String nombre, double consumoPorHora, int horasUso) {
        super(nombre);
        this.consumoPorHora = consumoPorHora;
        this.horasUso = horasUso;
    }
    
    @Override
    public double calcularConsumo() {
        return this.consumoPorHora * this.horasUso;
    }
}
