package es.uco.pw.grupo12.controller.patron;

import es.uco.pw.grupo12.model.domain.patron.Patron;
import es.uco.pw.grupo12.model.repository.PatronRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DarDeAltaPatronesController {

    private final PatronRepository patronRepository;

    
    public DarDeAltaPatronesController(PatronRepository patronRepository) {
        this.patronRepository = patronRepository;
    }

    // Muestra el formulario de alta de un patrón.
    
    @GetMapping("/altaPatron")
    public String mostrarFormularioAlta(Model model) {
        // Añadimos un objeto patrón vacío si no viene uno de un error anterior
        if (!model.containsAttribute("patron")) {
            model.addAttribute("patron", new Patron());
        }
        return "altaPatronView";
    }

    /**
       Procesa el formulario de alta de patrón.
       Comprueba si ya existe un patrón con el mismo DNI antes de insertarlo.
     */
    @PostMapping("/altaPatron")
    public String procesarAltaPatron(@ModelAttribute Patron patron, Model model, RedirectAttributes redirectAttributes) {

        // 1️. Verificar si ya existe un patrón con ese DNI
        Patron existente = patronRepository.findByDni(patron.getDni());

        if (existente != null) {
            model.addAttribute("error", "Ya existe un patrón con el DNI introducido.");
            model.addAttribute("patron", patron); // Devolvemos el patrón con los datos
            return "altaPatronView"; // Vuelve al formulario
        }

        // 2. Insertar nuevo patrón
        boolean exito = patronRepository.darDeAltaPatron(patron);

        // 3️. Confirmar en la vista
        if (exito) {
            // Usamos RedirectAttributes para que el mensaje sobreviva a la redirección
            redirectAttributes.addFlashAttribute("mensaje", "Patrón '" + patron.getNombre() + "' dado de alta correctamente.");
            return "redirect:/altaPatronExito"; // Redirigimos a una URL de éxito
        } else {
            // Error al guardar
            model.addAttribute("error", "Error al guardar el patrón en la base de datos.");
            model.addAttribute("patron", patron);
            return "altaPatronView";
        }
    }
    
    
    // Muestra la página de éxito tras el alta.
    @GetMapping("/altaPatronExito")
    public String mostrarExitoAlta() {
        return "resultadoAltaPatronView"; 
    }
}