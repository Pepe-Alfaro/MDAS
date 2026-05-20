package es.uco.pw.grupo12.model.domain; // Ajusta el paquete si lo pones en otra ruta

import java.time.LocalDate;

// [Refactorización: Herencia (Extraer Superclase) - Agrupa los atributos comunes de Socio y Patron]
public abstract class Persona {
    
    private String dni;
    private String nombre;
    private String apellidos;
    private LocalDate fechaNacimiento;

    public Persona() {
        this.dni = "-1";
        this.nombre = "";
        this.apellidos = "";
        this.fechaNacimiento = LocalDate.now();
    }

    public Persona(String dni, String nombre, String apellidos, LocalDate fechaNacimiento) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
    }

    // Getters
    public String getDni() { 
        return dni; 
    }
    
    public String getNombre() { 
        return nombre; 
    }
    
    public String getApellidos() { 
        return apellidos; 
    }
    
    public LocalDate getFechaNacimiento() { 
        return fechaNacimiento; 
    }

    // Setters
    public void setDni(String dni) { 
        this.dni = dni; 
    }
    
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }
    
    public void setApellidos(String apellidos) { 
        this.apellidos = apellidos; 
    }
    
    public void setFechaNacimiento(LocalDate fechaNacimiento) { 
        this.fechaNacimiento = fechaNacimiento; 
    }
}