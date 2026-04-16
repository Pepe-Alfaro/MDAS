package es.uco.pw.grupo12.model.domain.alquiler;

import es.uco.pw.grupo12.model.domain.socio.Socio;
import es.uco.pw.grupo12.model.domain.embarcacion.Embarcacion;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/*  Representa el alquiler de una embarcación por parte de un socio con título de patrón.
Se calcula el precio total en base a las personas y los días de duración.*/
 
public class Alquiler {

    private static final double PRECIO_POR_PERSONA_DIA = 20.0;

    private int idAlquiler;
    private Socio socioTitular;
    private List<Socio> sociosPasajeros; 
    private Embarcacion embarcacion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private double precioTotal;

    public Alquiler(int idAlquiler, Socio socioTitular, List<Socio> sociosPasajeros,
                    Embarcacion embarcacion, LocalDate fechaInicio, LocalDate fechaFin) {

        this.idAlquiler = idAlquiler;
        this.socioTitular = socioTitular;
        this.sociosPasajeros = sociosPasajeros;
        this.embarcacion = embarcacion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.precioTotal = calcularPrecio();
    }

    public Alquiler() {
        this.idAlquiler = -1;
        this.socioTitular = new Socio();
        this.sociosPasajeros = null;
        this.embarcacion = new Embarcacion();
        this.fechaInicio = LocalDate.now();
        this.fechaFin = LocalDate.now();
        this.precioTotal = 0.0;
    }
    private double calcularPrecio() {
        long dias = ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1; 
        int numPersonas = sociosPasajeros != null ? sociosPasajeros.size() : 1;
        return PRECIO_POR_PERSONA_DIA * numPersonas * dias;
    }

    // Getters

    public int getIdAlquiler(){ 
        return idAlquiler; 
    }
    public Socio getSocioTitular(){ 
        return socioTitular; 
    }
    public List<Socio> getSociosPasajeros(){
        return sociosPasajeros; 
    }
    public Embarcacion getEmbarcacion() {
        return embarcacion;
    }
    public LocalDate getFechaInicio(){
        return fechaInicio;
    }
    public LocalDate getFechaFin(){
        return fechaFin;
    }
    public double getPrecioTotal(){ 
        return precioTotal; 
    }

    // Setters
    public void setIdAlquiler(int idAlquiler){ 
        this.idAlquiler = idAlquiler; 
    }
    public void setSocioTitular(Socio socioTitular){ 
        this.socioTitular = socioTitular; 
    }
    public void setSociosPasajeros(List<Socio> sociosPasajeros){
        this.sociosPasajeros = sociosPasajeros; 
        this.precioTotal = calcularPrecio();
    }
    public void setEmbarcacion(Embarcacion embarcacion){
        this.embarcacion = embarcacion; 
    }
    public void setFechaInicio(LocalDate fechaInicio){
        this.fechaInicio = fechaInicio; 
        this.precioTotal = calcularPrecio();
    }
    public void setFechaFin(LocalDate fechaFin){
        this.fechaFin = fechaFin;
        this.precioTotal = calcularPrecio();
    }
    public void setPrecioTotal(double precioTotal){
        this.precioTotal = precioTotal;
    }

    @Override
    public String toString() {
        return "Alquiler [idAlquiler=" + idAlquiler + ", socioTitular=" + socioTitular + ", sociosPasajeros="
                + sociosPasajeros + ", embarcacion=" + embarcacion + ", fechaInicio=" + fechaInicio + ", fechaFin="
                + fechaFin + ", precioTotal=" + precioTotal + ", calcularPrecio()=" + calcularPrecio()
                + ", getIdAlquiler()=" + getIdAlquiler() + ", getSocioTitular()=" + getSocioTitular()
                + ", getSociosPasajeros()=" + getSociosPasajeros() + ", getEmbarcacion()=" + getEmbarcacion()
                + ", getFechaInicio()=" + getFechaInicio() + ", getFechaFin()=" + getFechaFin() + ", getPrecioTotal()="
                + getPrecioTotal() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
                + super.toString() + "]";
    }
}
