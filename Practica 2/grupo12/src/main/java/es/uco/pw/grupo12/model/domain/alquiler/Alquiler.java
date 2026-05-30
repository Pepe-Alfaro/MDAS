package es.uco.pw.grupo12.model.domain.alquiler;

import es.uco.pw.grupo12.model.domain.socio.Socio;
import es.uco.pw.grupo12.model.domain.embarcacion.Embarcacion;

import java.time.LocalDate;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

/* Representa el alquiler de una embarcación por parte de un socio con título de patrón.
Se calcula el precio total en base a las personas y los días de duración.*/
 
public class Alquiler {

    private static final double PRECIO_POR_PERSONA_DIA = 20.0;

    private int idAlquiler;
    private Socio socioTitular;
    private List<Socio> sociosPasajeros; 
    private Embarcacion embarcacion;
    
    // [Refactorización: Encapsulación (Extraer Clase) - Sustituye a fechaInicio y fechaFin]
    private RangoFechas rangoFechas;
    
    private double precioTotal;

    public Alquiler(int idAlquiler, Socio socioTitular, List<Socio> sociosPasajeros,
                    Embarcacion embarcacion, LocalDate fechaInicio, LocalDate fechaFin) {

        this.idAlquiler = idAlquiler;
        this.socioTitular = socioTitular;
        this.sociosPasajeros = sociosPasajeros;
        this.embarcacion = embarcacion;
        
        // Se inicializa el objeto extraído
        this.rangoFechas = new RangoFechas(fechaInicio, fechaFin);
        this.precioTotal = calcularPrecio();
    }

    public Alquiler() {
        this.idAlquiler = -1;
        this.socioTitular = new Socio();
        this.sociosPasajeros = null;
        this.embarcacion = new Embarcacion();
        
        // Se inicializa el objeto extraído con las fechas de hoy
        this.rangoFechas = new RangoFechas(LocalDate.now(), LocalDate.now());
        this.precioTotal = 0.0;
    }

    // [Refactorización: Reemplazar variable temporal por consulta]
    private double calcularPrecio() {
        // La fórmula ahora es limpia y directa, sin variables temporales
        return PRECIO_POR_PERSONA_DIA * getNumeroPersonas() * getDiasAlquiler();
    }

    // Getters

    public int getIdAlquiler(){ 
        return idAlquiler; 
    }
    
    public Socio getSocioTitular(){ 
        return socioTitular; 
    }
    
    // [Refactorización: Encapsular colecciones - Evitar modificación externa no controlada]
    public List<Socio> getSociosPasajeros() {
        return sociosPasajeros != null ? Collections.unmodifiableList(sociosPasajeros) : null;
    }
    
    public Embarcacion getEmbarcacion() {
        return embarcacion;
    }
    
    // [Refactorización: Extraer clase - Delegación]
    public LocalDate getFechaInicio(){
        return rangoFechas.getFechaInicio();
    }
    
    // [Refactorización: Extraer clase - Delegación]
    public LocalDate getFechaFin(){
        return rangoFechas.getFechaFin();
    }
    
    public double getPrecioTotal(){ 
        return precioTotal; 
    }

    // Setters y Métodos de gestión de colecciones
    
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

    // [Refactorización: Encapsular colecciones - Métodos seguros para añadir/quitar]
    public void addSocioPasajero(Socio pasajero) {
        if (this.sociosPasajeros == null) {
            this.sociosPasajeros = new ArrayList<>();
        }
        this.sociosPasajeros.add(pasajero);
        this.precioTotal = calcularPrecio(); // Se asegura la consistencia del precio
    }

    public void removeSocioPasajero(Socio pasajero) {
        if (this.sociosPasajeros != null) {
            this.sociosPasajeros.remove(pasajero);
            this.precioTotal = calcularPrecio(); // Se asegura la consistencia del precio
        }
    }
    
    public void setEmbarcacion(Embarcacion embarcacion){
        this.embarcacion = embarcacion; 
    }
    
    // [Refactorización: Extraer clase - Delegación de setter y recálculo]
    public void setFechaInicio(LocalDate fechaInicio){
        this.rangoFechas.setFechaInicio(fechaInicio); 
        this.precioTotal = calcularPrecio();
    }
    
    // [Refactorización: Extraer clase - Delegación de setter y recálculo]
    public void setFechaFin(LocalDate fechaFin){
        this.rangoFechas.setFechaFin(fechaFin);
        this.precioTotal = calcularPrecio();
    }
    
    public void setPrecioTotal(double precioTotal){
        this.precioTotal = precioTotal;
    }

    // Métodos extraídos que actúan como consultas (queries) 
    private long getDiasAlquiler() {
        // Ahora se delega el cálculo de días a la clase RangoFechas
        return this.rangoFechas.getDias();
    }

    private int getNumeroPersonas() {
        return sociosPasajeros != null ? sociosPasajeros.size() : 1;
    }

    @Override
    public String toString() {
        return "Alquiler [idAlquiler=" + idAlquiler + ", socioTitular=" + socioTitular + ", sociosPasajeros="
                + sociosPasajeros + ", embarcacion=" + embarcacion + ", fechaInicio=" + getFechaInicio() + ", fechaFin="
                + getFechaFin() + ", precioTotal=" + precioTotal + ", calcularPrecio()=" + calcularPrecio()
                + ", getIdAlquiler()=" + getIdAlquiler() + ", getSocioTitular()=" + getSocioTitular()
                + ", getSociosPasajeros()=" + getSociosPasajeros() + ", getEmbarcacion()=" + getEmbarcacion()
                + ", getFechaInicio()=" + getFechaInicio() + ", getFechaFin()=" + getFechaFin() + ", getPrecioTotal()="
                + getPrecioTotal() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
                + super.toString() + "]";
    }
}