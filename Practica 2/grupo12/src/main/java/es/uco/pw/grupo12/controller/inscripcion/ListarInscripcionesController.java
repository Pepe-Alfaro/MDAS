package es.uco.pw.grupo12.controller.inscripcion;

import es.uco.pw.grupo12.model.domain.inscripcion.Inscripcion;
import es.uco.pw.grupo12.model.repository.InscripcionRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ListarInscripcionesController {

    private final InscripcionRepository inscripcionRepository;

   
    public ListarInscripcionesController(InscripcionRepository inscripcionRepository) {
        this.inscripcionRepository = inscripcionRepository;
    }

    // Maneja la petición GET para /verInscripciones.
     
    @GetMapping("/verInscripciones")
    public String listarInscripciones(Model model) {
        
        // 1. Llamar al nuevo método del repositorio
        List<Inscripcion> listaInscripciones = inscripcionRepository.findAllWithFamiliarCount();
        
        // 2. Añadir la lista al modelo
        model.addAttribute("inscripciones", listaInscripciones);
        
        // 3. Devolver el nombre de la vista
        return "listarInscripcionesView";
    }
}