package es.uco.pw.grupo12.model.domain.patron;

import java.time.LocalDate;
import es.uco.pw.grupo12.model.domain.Persona; // Importamos la superclase

// [Refactorización: Herencia - Patron hereda de Persona para reutilizar el código común]
public class Patron extends Persona {
    
    private LocalDate fechaExpedicionTitulo;

    public Patron(){
       super("-1", "", "", LocalDate.now()); // Llamada al constructor de Persona
       this.fechaExpedicionTitulo=LocalDate.now();  
    }

    public Patron(String dni,String nombre,String apellidos,LocalDate fechaNacimiento,LocalDate fechaExpedicionTitulo){
        super(dni, nombre, apellidos, fechaNacimiento); // Llamada al constructor de Persona
        this.fechaExpedicionTitulo=fechaExpedicionTitulo;
    }

    // Getters específicos de Patron
    public LocalDate getFechaExpedicionTitulo(){
        return fechaExpedicionTitulo;
    } 

    // Setters específicos de Patron
    public void setFechaExpedicionTitulo(LocalDate fechaExpedicion){
        this.fechaExpedicionTitulo=fechaExpedicion;
    }

    @Override
    public String toString() {
        // Usamos los getters heredados para montar el String
        return "Patron [dni=" + getDni() + ", nombre=" + getNombre() + ", apellidos=" + getApellidos() + 
           ", fechaNacimiento=" + getFechaNacimiento() + 
           ", fechaExpedicionTitulo=" + fechaExpedicionTitulo + "]";
    }
}