package es.uco.pw.grupo12.model.domain.alquiler;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// [Refactorización: Encapsulación (Extraer Clase) - Encapsula la responsabilidad del periodo]
public class RangoFechas {
    
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public RangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public LocalDate getFechaInicio() { 
        return fechaInicio; 
    }
    
    public void setFechaInicio(LocalDate fechaInicio) { 
        this.fechaInicio = fechaInicio; 
    }

    public LocalDate getFechaFin() { 
        return fechaFin; 
    }
    
    public void setFechaFin(LocalDate fechaFin) { 
        this.fechaFin = fechaFin; 
    }

    // El cálculo de días se encapsula ahora en esta clase especializada
    public long getDias() {
        if (fechaInicio != null && fechaFin != null) {
            return ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
        }
        return 0;
    }
}