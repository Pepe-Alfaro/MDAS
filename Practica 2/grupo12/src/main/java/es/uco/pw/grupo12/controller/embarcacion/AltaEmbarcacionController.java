package es.uco.pw.grupo12.controller.embarcacion;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.uco.pw.grupo12.model.domain.embarcacion.Embarcacion;
import es.uco.pw.grupo12.model.domain.embarcacion.TipoEmbarcacion;
import es.uco.pw.grupo12.model.repository.EmbarcacionRepository;

@Controller
public class AltaEmbarcacionController {

    private final EmbarcacionRepository embarcacionRepository;

    
    public AltaEmbarcacionController(EmbarcacionRepository embarcacionRepository) {
        this.embarcacionRepository = embarcacionRepository;
    }

    // Muestra el formulario para dar de alta una embarcación.
    @GetMapping("/altaEmbarcacion")
    public String mostrarFormulario(Model model) {
        model.addAttribute("embarcacion", new Embarcacion());
        model.addAttribute("tiposEmbarcacion", TipoEmbarcacion.values());
        
        // Apunta a "templates/altaEmbarcacionForm.html"
        return "altaEmbarcacionForm"; 
    }

    
    // Procesa el formulario de alta de embarcación.
     
    @PostMapping("/altaEmbarcacion")
    public String procesarFormulario(@ModelAttribute Embarcacion embarcacion, RedirectAttributes redirectAttributes, Model model) {

        // Validacion 1: Matrícula única
        if (embarcacionRepository.findByMatricula(embarcacion.getMatricula()) != null) {
            model.addAttribute("error", "La matrícula '" + embarcacion.getMatricula() + "' ya existe.");
            model.addAttribute("tiposEmbarcacion", TipoEmbarcacion.values());
            return "altaEmbarcacionForm"; // Vuelve al formulario con error
        }

        // Validacion 2: Nombre único
        if (embarcacionRepository.findByNombre(embarcacion.getNombre()) != null) {
            model.addAttribute("error", "El nombre '" + embarcacion.getNombre() + "' ya existe.");
            model.addAttribute("tiposEmbarcacion", TipoEmbarcacion.values());
            return "altaEmbarcacionForm"; // Vuelve al formulario con error
        }
        
        // Guardar la embarcación
        boolean exito = embarcacionRepository.save(embarcacion);

        if (exito) {
            redirectAttributes.addFlashAttribute("exito", "Embarcación '" + embarcacion.getNombre() + "' dada de alta con éxito.");
            return "redirect:/altaEmbarcacionExito";
        } else {
            model.addAttribute("error", "Error desconocido al guardar la embarcación.");
            model.addAttribute("tiposEmbarcacion", TipoEmbarcacion.values());
            return "altaEmbarcacionForm"; 
        }
    }

    
    //Muestra la página de éxito.

    @GetMapping("/altaEmbarcacionExito")
    public String mostrarExito() {
        // Apunta a "templates/altaEmbarcacionExito.html"
        return "altaEmbarcacionExito"; 
    }
}