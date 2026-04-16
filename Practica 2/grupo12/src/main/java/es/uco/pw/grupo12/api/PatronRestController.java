package es.uco.pw.grupo12.api;

import es.uco.pw.grupo12.model.domain.patron.Patron;
import es.uco.pw.grupo12.model.repository.PatronRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/patrones") 
public class PatronRestController {

    private final PatronRepository patronRepository;

    public PatronRestController(PatronRepository patronRepository) {
        this.patronRepository = patronRepository;
    }

   
     //3. Obtener la lista completa de patrones.
     ///api/patrones
    
    @GetMapping
    public ResponseEntity<List<Patron>> listarTodosLosPatrones() {
        // Llamamos al método obtenerPatrones() que YA existe en tu repositorio
        List<Patron> patrones = patronRepository.obtenerPatrones(); 

        // Verificamos si la lista es nula o vacía para devolver el código HTTP correcto
        if (patrones == null || patrones.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        }

        return new ResponseEntity<>(patrones, HttpStatus.OK); // 200 OK
    }
    
     //5. Crear un nuevo patrón, sin asociarle embarcación (POST)
     
    @PostMapping
    public ResponseEntity<String> crearPatron(@RequestBody Patron patron) {
        
        
        if (patron.getDni() == null || patron.getDni().isEmpty() || patron.getDni().equals("-1")) {
             return new ResponseEntity<>("El DNI es obligatorio y válido", HttpStatus.BAD_REQUEST);
        }

        boolean guardado = patronRepository.darDeAltaPatron(patron); 

        if (guardado) {
            return new ResponseEntity<>("Patrón creado correctamente", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("No se pudo crear el patrón. Verifique que el DNI no exista ya.", HttpStatus.BAD_REQUEST);
        }
    }
    /**
     * 2. Actualizar los campos de información de un patrón, excepto el DNI (PATCH)
     * URL: /api/patrones/{dni}
     */
    @PatchMapping("/{dni}")
    public ResponseEntity<Patron> updatePatron(@PathVariable String dni, @RequestBody Patron datosNuevos) {
        ResponseEntity<Patron> response;

        // 1. Buscamos el patrón original
        Patron patronActual = patronRepository.findByDni(dni);

        if (patronActual == null) {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            // 2. Actualizamos SOLO lo que nos envíens
            if (datosNuevos.getNombre() != null) {
                patronActual.setNombre(datosNuevos.getNombre());
            }
            if (datosNuevos.getApellidos() != null) {
                patronActual.setApellidos(datosNuevos.getApellidos());
            }
            if (datosNuevos.getFechaNacimiento() != null) {
                patronActual.setFechaNacimiento(datosNuevos.getFechaNacimiento());
            }
            if (datosNuevos.getFechaExpedicionTitulo() != null) {
                patronActual.setFechaExpedicionTitulo(datosNuevos.getFechaExpedicionTitulo());
            }

            // 3. Guardamos los cambios
            int filas = patronRepository.update(patronActual);

            if (filas > 0) {
                response = new ResponseEntity<>(patronActual, HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return response;
    }

    // 6. Eliminar a un patrón si no está vinculado con ninguna embarcación (DELETE)
    // URL: /api/patrones/{dni}
    @DeleteMapping("/{dni}")
    public ResponseEntity<String> borrarPatron(@PathVariable String dni) {
        
        // 1. Comprobar que el patrón existe
        if (patronRepository.findByDni(dni) == null) {
            return new ResponseEntity<>("No existe ningún patrón con DNI: " + dni, HttpStatus.NOT_FOUND);
        }
        // 2. Comprobar restricciones
        if (patronRepository.estaAsignado(dni)) {
            return new ResponseEntity<>("No se puede eliminar: El patrón está vinculado a una o más embarcaciones.", HttpStatus.CONFLICT);
        }
        // 3. Proceder al borrado
        boolean borrado = patronRepository.delete(dni);
        if (borrado) {
            return new ResponseEntity<>("Patrón eliminado correctamente.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Error interno al intentar borrar el patrón.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
