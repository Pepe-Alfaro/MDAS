package es.uco.pw.grupo12.controller.socio;

import es.uco.pw.grupo12.model.repository.SocioRepository;
import es.uco.pw.grupo12.model.domain.socio.Socio;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ListarSociosController {

    private final SocioRepository socioRepository;

    public ListarSociosController(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
    }

 
     //Obtiene todos los socios con su estado de titular y los pasa a la vista.
    
    @GetMapping("/verSocios")
    public String listarSocios(Model model) {
        
        // Llamamos al nuevo método del repositorio
        List<Socio> listaSocios = socioRepository.findAllSociosWithTitularStatus();
        
        // Añadimos la lista al modelo
        model.addAttribute("socios", listaSocios);
        
        // Devolvemos el nombre del archivo HTML (sin .html)
        return "listarSociosView"; 
    }
}