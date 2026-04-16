package es.uco.pw.grupo12.model.domain.patron;

import java.time.LocalDate;

public class Patron {
    private String dni;
    private String nombre;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private LocalDate fechaExpedicionTitulo;

    public Patron(){
       this.dni="-1";
       this.nombre="";
       this.apellidos="";
       this.fechaNacimiento=LocalDate.now();       
       this.fechaExpedicionTitulo=LocalDate.now();  
    }

    public Patron(String dni,String nombre,String apellidos,LocalDate fechaNacimiento,LocalDate fechaExpedicionTitulo){
        this.dni=dni;
        this.nombre=nombre;
        this.apellidos=apellidos;
        this.fechaNacimiento=fechaNacimiento;
        this.fechaExpedicionTitulo=fechaExpedicionTitulo;
    }


//Getters
    public String getNombre() {
        return nombre;
    }
    public String getApellidos() {
        return apellidos;
    }
    public String getDni() {
        return dni;

    }
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
    
    public LocalDate getFechaExpedicionTitulo(){
        return fechaExpedicionTitulo;
    } 

//Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
    public void setDni(String dni) {
        this.dni = dni;
    }
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    public void setFechaExpedicionTitulo(LocalDate fechaExpedicion){
        this.fechaExpedicionTitulo=fechaExpedicion;
    }

    @Override
    public String toString() {
        return "Patron [dni=" + dni + ", nombre=" + nombre + ", apellidos=" + apellidos + 
           ", fechaNacimiento=" + fechaNacimiento + 
           ", fechaExpedicionTitulo=" + fechaExpedicionTitulo + "]";
    }
}
