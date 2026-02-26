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

    public String getNombre(){
        return nombre;
    }
    
    public TipoPlato getTipo(){
        return tipo;
    }

    public double getPrecio(){
        return precio;
    }

    public Acompanamiento getSide(){
        return side;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public void setTipo(TipoPlato tipo){
        this.tipo = tipo;
    }
    
    public void setPrecio(float precio){
        this.precio = precio;
    }

    public void setSide(Acompanamiento side){
        this.side = side;
    }

}
