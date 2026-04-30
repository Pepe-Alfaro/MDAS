package es.uco.pw.grupo12.api;

import es.uco.pw.grupo12.model.domain.embarcacion.Embarcacion;
import es.uco.pw.grupo12.model.domain.embarcacion.TipoEmbarcacion;
import es.uco.pw.grupo12.model.domain.patron.Patron;
import es.uco.pw.grupo12.model.repository.EmbarcacionRepository;
import es.uco.pw.grupo12.model.repository.PatronRepository;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/embarcaciones") 
public class EmbarcacionRestController {

    private final EmbarcacionRepository embarcacionRepository;
    private final PatronRepository patronRepository;

    public EmbarcacionRestController(EmbarcacionRepository embarcacionRepository, PatronRepository patronRepository) {
        this.embarcacionRepository = embarcacionRepository;
        this.patronRepository = patronRepository;
    }

    
    // 1. Obtener la lista completa de embarcaciones.
    // /api/embarcaciones (GET)

    @GetMapping
    public ResponseEntity<List<Embarcacion>> listarTodasLasEmbarcaciones() {
        // Usamos el método findAll() 
        List<Embarcacion> embarcaciones = embarcacionRepository.findAll();

        if (embarcaciones.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 si no hay barcos
        }

        return new ResponseEntity<>(embarcaciones, HttpStatus.OK); // 200 OK con la lista
    }
    
    // 2. Obtener la lista de embarcaciones según el tipo de embarcación.
     // /api/embarcaciones/tipo/{tipo}  (Ejemplo: /api/embarcaciones/tipo/VELERO) (GET)

    // Cambio el nombre de la variable a nombreTipo para que sea más descriptiva. Aplicando la regla de nombrado 10.

    //// Decisión de diseño: Se eliminan los comentarios línea a línea por ser redundantes; 
    // el código es autoexplicativo (Regla 10 de comentarios).
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Embarcacion>> listarEmbarcacionesPorTipo(@PathVariable("tipo") String nombreTipo) {
        try {
            
            TipoEmbarcacion tipo = TipoEmbarcacion.valueOf(nombreTipo.toUpperCase());
            
            List<Embarcacion> embarcaciones = embarcacionRepository.findByTipo(tipo);

            if (embarcaciones.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
            }

            return new ResponseEntity<>(embarcaciones, HttpStatus.OK); 
            
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
     
    @PostMapping
    public ResponseEntity<String> crearEmbarcacion(@RequestBody Embarcacion embarcacion) {
        
        // comprobar que trae matrícula
        if (embarcacion.getMatricula() == null || embarcacion.getMatricula().isEmpty()) {
             return new ResponseEntity<>("La matrícula es obligatoria", HttpStatus.BAD_REQUEST);
        }

        boolean guardado = embarcacionRepository.save(embarcacion);

        if (guardado) {
            return new ResponseEntity<>("Embarcación creada correctamente", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Error al crear la embarcación. Puede que la matrícula ya exista.", HttpStatus.BAD_REQUEST);
        }
    }
    
    // 3. Obtener las embarcaciones disponibles dada una fecha de inicio y de fin (GET)
    // URL ejemplo: /api/embarcaciones/disponibles?fechaInicio=2025-11-01&fechaFin=2025-11-05
     
    @GetMapping("/disponibles")
    public ResponseEntity<List<Embarcacion>> getDisponibles(
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) { 
        // Validación básica de fechas
        if (fechaInicio.isAfter(fechaFin)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
        }

        List<Embarcacion> disponibles = embarcacionRepository.findDisponiblesParaApi(fechaInicio, fechaFin);

        if (disponibles.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        }
        return new ResponseEntity<>(disponibles, HttpStatus.OK);
    }

    // 5. Actualizar los campos de información de una embarcación, excepto la matrícula (PATCH)
    // URL: /api/embarcaciones/{matricula}
    
    //[Reglas 3 y 6: Comentarios sobre condicionales y de intención]
    @PatchMapping("/{matricula}")
    public ResponseEntity<Embarcacion> updateEmbarcacion(@PathVariable String matricula, @RequestBody Embarcacion embarcacionActualizada) {
        
        
        Embarcacion embarcacionActual = embarcacionRepository.findByMatricula(matricula);
        if (embarcacionActual == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
      
        if (embarcacionActualizada.getNombre() != null) {
            embarcacionActual.setNombre(embarcacionActualizada.getNombre());
        }
       
        if (embarcacionActualizada.getTipo() != null) {
            embarcacionActual.setTipo(embarcacionActualizada.getTipo());
        }
      
        if (embarcacionActualizada.getPlazas() > 0) {
            embarcacionActual.setPlazas(embarcacionActualizada.getPlazas());
        }
        
       
        if (embarcacionActualizada.getDimensiones() != null) {
            embarcacionActual.setDimensiones(embarcacionActualizada.getDimensiones());
        }

        
        int filasAfectadas = embarcacionRepository.update(embarcacionActual);
        if (filasAfectadas > 0) {
            return new ResponseEntity<>(embarcacionActual, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //3. Vincular un patrón a una embarcación (PATCH)
    @PatchMapping("/{matricula}/patron")
    public ResponseEntity<?> vincularPatron(@PathVariable String matricula, @RequestBody Patron patron) {
        
        // 1. Validar que nos envían un DNI
        String dniPatron = patron.getDni();
        if (dniPatron == null || dniPatron.isEmpty()) {
            return ResponseEntity.badRequest().body("El DNI del patrón es obligatorio");
        } 
        // 2. Comprobar que la embarcación existe
        if (embarcacionRepository.findByMatricula(matricula) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe embarcación con matrícula: " + matricula);
        }
        // 3. Comprobar que el patrón existe
        if (patronRepository.findByDni(dniPatron) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe ningún patrón con DNI: " + dniPatron);
        }
        // 4. Vincular
        boolean exito = embarcacionRepository.asociarPatron(matricula, dniPatron);
        if (exito) {
            return ResponseEntity.ok("Patrón " + dniPatron + " vinculado correctamente a la embarcación " + matricula);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al vincular el patrón");
        }
    }
    
    //4. Desvincular un patrón de una embarcación (PATCH)
    //URL: /api/embarcaciones/{matricula}/patron/desvincular
    @PatchMapping("/{matricula}/patron/desvincular")
    public ResponseEntity<?> desvincularPatron(@PathVariable String matricula) {
        
        // 1. Comprobar que la embarcación existe
        Embarcacion embarcacion = embarcacionRepository.findByMatricula(matricula);
        if (embarcacion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe embarcación con matrícula: " + matricula);
        }
        // 2. Desvincular (Pasamos null al método que ya existe)
        boolean exito = embarcacionRepository.asociarPatron(matricula, null);
        if (exito) {
            return ResponseEntity.ok("Patrón desvinculado correctamente de la embarcación " + matricula);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al desvincular el patrón");
        }
    }
    
    // 5. Eliminar una embarcación si no está vinculada a ningún alquiler o reserva (DELETE)
    // URL: /api/embarcaciones/{matricula}
    @DeleteMapping("/{matricula}")
    public ResponseEntity<String> borrarEmbarcacion(@PathVariable String matricula) {
        
        // 1. Comprobar que la embarcación existe
        if (embarcacionRepository.findByMatricula(matricula) == null) {
            return new ResponseEntity<>("No existe la embarcación con matrícula: " + matricula, HttpStatus.NOT_FOUND);
        }

        // 2. Comprobar restricciones (Que no tenga historial)
        if (embarcacionRepository.tieneHistorial(matricula)) {
            return new ResponseEntity<>("No se puede eliminar: La embarcación tiene alquileres o reservas asociadas.", HttpStatus.CONFLICT); // 409 Conflict
        }

        // 3. Proceder al borrado
        boolean borrado = embarcacionRepository.delete(matricula);

        if (borrado) {
            return new ResponseEntity<>("Embarcación eliminada correctamente.", HttpStatus.OK); // O HttpStatus.NO_CONTENT
        } else {
            return new ResponseEntity<>("Error interno al intentar borrar la embarcación.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
