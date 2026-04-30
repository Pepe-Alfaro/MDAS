package es.uco.pw.grupo12.controller.patron;

import es.uco.pw.grupo12.model.domain.patron.Patron;
import es.uco.pw.grupo12.model.domain.embarcacion.Embarcacion;
import es.uco.pw.grupo12.model.repository.PatronRepository;
import es.uco.pw.grupo12.model.repository.EmbarcacionRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AsociarPatronEmbarcacionController {

    private final PatronRepository patronRepository;
    private final EmbarcacionRepository embarcacionRepository;

    
    public AsociarPatronEmbarcacionController(PatronRepository patronRepository, EmbarcacionRepository embarcacionRepository) {
        this.patronRepository = patronRepository;
        this.embarcacionRepository = embarcacionRepository;
    }

    
    // Muestra el formulario para elegir embarcación y patrón.
    
    @GetMapping("/asociarPatron")
    public String mostrarFormulario(Model model) {
        model.addAttribute("embarcaciones", embarcacionRepository.findAll());
        model.addAttribute("patrones", patronRepository.obtenerPatrones());
        return "asociarPatronView"; // Vista 1: El formulario
    }

    
    // Procesa la asociación entre patrón y embarcación.
    
    @PostMapping("/asociarPatron")
    public String asociarPatron(
            @RequestParam("matricula") String matricula,
            @RequestParam("dniPatron") String dniPatron,
            @RequestParam(value = "confirmar", required = false) String confirmar,
            Model model) {

        Embarcacion embarcacion = embarcacionRepository.findByMatricula(matricula);
        Patron patronNuevo = patronRepository.findByDni(dniPatron);

        // Validación básica
        if (embarcacion == null || patronNuevo == null) {
            model.addAttribute("error", "Embarcación o patrón no encontrados.");
            model.addAttribute("embarcaciones", embarcacionRepository.findAll());
            model.addAttribute("patrones", patronRepository.obtenerPatrones());
            return "asociarPatronView";
        }

        // REGLA 1: "El patrón elegido no debe estar asignado a ninguna otra embarcación."
        if (patronRepository.estaAsignado(dniPatron)) {
            model.addAttribute("error", "El patrón " + patronNuevo.getNombre() + " ya está asignado a otra embarcación.");
            model.addAttribute("embarcaciones", embarcacionRepository.findAll());
            model.addAttribute("patrones", patronRepository.obtenerPatrones());
            return "asociarPatronView";
        }

        // REGLA 2: "Si la embarcación tenía asignado un patrón anteriormente..."
        if (embarcacion.getPatron() != null && !embarcacion.getPatron().getDni().equals("-1")) {

            // REGLA 2A: "...debe informarse al usuario para que confirme..."
            if (confirmar == null) {
                // No hay confirmación, mostramos la vista de confirmación
                model.addAttribute("embarcacion", embarcacion);
                model.addAttribute("patronNuevo", patronNuevo);
                // [Regla de Comentarios: Regla 2 - Se elimina comentario de numeración interna del programador]
                return "confirmarReemplazoView";
            }
            
        }

        // Lógica final de asociación
        // Esta es la única llamada necesaria. Actualiza la FK en la tabla Embarcacion.
        embarcacionRepository.asociarPatron(matricula, dniPatron);

        model.addAttribute("mensaje", "¡Patrón " + patronNuevo.getNombre() + " asociado correctamente a la embarcación " + embarcacion.getNombre() + "!");
        return "resultadoAsociacionView"; // Vista 3: Éxito
    }
}