package es.uco.pw.grupo12.model.domain.reserva;
import es.uco.pw.grupo12.model.domain.socio.Socio;
import es.uco.pw.grupo12.model.domain.embarcacion.Embarcacion;
import java.time.LocalDate;


public class Reserva {

    private int idReserva;                   // Identificador único de la reserva
    private Socio socioSolicitante;          // Socio que solicita la actividad
    private Embarcacion embarcacion;         // Embarcación reservada (con patrón)
    private LocalDate fechaActividad;        // Fecha del evento (solo un día)
    private int plazasSolicitadas;           // Número de personas que participarán
    private String descripcion;              // Propósito o descripción de la actividad
    private double precioTotal;              // Precio calculado (40€ por persona)

    public Reserva() {
        this.idReserva = -1;
        this.socioSolicitante = new Socio();
        this.embarcacion = new Embarcacion();
        this.fechaActividad = LocalDate.now();
        this.plazasSolicitadas = 0;
        this.descripcion = "";
        this.precioTotal = 0.0;
    }     
    
    public Reserva(int idReserva, Socio socioSolicitante, Embarcacion embarcacion,
                   LocalDate fechaActividad, int plazasSolicitadas, String descripcion) {
        this.idReserva = idReserva;
        this.socioSolicitante = socioSolicitante;
        this.embarcacion = embarcacion;
        this.fechaActividad = fechaActividad;
        this.plazasSolicitadas = plazasSolicitadas;
        this.descripcion = descripcion;
        this.precioTotal = calcularPrecio();
    }
  private double calcularPrecio() {
        return plazasSolicitadas * 40.0;
    }

    // Getters 
    public int getIdReserva() { 
        return idReserva; 
    }
    
    public Socio getSocioSolicitante() { 
        return socioSolicitante; 
    }

    public Embarcacion getEmbarcacion() { 
        return embarcacion; 
    }

    public LocalDate getFechaActividad() { 
        return fechaActividad; 
    }

    public double getPrecioTotal() { 
        return precioTotal; 
    }

    public int getPlazasSolicitadas() { 
        return plazasSolicitadas;
     }
     public String getDescripcion() { 
        return descripcion;
     }

    
     //Setters
    public void setIdReserva(int idReserva) { 
        this.idReserva = idReserva; }

    
    public void setSocioSolicitante(Socio socioSolicitante) { 
        this.socioSolicitante = socioSolicitante; 
    }

  
    public void setEmbarcacion(Embarcacion embarcacion) { 
        this.embarcacion = embarcacion; 
    }

   
    public void setFechaActividad(LocalDate fechaActividad) { 
        this.fechaActividad = fechaActividad; 
    }

    
    public void setPlazasSolicitadas(int plazasSolicitadas) {
        this.plazasSolicitadas = plazasSolicitadas;
        this.precioTotal = calcularPrecio();
    }

    
    public void setDescripcion(String descripcion) { 
        this.descripcion = descripcion; 
    }

    public void setPrecioTotal(double precioTotal){
        this.precioTotal=precioTotal;
    }

    @Override
    public String toString() {
        return "Reserva [idReserva=" + idReserva + ", socioSolicitante=" + socioSolicitante + ", embarcacion="
                + embarcacion + ", fechaActividad=" + fechaActividad + ", plazasSolicitadas=" + plazasSolicitadas
                + ", descripcion=" + descripcion + ", precioTotal=" + precioTotal + ", calcularPrecio()="
                + calcularPrecio() + ", getIdReserva()=" + getIdReserva() + ", getSocioSolicitante()="
                + getSocioSolicitante() + ", getEmbarcacion()=" + getEmbarcacion() + ", getFechaActividad()="
                + getFechaActividad() + ", getPrecioTotal()=" + getPrecioTotal() + ", getPlazasSolicitadas()="
                + getPlazasSolicitadas() + ", getDescripcion()=" + getDescripcion() + ", getClass()=" + getClass()
                + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
    }
}