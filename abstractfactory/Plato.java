package abstractfactory;

public class Plato {
    private String nombre;
    private TipoPlato tipo;
    private double precio;
    private Acompanamiento side;

    public Plato(String nombre, TipoPlato tipo, double precio,  Acompanamiento side){
        this.nombre = nombre;
        this.tipo = tipo;
        this.precio = precio;
        this.side = side;
    }

    public String getName(){
        return nombre;
    }
    
    public TipoPlato getType(){
        return tipo;
    }

    public double getPrecio(){
        return precio;
    }

    public Acompanamiento getSide(){
        return side;
    }

    public void setName(String nombre){
        this.nombre = nombre;
    }

}
