package es.uco.pw.grupo12.controller.socio;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.uco.pw.grupo12.model.domain.socio.Socio;
import es.uco.pw.grupo12.model.repository.InscripcionRepository;
import es.uco.pw.grupo12.model.repository.SocioRepository;

@Controller
public class InscripcionSocioController {

    private final SocioRepository socioRepository;
    private final InscripcionRepository inscripcionRepository;

    public InscripcionSocioController(SocioRepository socioRepository, InscripcionRepository inscripcionRepository) {
        this.socioRepository = socioRepository;
        this.inscripcionRepository = inscripcionRepository;
    }

    // Muestra el formulario de inscripción de socio.
     
    @GetMapping("/inscribirSocio")
    public String mostrarFormularioInscripcion(Model model) {
        model.addAttribute("socio", new Socio());
        return "inscribirSocioForm"; 
    }

    // Procesa el formulario de inscripción de socio.
     
    @PostMapping("/inscribirSocio")
    public String procesarInscripcionSocio(@ModelAttribute Socio socio, RedirectAttributes redirectAttributes) {

        // 1. Validar DNI
        Socio socioExistente = socioRepository.findSocioByDni(socio.getDni());
        if (socioExistente != null) {
            redirectAttributes.addFlashAttribute("error", "El DNI " + socio.getDni() + " ya está registrado.");
            return "redirect:/inscribirSocio"; // Recarga el formulario
        }

        // 2. Validar Edad (Debe ser mayor de 18)
        if (!socio.esMayorDeEdad()) {
            redirectAttributes.addFlashAttribute("error", "El socio debe ser mayor de edad para inscribirse como titular.");
            return "redirect:/inscribirSocio"; // Recarga el formulario
        }

        // 3. Establecer fecha de inscripción (hoy)
        socio.setFechaInscripcion(LocalDate.now());

        // 4. Guardar el Socio primero
        boolean socioGuardado = socioRepository.saveSocio(socio);

        if (!socioGuardado) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el socio en la base de datos.");
            return "redirect:/inscribirSocio";
        }

        // 5. Crear la Inscripción Individual
        int nuevaInscripcionId = inscripcionRepository.saveInscripcionIndividual(socio);

        if (nuevaInscripcionId == -1) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la inscripción. Contacte a soporte.");
            return "redirect:/inscribirSocio";
        }

        // 6. Vincular la Inscripción al Socio (Actualizar FK)
        boolean fkActualizada = socioRepository.updateInscripcionFk(socio.getDni(), nuevaInscripcionId);

        if (!fkActualizada) {
            redirectAttributes.addFlashAttribute("error", "Error al vincular la inscripción. Contacte a soporte.");
            return "redirect:/inscribirSocio";
        }

        // 7. Éxito
        redirectAttributes.addFlashAttribute("exito", "¡Socio " + socio.getNombre() + " inscrito correctamente con ID de inscripción " + nuevaInscripcionId + "!");
        return "redirect:/inscribirSocioExito"; // Redirigir a una página de éxito
    }

    // Muestra la página de éxito.
     
    @GetMapping("/inscribirSocioExito")
    public String mostrarExito() {
        return "inscripcionSocioExito";
    }
}