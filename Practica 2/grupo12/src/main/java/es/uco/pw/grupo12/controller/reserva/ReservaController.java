package es.uco.pw.grupo12.controller.reserva;

import es.uco.pw.grupo12.model.domain.embarcacion.Embarcacion;
import es.uco.pw.grupo12.model.domain.reserva.Reserva;
import es.uco.pw.grupo12.model.domain.socio.Socio;
import es.uco.pw.grupo12.model.repository.EmbarcacionRepository;
import es.uco.pw.grupo12.model.repository.ReservaRepository;
import es.uco.pw.grupo12.model.repository.SocioRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class ReservaController {

    private final ReservaRepository reservaRepository;
    private final EmbarcacionRepository embarcacionRepository;
    private final SocioRepository socioRepository;

    
    public ReservaController(ReservaRepository reservaRepository,
                             EmbarcacionRepository embarcacionRepository,
                             SocioRepository socioRepository) {
        this.reservaRepository = reservaRepository;
        this.embarcacionRepository = embarcacionRepository;
        this.socioRepository = socioRepository;
    }

    // Muestra el formulario para crear una nueva reserva.
     
    @GetMapping("/reservarEmbarcacion")
    public String mostrarFormularioReserva() {
        return "reservarEmbarcacionForm"; // Devuelve el nombre del HTML del formulario
    }

    //Procesa el envío del formulario de reserva y realiza las validaciones.
     
    @PostMapping("/procesarReserva")
    public String procesarReserva(
            @RequestParam("matricula") String matricula,
            @RequestParam("dniSocio") String dniSocio,
            @RequestParam("fecha") LocalDate fecha,
            @RequestParam("plazasSolicitadas") int plazasSolicitadas,
            @RequestParam("descripcion") String descripcion,
            Model model) {

        // 1. Validar que el Socio existe 
        Socio socio = socioRepository.findSocioByDni(dniSocio);
        if (socio == null) {
            model.addAttribute("exito", false);
            model.addAttribute("mensaje", "Error: El DNI del socio no existe.");
            return "reservaResultado";
        }

        // 2. Validar que la Embarcación existe 
        Embarcacion embarcacion = embarcacionRepository.findByMatricula(matricula);
        if (embarcacion == null) {
            model.addAttribute("exito", false);
            model.addAttribute("mensaje", "Error: La matrícula de la embarcación no existe.");
            return "reservaResultado";
        }
 
        // 3.Comprobamos si el patrón es nulo
        // [Regla de Comentarios: Regla 1 - Se borra comentario desactualizado/historial]
        if (embarcacion.getPatron() == null || 
            embarcacion.getPatron().getDni() == null || 
            embarcacion.getPatron().getDni().isEmpty() || 
            embarcacion.getPatron().getDni().equals("-1")) {
            
            model.addAttribute("exito", false);
            model.addAttribute("mensaje", "Error: La embarcación '" + embarcacion.getNombre() + "' no tiene un patrón asociado y no se puede reservar.");
            return "reservaResultado";
        }
        
        // 4. NUEVA VALIDACIÓN: que la Fecha no es pasada
        if (fecha.isBefore(LocalDate.now())) {
            model.addAttribute("exito", false);
            model.addAttribute("mensaje", "Error: No se pueden realizar reservas para una fecha anterior a hoy (" + LocalDate.now() + ").");
            return "reservaResultado";
        }

        // 5. Validar Capacidad 
        // [Regla de Comentarios: Regla 3 - Renombrar variable para explicar intención y borrar comentario]
        int plazasExcluyendoAlPatron = embarcacion.getPlazas() - 1; 
        
        if (plazasSolicitadas <= 0 || plazasSolicitadas > plazasExcluyendoAlPatron) {
            model.addAttribute("exito", false);
            model.addAttribute("mensaje", "Error: Número de plazas no válido. La embarcación tiene " + plazasExcluyendoAlPatron + " plazas disponibles (restando al patrón).");
            return "reservaResultado";
        }

        // 6. Validar Disponibilidad de Fecha 
        boolean yaReservada = reservaRepository.isReservada(matricula, fecha);
        if (yaReservada) {
            model.addAttribute("exito", false);
            model.addAttribute("mensaje", "Error: La embarcación ya está reservada para el día " + fecha);
            return "reservaResultado";
        }

        // 7.Guardar reserva
        
        // [Regla de Nombrado: Regla 7 - Declarar variable para valor constante numérico]
        final double PRECIO_POR_PLAZA = 40.0;
        
        // Calcular precio 
        double precioTotal = plazasSolicitadas * PRECIO_POR_PLAZA;
        
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setEmbarcacion(embarcacion);
        nuevaReserva.setSocioSolicitante(socio);
        nuevaReserva.setFechaActividad(fecha);
        nuevaReserva.setPlazasSolicitadas(plazasSolicitadas);
        nuevaReserva.setDescripcion(descripcion);
        nuevaReserva.setPrecioTotal(precioTotal);

        boolean exito = reservaRepository.saveReserva(nuevaReserva);

        if (exito) {
            model.addAttribute("exito", true);
            model.addAttribute("mensaje", "¡Reserva completada con éxito! Precio total: " + String.format("%.2f", precioTotal) + "€");
        } else {
            model.addAttribute("exito", false);
            model.addAttribute("mensaje", "Error: No se pudo guardar la reserva en la base de datos.");
        }

        return "reservaResultado"; // Muestra la página de éxito o error
    }
}