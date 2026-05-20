package es.uco.pw.grupo12.model.domain.socio;

import java.time.LocalDate;
import es.uco.pw.grupo12.model.domain.Persona; // Importamos la superclase

// [Refactorización: Herencia - Socio hereda de Persona para reutilizar el código común]
public class Socio extends Persona {

    private String direccion;
    private LocalDate fechaInscripcion;
    private boolean tituloPatron;
    private int idInscripcionFk;
    private Boolean esTitular; // Este campo no está en la BBDD. Se rellenará mediante la consulta.

    public Socio(){
        super("-1", "", "", LocalDate.now()); // Llamada al constructor de Persona
        this.direccion="";
        this.fechaInscripcion=LocalDate.now();
        this.tituloPatron=false;
        this.idInscripcionFk=-1;
        this.esTitular = false; // Valor por defecto
    }
    
    public Socio(String dni, String nombre, String apellidos, LocalDate fechaNacimiento,
                 String direccion, LocalDate fechaInscripcion) {
        
        super(dni, nombre, apellidos, fechaNacimiento); // Llamada al constructor de Persona
        this.direccion = direccion;
        this.fechaInscripcion = fechaInscripcion;
        this.tituloPatron = false; // valor por defecto
        this.idInscripcionFk = -1;
        this.esTitular = false; // Valor por defecto
    }

    // Getters específicos de Socio
    public String getDireccion() {
        return direccion;
    }
    
    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }
    
    public boolean getTituloPatron() {
        return tituloPatron;
    }
    
    public int getIdInscripcionFk() { 
        return idInscripcionFk; 
    }
    
    public Boolean getEsTitular() {
        if (this.esTitular == null) {
            return false; // Seguridad por si no se ha seteado
        }
        return esTitular;
    }

    // Setters específicos de Socio
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public void setFechaInscripcion(LocalDate fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }
    
    public void setTituloPatron(boolean tituloPatron) {
        this.tituloPatron = tituloPatron;
    }
    
    public void setIdInscripcionFk(int idInscripcionFk) { 
        this.idInscripcionFk = idInscripcionFk;
    }
    
    public void setEsTitular(Boolean esTitular) {
        this.esTitular = esTitular;
    }

    public boolean esMayorDeEdad() {
        // Al usar herencia, llamamos al getter de la superclase
        return this.getFechaNacimiento().plusYears(18).isBefore(LocalDate.now()) || 
               this.getFechaNacimiento().plusYears(18).isEqual(LocalDate.now());
    }
    
    @Override
    public String toString() {
    // Usamos los getters heredados (getDni, getNombre...) para montar el String
    return "Socio [dni=" + getDni() + ", nombre=" + getNombre() + ", apellidos=" + getApellidos() + 
           ", fechaNacimiento=" + getFechaNacimiento() + ", direccion=" + direccion + 
           ", fechaInscripcion=" + fechaInscripcion + ", tituloPatron=" + tituloPatron + 
           ", idInscripcionFk=" + idInscripcionFk + ", esTitular=" + esTitular + "]";
    }
}