package es.uco.pw.grupo12.controller.alquiler;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.uco.pw.grupo12.model.domain.alquiler.Alquiler;
import es.uco.pw.grupo12.model.domain.embarcacion.Embarcacion;
import es.uco.pw.grupo12.model.domain.socio.Socio;
import es.uco.pw.grupo12.model.repository.AlquilerRepository;
import es.uco.pw.grupo12.model.repository.EmbarcacionRepository;
import es.uco.pw.grupo12.model.repository.SocioRepository;

@Controller
public class AlquilerController {

    private final SocioRepository socioRepository;
    private final EmbarcacionRepository embarcacionRepository;
    private final AlquilerRepository alquilerRepository;


    public AlquilerController(SocioRepository socioRepository, EmbarcacionRepository embarcacionRepository, AlquilerRepository alquilerRepository) {
        this.socioRepository = socioRepository;
        this.embarcacionRepository = embarcacionRepository;
        this.alquilerRepository = alquilerRepository;
    }

    @GetMapping("/alquilar")
    public String mostrarFormulario(Model model) {
        model.addAttribute("sociosConTitulo", socioRepository.findSociosConTitulo());
        model.addAttribute("todosLosSocios", socioRepository.findAllSociosWithTitularStatus());
        model.addAttribute("embarcaciones", embarcacionRepository.findAll());
        return "alquilerFormulario";
    }

    @PostMapping("/alquilar/crear")
    public String crearAlquiler(
            @RequestParam("dniSocioTitular") String dniSocioTitular,
            @RequestParam("matriculaEmbarcacion") String matriculaEmbarcacion,
            @RequestParam("fechaInicio") LocalDate fechaInicio,
            @RequestParam("fechaFin") LocalDate fechaFin,
            @RequestParam(value = "dniSociosPasajeros", required = false) List<String> dniSociosPasajeros,
            Model model, RedirectAttributes redirectAttributes) {

        Socio titular = socioRepository.findSocioByDni(dniSocioTitular);
        Embarcacion embarcacion = embarcacionRepository.findByMatricula(matriculaEmbarcacion);

        // 1. Validación básica
        if (titular == null || embarcacion == null) {
            model.addAttribute("error", "Datos inválidos.");
            return mostrarFormulario(model);
        }

        // 2. Validar fechas 
        // [Regla de Nombrado: Regla 1 - Nombre informa qué mide y unidad]
        long duracionEnDias = ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
        int mesInicioAlquiler = fechaInicio.getMonthValue();
        boolean esTemporadaAlta = (mesInicioAlquiler >= 5 && mesInicioAlquiler <= 9);

        if (esTemporadaAlta) {
            if (duracionEnDias != 7 && duracionEnDias != 14) {
                model.addAttribute("error", "En temporada alta (May-Sep) el alquiler debe ser de 1 o 2 semanas (7 o 14 días).");
                return mostrarFormulario(model);
            }
        } else {
            if (duracionEnDias != 3) {
                model.addAttribute("error", "En temporada baja (Oct-Abr) el alquiler debe ser de 3 días.");
                return mostrarFormulario(model);
            }
        }

        // 3. Validar disponibilidad
        if (embarcacionRepository.checkDisponibilidadAlquiler(matriculaEmbarcacion, fechaInicio, fechaFin) ||
            embarcacionRepository.checkDisponibilidadReserva(matriculaEmbarcacion, fechaInicio, fechaFin)) {
            model.addAttribute("error", "La embarcación no está disponible en esas fechas.");
            return mostrarFormulario(model);
        }

        // 4. Validar capacidad
        if (dniSociosPasajeros == null) dniSociosPasajeros = new ArrayList<>();
        if (!dniSociosPasajeros.contains(dniSocioTitular)) dniSociosPasajeros.add(dniSocioTitular);

        if (dniSociosPasajeros.size() > embarcacion.getPlazas()) {
            model.addAttribute("error", "Se excede la capacidad de la embarcación (" + embarcacion.getPlazas() + ").");
            return mostrarFormulario(model);
        }

        // Guardar
        List<Socio> pasajeros = dniSociosPasajeros.stream().map(socioRepository::findSocioByDni).collect(Collectors.toList());
        
        Alquiler alquiler = new Alquiler();
        alquiler.setSocioTitular(titular);
        alquiler.setEmbarcacion(embarcacion);
        alquiler.setFechaInicio(fechaInicio);
        alquiler.setFechaFin(fechaFin);
        alquiler.setSociosPasajeros(pasajeros);

        int id = alquilerRepository.save(alquiler);
        if (id > 0) {
            alquilerRepository.savePasajeros(id, pasajeros);
            redirectAttributes.addFlashAttribute("alquiler", alquiler);
            return "redirect:/alquilerConfirmacion";
        } else {
            model.addAttribute("error", "Error al guardar en base de datos.");
            return mostrarFormulario(model);
        }
    }
    
    @GetMapping("/alquilerConfirmacion")
    public String confirmacion() { return "alquilerConfirmacion"; }
}