package es.uco.pw.grupo12.model.domain.inscripcion;

import es.uco.pw.grupo12.model.domain.socio.Socio;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*
  Representa una inscripción al club náutico.
  Puede ser individual o familiar, con socios vinculados y una cuota asociada.
*/
public class Inscripcion {

    // Constantes de precios
    private static final double CUOTA_ADULTO_TITULAR = 300.0;
    private static final double CUOTA_SEGUNDO_ADULTO = 250.0;
    private static final double CUOTA_HIJO = 100.0;

    // Atributos
    private int idInscripcion;
    private Socio socioTitular;
    private List<Socio> sociosVinculados; // pareja e hijos
    private double cuota;
    private LocalDate fechaCreacion;
    private TipoInscripcion tipo; // INDIVIDUAL o FAMILIAR
    private long numeroFamiliares;

    public Inscripcion() {
        this.idInscripcion = -1;
        this.socioTitular = new Socio();
        this.sociosVinculados = new ArrayList<>();
        this.cuota = CUOTA_ADULTO_TITULAR;
        this.fechaCreacion = LocalDate.now();
        this.tipo = TipoInscripcion.INDIVIDUAL;
    }

    
    // Constructor para una inscripción individual.
     
    public Inscripcion(int idInscripcion, Socio socioTitular, LocalDate fechaCreacion) {
        this.idInscripcion = idInscripcion;
        this.socioTitular = socioTitular;
        this.fechaCreacion = fechaCreacion;
        this.sociosVinculados = new ArrayList<>();
        this.tipo = TipoInscripcion.INDIVIDUAL;
        this.cuota = CUOTA_ADULTO_TITULAR;
    }

    
     // Convierte la inscripción a familiar y añade socios vinculados. (Actualiza automáticamente la cuota)
       
     
    public void ampliarAFamiliar(List<Socio> nuevosSocios) {
        if (this.tipo == TipoInscripcion.INDIVIDUAL) {
            this.tipo = TipoInscripcion.FAMILIAR;
        }

        this.sociosVinculados.addAll(nuevosSocios);
        calcularCuota(LocalDate.now());
    }

  
     // Calcula la cuota total de la inscripción familiar.
    
     
    public void calcularCuota(LocalDate fechaHoy) {
        int numAdultos = obtenerCantidadAdultos(fechaHoy);
        int numHijos = obtenerCantidadHijos(numAdultos);

        double total = calcularCuotaAdultos(numAdultos);
        total += calcularCuotaHijos(numHijos);

        this.cuota = total;
    }

    private int obtenerCantidadAdultos(LocalDate fechaHoy) {
        int count = 0;

        for (Socio socio : sociosVinculados) {
            if (socio.esMayorDeEdad()) {
                count++;
            }
        }
        return count;
    }

    private int obtenerCantidadHijos(int numAdultos) {
        return sociosVinculados.size() - numAdultos;
    }

    private double calcularCuotaAdultos(int numAdultos) {
        double total = 0.0;

        // Cuota del titular
        if (numAdultos > 0) {
            total += CUOTA_ADULTO_TITULAR;
            numAdultos--;
        }

        // Cuota del cónyuge
        if (numAdultos > 0) {
            total += CUOTA_SEGUNDO_ADULTO;
        }

    return total;
    }

    private double calcularCuotaHijos(int numHijos) {
        return numHijos * CUOTA_HIJO;
    }

    // Método auxiliar para calcular edad.
     
    // Decisión de diseño: Se transforma en una función pura al recibir la fecha de 
    // referencia por parámetro, eliminando la dependencia oculta de LocalDate.now() (Regla 4 de funciones).

    // Getters

    public int getIdInscripcion() {
        return idInscripcion;
    }
    public Socio getSocioTitular() {
        return socioTitular;
    }
    public List<Socio> getSociosVinculados() {
        return sociosVinculados;
    }
    public double getCuota() {
        return cuota;
    }
    public void setCuota(double cuota) {
        this.cuota = cuota;
    }
    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }
    public TipoInscripcion getTipo() {
        return tipo;
    }
    public long getNumeroFamiliares() {
    return numeroFamiliares;
    }
    // Setters
    public void setIdInscripcion(int idInscripcion) {
        this.idInscripcion = idInscripcion;
    }
    public void setSocioTitular(Socio socioTitular) {
        this.socioTitular = socioTitular;
    }
    public void setSociosVinculados(List<Socio> sociosVinculados) {
        this.sociosVinculados = sociosVinculados;
    }
    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    public void setTipo(TipoInscripcion tipo) {
        this.tipo = tipo;
    }
    public void setNumeroFamiliares(long numeroFamiliares) {
        this.numeroFamiliares = numeroFamiliares;
    }

    @Override
    public String toString() {
        return "Inscripcion [idInscripcion=" + idInscripcion + ", socioTitular=" + socioTitular + ", sociosVinculados="
                + sociosVinculados + ", cuota=" + cuota + ", fechaCreacion=" + fechaCreacion + ", tipo=" + tipo
                + ", getIdInscripcion()=" + getIdInscripcion() + ", getSocioTitular()=" + getSocioTitular()
                + ", getSociosVinculados()=" + getSociosVinculados() + ", getCuota()=" + getCuota()
                + ", getFechaCreacion()=" + getFechaCreacion() + ", getTipo()=" + getTipo() + ", getClass()="
                + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
    }
}
