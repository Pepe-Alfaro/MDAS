package es.uco.pw.grupo12.model.domain.embarcacion;
import es.uco.pw.grupo12.model.domain.patron.Patron;
import java.math.BigDecimal;

public class Embarcacion {

    private String matricula;
    private TipoEmbarcacion tipo;
    private String nombre;
    private int plazas;
    private BigDecimal dimensiones;
    private Patron patron;

    public Embarcacion( String matricula, TipoEmbarcacion tipo, String nombre, int plazas, BigDecimal dimensiones, Patron patron) {
        this.matricula=matricula;
        this.tipo=tipo;
        this.nombre=nombre;
        this.plazas=plazas;
        this.dimensiones=dimensiones;
        this.patron=patron;
    }

    public Embarcacion() {
        this.matricula="";
        this.tipo=TipoEmbarcacion.NONE;
        this.nombre="";
        this.plazas=0;
        this.dimensiones=BigDecimal.ZERO;
        this.patron=new Patron();
    }

    
    //Getters

    public String getMatricula() {
        return matricula;
    }

    public TipoEmbarcacion getTipo(){
        return tipo;
    }

    public String getNombre(){
        return nombre;
    }

    public int getPlazas(){
        return plazas;
    }

    public BigDecimal getDimensiones(){
        return dimensiones;

    }

  
    public Patron getPatron(){
        return patron;
    }

    //Setters
    public void setMatricula(String matricula) {
        this.matricula=matricula;
    }


    public void setTipo(TipoEmbarcacion tipo) {
        this.tipo = tipo;
    }

    public void setNombre(String nombre){
        this.nombre=nombre;
    }

    public void setPlazas(int plazas){
        this.plazas=plazas;
    }

    public void setDimensiones(BigDecimal dimensiones){
        this.dimensiones=dimensiones;
    }

    public void setPatron(Patron Patron){
        this.patron=Patron;
    }
}