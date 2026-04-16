package es.uco.pw.grupo12.controller.patron;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import es.uco.pw.grupo12.model.domain.patron.Patron;
import es.uco.pw.grupo12.model.repository.PatronRepository;

@Controller
public class ObtenerPatronesController {

    PatronRepository patronRepository;

    public ObtenerPatronesController(PatronRepository patronRepository) {
        this.patronRepository = patronRepository;
    }

    @GetMapping("/verPatrones")
    public ModelAndView obtenerPatrones() {
       List<Patron> patrones = patronRepository.obtenerPatrones();
       ModelAndView model = new ModelAndView("obtenerPatronesView.html");
       model.addObject("patrones", patrones);
       return model;
    }

}
