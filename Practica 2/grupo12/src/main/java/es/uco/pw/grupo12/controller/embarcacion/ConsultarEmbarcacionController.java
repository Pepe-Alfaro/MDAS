package es.uco.pw.grupo12.controller.embarcacion;

import java.util.ArrayList;
import java.util.List;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.uco.pw.grupo12.model.domain.embarcacion.Embarcacion;
import es.uco.pw.grupo12.model.domain.embarcacion.TipoEmbarcacion;
import es.uco.pw.grupo12.model.repository.EmbarcacionRepository;

@Controller
public class ConsultarEmbarcacionController {

    private final EmbarcacionRepository embarcacionRepository;

    
    public ConsultarEmbarcacionController(EmbarcacionRepository embarcacionRepository) {
        this.embarcacionRepository = embarcacionRepository;
    }

    /**
     * Muestra la página de consulta.
     * Si se recibe un parámetro 'tipo', realiza la búsqueda y muestra resultados.
     * Si no, solo muestra el formulario de búsqueda.
     */
    @GetMapping("/consultarEmbarcacion")
    public String consultarEmbarcaciones(
            @RequestParam(value = "tipo", required = false) String tipoSeleccionado,
            Model model) {

        // 1. Añadir siempre la lista de tipos para el desplegable
        model.addAttribute("tiposEmbarcacion", TipoEmbarcacion.values());
        model.addAttribute("tipoSeleccionado", tipoSeleccionado);
        
        // [Buena Práctica - Regla 2: Nombre de colección claro]
        List<Embarcacion> embarcacionesEncontradas;

        // 2. Comprobar si el usuario ha enviado un tipo para buscar
        if (tipoSeleccionado != null && !tipoSeleccionado.isEmpty()) {
            try {
                // [Regla de Nombrado: Regla 10 - Evitar codificaciones del tipo de dato en el nombre]
                TipoEmbarcacion tipoEmbarcacion = TipoEmbarcacion.valueOf(tipoSeleccionado);
                embarcacionesEncontradas = embarcacionRepository.findByTipo(tipoEmbarcacion);
            } catch (IllegalArgumentException e) {
                // Si el tipo no es válido, devolvemos una lista vacía
                embarcacionesEncontradas = new ArrayList<>();
                model.addAttribute("error", "Tipo de embarcación no válido.");
            }
        } else {
            // Si es la primera vez que carga la página, lista vacía
            embarcacionesEncontradas = new ArrayList<>();
        }

        // 3. Añadir los resultados al modelo
        model.addAttribute("embarcaciones", embarcacionesEncontradas);

        // 4. Devolver la vista
        return "consultarEmbarcacionView";
    }
}