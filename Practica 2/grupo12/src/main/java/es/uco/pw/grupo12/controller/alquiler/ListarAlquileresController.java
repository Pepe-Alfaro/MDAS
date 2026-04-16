package es.uco.pw.grupo12.controller.alquiler;

import es.uco.pw.grupo12.model.domain.alquiler.Alquiler;
import es.uco.pw.grupo12.model.repository.AlquilerRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ListarAlquileresController {

    private final AlquilerRepository alquilerRepository;


    public ListarAlquileresController(AlquilerRepository alquilerRepository) {
        this.alquilerRepository = alquilerRepository;
    }

    //Obtiene todos los alquileres futuros y los pasa a la vista.
    
    @GetMapping("/listarAlquileres")
    public String listarAlquileres(Model model) {
        
        List<Alquiler> alquileres = alquilerRepository.findFuturosAlquileres();
        
        model.addAttribute("alquileres", alquileres);
        
        return "listarAlquileresView"; 
    }
}