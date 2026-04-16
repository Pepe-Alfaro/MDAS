package es.uco.pw.grupo12.api;

import es.uco.pw.grupo12.model.domain.socio.Socio;
import es.uco.pw.grupo12.model.repository.SocioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;

import java.util.List;

@RestController
@RequestMapping("/api/socios")
public class SocioRestController {

    private final SocioRepository socioRepository;

    // Inyección del repositorio por constructor
    public SocioRestController(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
    }

    /**
     * 1. Obtener la lista completa de socios, sean titulares o no.
     * Método HTTP: GET
     * URL: /api/socios
     */
    @GetMapping
    public ResponseEntity<List<Socio>> listarTodosLosSocios() {
        // Llamamos al método de tu repositorio que ya recuperaba TODOS los socios 
        // (incluyendo la comprobación de si son titulares o no)
        List<Socio> socios = socioRepository.findAllSociosWithTitularStatus();

        // Si la lista está vacía, devolvemos un 204 No Content
        if (socios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        
        // Si hay datos, devolvemos la lista y un 200 OK
        return new ResponseEntity<>(socios, HttpStatus.OK);
    }

    /**
     * 2. Obtener la información de un socio, sea titular o no, dado su DNI.
     * Método HTTP: GET
     * URL: /api/socios/{dni}
     */
    @GetMapping("/{dni}")
    public ResponseEntity<Socio> getSocioByDni(@PathVariable String dni) {
        // Funcion reutilizada
        Socio socio = socioRepository.findSocioByDni(dni);

        // Si el socio existe, devolvemos 200 OK y el objeto
        if (socio != null) {
            return new ResponseEntity<>(socio, HttpStatus.OK);
        } 
        
        // Si no existe (es null), devolvemos 404 Not Found
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //3.Crear socio (POST)
    // URL:api/socios/inscripcion/5
    @PostMapping
    public ResponseEntity<String> crearSocio(@RequestBody Socio socio) {
        // Por defecto, el constructor de Socio pone idInscripcionFk a -1.
        // El repositorio convierte ese -1 a NULL en la BBDD, cumpliendo el requisito.
        
        // Intentamos guardar el socio

        //cambio el nombre de variable guardado por isExitoRegistro. Aplicando al regla de nombrado 1.
        boolean isExitoRegistro = socioRepository.saveSocio(socio);

        if (isExitoRegistro) {
            return new ResponseEntity<>("Socio creado correctamente", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("No se pudo crear el socio. Verifique que el DNI no exista ya.", HttpStatus.BAD_REQUEST);
        }
    }
    //4. Crear un nuevo socio asociándolo a una inscripción familiar ya existente.
     
    @PostMapping("/inscripcion/{idInscripcion}")
    public ResponseEntity<String> crearSocioFamiliar(
            @PathVariable("idInscripcion") int idInscripcion, 
            @RequestBody Socio socio) {
        
        // 1. Asignamos el ID de la inscripción que nos pasan por la URL al objeto Socio
        socio.setIdInscripcionFk(idInscripcion);
        
        // 2. Por lógica de negocio, si se añade a una inscripción existente, no suele ser el titular
        socio.setEsTitular(false); 

        // 3. Guardamos el socio. 
        // Si el ID de inscripción no existe en la BBDD, saltará un error de Clave Foránea (FK)
        // y saveSocio devolverá false o lanzará excepción (dependiendo de tu implementación de Repository).


        //cambio el nombre de variable guardado por isExitoRegistro. Aplicando al regla de nombrado 1.
        boolean isExitoRegistro = socioRepository.saveSocio(socio);

        if (isExitoRegistro) {
            return new ResponseEntity<>("Socio familiar creado y vinculado a la inscripción " + idInscripcion, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("No se pudo crear el socio. Verifique que el DNI no exista ya y que el ID de inscripción sea correcto.", HttpStatus.BAD_REQUEST);
        }
    }
    //(API) 1. Actualizar los campos de información de un socio, excepto el DNI (PATCH).
     
    @PatchMapping("/{dni}")
    public ResponseEntity<?> actualizarSocio(@PathVariable("dni") String dni, @RequestBody Socio socioUpdates) {
        
        // 1. Buscar el socio existente
        Socio socioExistente = socioRepository.findSocioByDni(dni);
        
        if (socioExistente == null) {
            return new ResponseEntity<>("No existe ningún socio con el DNI " + dni, HttpStatus.NOT_FOUND);
        }

        // 2. Aplicar cambios 
        
        if (socioUpdates.getNombre() != null && !socioUpdates.getNombre().isEmpty()) {
            socioExistente.setNombre(socioUpdates.getNombre());
        }
        if (socioUpdates.getApellidos() != null && !socioUpdates.getApellidos().isEmpty()) {
            socioExistente.setApellidos(socioUpdates.getApellidos());
        }
        if (socioUpdates.getFechaNacimiento() != null) {
            socioExistente.setFechaNacimiento(socioUpdates.getFechaNacimiento());
        }
        if (socioUpdates.getDireccion() != null && !socioUpdates.getDireccion().isEmpty()) {
            socioExistente.setDireccion(socioUpdates.getDireccion());
        }
        if (socioUpdates.getFechaInscripcion() != null) {
            socioExistente.setFechaInscripcion(socioUpdates.getFechaInscripcion());
        }
        
        
        if (socioUpdates.getTituloPatron() != socioExistente.getTituloPatron()) {
             
             if(socioUpdates.getTituloPatron()) {
                 socioExistente.setTituloPatron(true);
             }
             
        }

        // 3. Guardar los cambios
        boolean actualizado = socioRepository.updateSocio(socioExistente);

        if (actualizado) {
            return new ResponseEntity<>("Socio actualizado correctamente", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Error al actualizar el socio", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
     //Eliminar a un socio si no está vinculado a ninguna inscripción (DELETE).
     // URL: /api/socios/{dni}
     
    @org.springframework.web.bind.annotation.DeleteMapping("/{dni}")
    public ResponseEntity<?> eliminarSocio(@PathVariable("dni") String dni) {
        
        // 1. Buscar el socio para ver si existe y comprobar su inscripción
        Socio socio = socioRepository.findSocioByDni(dni);

        if (socio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe ningún socio con DNI " + dni);
        }

        // 2. Verificar si está vinculado a una inscripción (Sin inscripcion ID = -1)
      
        if (socio.getIdInscripcionFk() != -1 && socio.getIdInscripcionFk() != 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar al socio " + dni + " porque está vinculado a la inscripción ID: " + socio.getIdInscripcionFk());
        }

        // 3. Ejecutar el borrado
        boolean borrado = socioRepository.deleteSocio(dni);

        if (borrado) {
            return ResponseEntity.ok("Socio " + dni + " eliminado correctamente del sistema.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al intentar eliminar el socio.");
        }
    }
}