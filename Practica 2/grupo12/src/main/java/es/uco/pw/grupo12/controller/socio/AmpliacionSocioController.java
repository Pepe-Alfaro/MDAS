package es.uco.pw.grupo12.controller.socio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.uco.pw.grupo12.model.domain.inscripcion.Inscripcion;
import es.uco.pw.grupo12.model.domain.socio.Socio;
import es.uco.pw.grupo12.model.repository.InscripcionRepository;
import es.uco.pw.grupo12.model.repository.SocioRepository;

@Controller
public class AmpliacionSocioController {

    private final SocioRepository socioRepository;
    private final InscripcionRepository inscripcionRepository;

    public AmpliacionSocioController(SocioRepository socioRepository, InscripcionRepository inscripcionRepository) {
        this.socioRepository = socioRepository;
        this.inscripcionRepository = inscripcionRepository;
    }

    //Busca socio titular
    @GetMapping("/ampliarInscripcion")
    public String mostrarFormularioBusqueda() {
        return "ampliarInscripcionBuscar"; // Vista 1: Buscar titular
    }

    // Procesa la búsqueda del socio titular.
     
    @PostMapping("/ampliarInscripcion/buscar")
    public String buscarSocioTitular(@RequestParam String dniTitular, RedirectAttributes redirectAttributes, Model model) {
        
        Socio titular = socioRepository.findSocioByDni(dniTitular);
        Inscripcion inscripcion = inscripcionRepository.findInscripcionByTitularDni(dniTitular);

        // Validaciones
        if (titular == null) {
            redirectAttributes.addFlashAttribute("error", "El socio con DNI " + dniTitular + " no existe.");
            return "redirect:/ampliarInscripcion";
        }
        if (inscripcion == null) {
            redirectAttributes.addFlashAttribute("error", "El socio con DNI " + dniTitular + " existe, pero no es titular de ninguna inscripción.");
            return "redirect:/ampliarInscripcion";
        }

        // Éxito:Redirigimos al formulario de ampliación, pasando los datos del titular
        return "redirect:/ampliarInscripcion/formulario?dniTitular=" + dniTitular + "&inscripcionId=" + inscripcion.getIdInscripcion();
    }

    // Muestra el formulario para añadir un nuevo miembro (adulto o hijo).
     
    @GetMapping("/ampliarInscripcion/formulario")
    public String mostrarFormularioAmpliacion(@RequestParam String dniTitular, @RequestParam int inscripcionId, Model model) {
        
        // Pasamos los datos del titular y la inscripción a la vista
        model.addAttribute("dniTitular", dniTitular);
        model.addAttribute("inscripcionId", inscripcionId);
        
        // Creamos un objeto Socio vacío para el formulario (el *nuevo* miembro)
        model.addAttribute("nuevoSocio", new Socio());

        return "ampliarInscripcionForm"; // Vista 2: Añadir familiar
    }

    // Procesa y guarda el nuevo miembro familiar.
     
    @PostMapping("/ampliarInscripcion/guardar")
    public String guardarAmpliacion(@ModelAttribute Socio nuevoSocio, 
                                     @RequestParam String dniTitular, 
                                     @RequestParam int inscripcionId, 
                                     RedirectAttributes redirectAttributes) {

        // 1. Validar DNI del nuevo socio 
        Socio socioExistente = socioRepository.findSocioByDni(nuevoSocio.getDni());
        if (socioExistente != null) {
            redirectAttributes.addFlashAttribute("error", "El DNI del nuevo miembro (" + nuevoSocio.getDni() + ") ya está registrado.");
            return "redirect:/ampliarInscripcion/formulario?dniTitular=" + dniTitular + "&inscripcionId=" + inscripcionId;
        }

        // 2. Configurar datos del nuevo socio
        nuevoSocio.setFechaInscripcion(LocalDate.now());
        nuevoSocio.setIdInscripcionFk(inscripcionId); 

        // 3. Guardar el nuevo socio 
        boolean socioGuardado = socioRepository.saveSocio(nuevoSocio);
        if (!socioGuardado) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el nuevo socio familiar.");
            return "redirect:/ampliarInscripcion/formulario?dniTitular=" + dniTitular + "&inscripcionId=" + inscripcionId;
        }

        // 4. Recalcular la cuota
        List<Socio> familiares = socioRepository.findAllByInscripcionId(inscripcionId);
      
        Inscripcion inscripcionTemp = new Inscripcion(); 
        inscripcionTemp.setSociosVinculados(familiares);
        inscripcionTemp.calcularCuota(); 
        
        double nuevaCuota = inscripcionTemp.getCuota();

        // 5. Actualizar la inscripción (cambiar a FAMILIAR y poner nueva cuota)
        boolean inscripcionActualizada = inscripcionRepository.updateInscripcionAFamiliar(inscripcionId, nuevaCuota);
        if (!inscripcionActualizada) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la cuota de la inscripción.");
            return "redirect:/ampliarInscripcion/formulario?dniTitular=" + dniTitular + "&inscripcionId=" + inscripcionId;
        }

        // 6. Éxito
        redirectAttributes.addFlashAttribute("exito", "¡Familiar añadido correctamente! La nueva cuota es: " + nuevaCuota + "€");
        return "redirect:/ampliacionSocioExito";
    }

    // Muestra la página de éxito de la ampliación.
     
    @GetMapping("/ampliacionSocioExito")
    public String mostrarExitoAmpliacion() {
        return "ampliacionSocioExito";
    }
}