package es.uco.pw.grupo12.controller.embarcacion;

import es.uco.pw.grupo12.model.domain.embarcacion.Embarcacion;
import es.uco.pw.grupo12.model.repository.EmbarcacionRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class BuscarEmbarcacionController {

    private final EmbarcacionRepository embarcacionRepository;

    
    public BuscarEmbarcacionController(EmbarcacionRepository embarcacionRepository) {
        this.embarcacionRepository = embarcacionRepository;
    }

    
    // Muestra el formulario para introducir las fechas de búsqueda.
    
    @GetMapping("/buscarEmbarcacion")
    public String mostrarFormularioBusqueda() {
        return "buscarEmbarcacionForm";
    }

    
    //Procesa la búsqueda y muestra los resultados.
    
    @GetMapping("/resultadosEmbarcacion")
    public String buscarEmbarcacionesDisponibles(
            @RequestParam("fechaInicio") LocalDate fechaInicio,
            @RequestParam("fechaFin") LocalDate fechaFin,
            Model model) {
        
        List<Embarcacion> embarcaciones = embarcacionRepository.findEmbarcacionesDisponibles(fechaInicio, fechaFin);
        
        model.addAttribute("embarcaciones", embarcaciones);
        model.addAttribute("fechaInicio", fechaInicio); // Para mostrar en la vista
        model.addAttribute("fechaFin", fechaFin);     // Para mostrar en la vista
        
        return "buscarEmbarcacionResultados";
    }
}