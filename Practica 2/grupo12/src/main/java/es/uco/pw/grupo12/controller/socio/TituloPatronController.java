package es.uco.pw.grupo12.controller.socio;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.uco.pw.grupo12.model.domain.socio.Socio;
import es.uco.pw.grupo12.model.repository.SocioRepository;

@Controller
public class TituloPatronController {

    private final SocioRepository socioRepository;

    public TituloPatronController(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
    }

    /**
     * Muestra el formulario para buscar al socio por DNI.
     */
    @GetMapping("/registrarTitulo")
    public String mostrarFormularioBusqueda() {
        return "registrarTituloBuscar"; // Vista 1: Formulario de búsqueda
    }

    /**
     * Procesa la actualización del título.
     */
    @PostMapping("/registrarTitulo")
    public String procesarRegistroTitulo(@RequestParam String dni, RedirectAttributes redirectAttributes) {

        // 1. Buscar al socio
        Socio socio = socioRepository.findSocioByDni(dni);

        if (socio == null) {
            redirectAttributes.addFlashAttribute("error", "No se encontró ningún socio con el DNI: " + dni);
            return "redirect:/registrarTitulo";
        }

        // 2. Comprobar si ya tiene el título
        if (socio.getTituloPatron()) {
            redirectAttributes.addFlashAttribute("info", "El socio " + socio.getNombre() + " (DNI: " + dni + ") ya tenía el título de patrón registrado.");
            return "redirect:/registrarTitulo";
        }

        // 3. Actualizar el título
        boolean exito = socioRepository.updateTituloPatron(dni);

        if (exito) {
            redirectAttributes.addFlashAttribute("exito", "¡Título de patrón registrado correctamente para el socio " + socio.getNombre() + "!");
            return "redirect:/registrarTituloExito"; // Redirigir a página de éxito
        } else {
            redirectAttributes.addFlashAttribute("error", "Error en la base de datos al intentar actualizar el título.");
            return "redirect:/registrarTitulo";
        }
    }
    
    // Muestra la página de éxito.
     
    @GetMapping("/registrarTituloExito")
    public String mostrarExito() {
        return "registrarTituloExito";
    }
}