package es.uco.pw.grupo12.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    //Mapea la raíz del sitio (/) a la vista home.html.
    @GetMapping("/")
    public String home() {
        return "home"; // Esto buscará un archivo llamado home.html en templates/
    }
}