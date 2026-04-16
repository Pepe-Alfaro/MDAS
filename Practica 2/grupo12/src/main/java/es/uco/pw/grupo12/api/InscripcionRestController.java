package es.uco.pw.grupo12.api;

import es.uco.pw.grupo12.model.domain.inscripcion.Inscripcion;
import es.uco.pw.grupo12.model.domain.inscripcion.TipoInscripcion;
import es.uco.pw.grupo12.model.domain.socio.Socio;
import es.uco.pw.grupo12.model.repository.InscripcionRepository;
import es.uco.pw.grupo12.model.repository.SocioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionRestController {

    private final InscripcionRepository inscripcionRepository;
    private final SocioRepository socioRepository;

    public InscripcionRestController(InscripcionRepository inscripcionRepository, SocioRepository socioRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.socioRepository = socioRepository;
    }

    // 1. Obtener la lista de inscripciones individuales (GET)
    @GetMapping("/individuales")
    public ResponseEntity<List<Inscripcion>> getInscripcionesIndividuales() {
        List<Inscripcion> inscripciones = inscripcionRepository.findInscripcionesByTipo(TipoInscripcion.INDIVIDUAL);

        if (inscripciones.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(inscripciones, HttpStatus.OK);
    }

    // 2. Obtener la lista de inscripciones familiares (GET)
    @GetMapping("/familiares")
    public ResponseEntity<List<Inscripcion>> getInscripcionesFamiliares() {
        List<Inscripcion> inscripciones = inscripcionRepository.findInscripcionesByTipo(TipoInscripcion.FAMILIAR);

        if (inscripciones.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(inscripciones, HttpStatus.OK);
    }

    // 3. Obtener la información de una inscripción dado el DNI del socio titular (GET)
    @GetMapping("/titular/{dni}")
    public ResponseEntity<Inscripcion> getInscripcionByTitular(@PathVariable String dni) {
        Inscripcion inscripcion = inscripcionRepository.findInscripcionByTitularDni(dni);

        if (inscripcion != null) {
            return new ResponseEntity<>(inscripcion, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    // 4. Crear una inscripción para un socio titular (POST)
    @PostMapping
    public ResponseEntity<?> crearInscripcion(@RequestBody Inscripcion nuevaInscripcion) {
        
        if (nuevaInscripcion.getSocioTitular() == null || nuevaInscripcion.getSocioTitular().getDni() == null) {
            return ResponseEntity.badRequest().body("El DNI del socio titular es obligatorio");
        }

        String dniTitular = nuevaInscripcion.getSocioTitular().getDni();
        Socio socio = socioRepository.findSocioByDni(dniTitular);
        
        if (socio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe ningún socio con DNI: " + dniTitular);
        }

        if (socio.getIdInscripcionFk() != -1 && socio.getIdInscripcionFk() != 0) {
             return ResponseEntity.status(HttpStatus.CONFLICT).body("El socio ya tiene una inscripción asignada");
        }

        int idNuevaInscripcion = inscripcionRepository.saveInscripcionIndividual(socio);

        if (idNuevaInscripcion > 0) {
            boolean vinculado = socioRepository.updateInscripcionFk(dniTitular, idNuevaInscripcion);
            
            if (vinculado) {
                nuevaInscripcion.setIdInscripcion(idNuevaInscripcion);
                return new ResponseEntity<>(nuevaInscripcion, HttpStatus.CREATED);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Inscripción creada pero falló la vinculación con el socio");
            }
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar la inscripción");
        }
    }

    // 2. Actualizar una inscripción individual para convertirla en una familiar (PUT).

    //Se modifica la variable inscripcionDatos por inscripcionAModificar para evitar palabras generales. Aplicando la regla de nombrado 12
    @PutMapping("/{id}/familiar")
        public ResponseEntity<?> convertirAFamiliar(@PathVariable("id") int id, @RequestBody Inscripcion inscripcionAModificar) {
        
        if (inscripcionAModificar.getCuota() <= 0) {
            return ResponseEntity.badRequest().body("Debe especificar una cuota válida mayor que 0.");
        }

        boolean actualizado = inscripcionRepository.updateInscripcionAFamiliar(id, inscripcionAModificar.getCuota());

        if (actualizado) {
            return ResponseEntity.ok("Inscripción " + id + " convertida a FAMILIAR correctamente. Nueva cuota: " + inscripcionAModificar.getCuota());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se pudo actualizar. Verifique que el ID de inscripción exista.");
        }
    }
 
    // 3. Vincular a un nuevo miembro en una inscripción familiar (PATCH).
    @org.springframework.web.bind.annotation.PatchMapping("/{id}/vincular")

    // Se modifica la variable socioDatos por socioAVincular para evitar palabras generales. Aplicando la regla de nombrado 12
    public ResponseEntity<?> vincularMiembro(@PathVariable("id") int idInscripcion, @RequestBody Socio socioAVincular) {
        
        String dniNuevoMiembro = socioAVincular.getDni();
        
        if (dniNuevoMiembro == null || dniNuevoMiembro.isEmpty()) {
            return ResponseEntity.badRequest().body("El DNI del socio es obligatorio.");
        }

        Inscripcion inscripcion = inscripcionRepository.findById(idInscripcion);
        if (inscripcion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La inscripción no existe.");
        }
        if (inscripcion.getTipo() != TipoInscripcion.FAMILIAR) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se pueden añadir miembros a una inscripción INDIVIDUAL. Conviértala a FAMILIAR primero.");
        }

        Socio socio = socioRepository.findSocioByDni(dniNuevoMiembro);
        if (socio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El socio con DNI " + dniNuevoMiembro + " no existe.");
        }

        if (socio.getIdInscripcionFk() != -1 && socio.getIdInscripcionFk() != 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El socio ya pertenece a otra inscripción (ID: " + socio.getIdInscripcionFk() + ").");
        }

        // VINCULAR
        boolean exito = socioRepository.updateInscripcionFk(dniNuevoMiembro, idInscripcion);

        if (exito) {
            // --- NUEVO: RECALCULAR CUOTA AUTOMÁTICAMENTE ---
            double nuevaCuota = recalcularYGuardarCuota(idInscripcion);
            
            return ResponseEntity.ok("Socio " + dniNuevoMiembro + " vinculado correctamente a la inscripción " + idInscripcion + ". Cuota actualizada a: " + nuevaCuota + "€");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al vincular el socio.");
        }
    }
    
     // 4. Desvincular a un miembro de una inscripción familiar (PATCH).
    @org.springframework.web.bind.annotation.PatchMapping("/{id}/desvincular")
    public ResponseEntity<?> desvincularMiembro(@PathVariable("id") int idInscripcion, @RequestBody Socio socioAVincular) {
        
        String dniSocio = socioAVincular.getDni();
        
        if (dniSocio == null || dniSocio.isEmpty()) {
            return ResponseEntity.badRequest().body("El DNI del socio es obligatorio.");
        }
        
        Inscripcion inscripcion = inscripcionRepository.findById(idInscripcion);
        if (inscripcion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La inscripción " + idInscripcion + " no existe.");
        }
        
        if (inscripcion.getSocioTitular().getDni().equalsIgnoreCase(dniSocio)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede desvincular al socio titular. Debe cancelar la inscripción completa.");
        }
        
        Socio socio = socioRepository.findSocioByDni(dniSocio);
        if (socio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El socio no existe.");
        }
        
        if (socio.getIdInscripcionFk() != idInscripcion) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El socio no pertenece a la inscripción " + idInscripcion);
        }
        
        // EJECUTAR DESVINCULACIÓN
        boolean exito = socioRepository.desvincularSocio(dniSocio);

        if (exito) {
            // --- NUEVO: RECALCULAR CUOTA AUTOMÁTICAMENTE ---
            double nuevaCuota = recalcularYGuardarCuota(idInscripcion);

            return ResponseEntity.ok("Socio " + dniSocio + " desvinculado correctamente de la inscripción " + idInscripcion + ". Cuota actualizada a: " + nuevaCuota + "€");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al desvincular el socio.");
        }
    }
    
    // 3. Cancelar una inscripción individual o familiar (DELETE).
    @org.springframework.web.bind.annotation.DeleteMapping("/titular/{dni}")
    public ResponseEntity<?> cancelarInscripcion(@PathVariable("dni") String dniTitular) {
        
        Inscripcion inscripcion = inscripcionRepository.findInscripcionByTitularDni(dniTitular);
        
        if (inscripcion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe ninguna inscripción asociada al titular " + dniTitular);
        }

        int idInscripcion = inscripcion.getIdInscripcion();

        // Desvincular a los socios primero
        boolean desvinculados = socioRepository.desvincularTodosDeInscripcion(idInscripcion);
        
        if (!desvinculados) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al desvincular a los socios. No se ha borrado la inscripción.");
        }

        // Borrar la inscripción
        boolean borrado = inscripcionRepository.deleteInscripcion(idInscripcion);

        if (borrado) {
            return ResponseEntity.ok("Inscripción cancelada correctamente. Los socios asociados siguen existiendo en el sistema pero sin inscripción.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al borrar la inscripción de la base de datos.");
        }
    }

    /**
     * Método auxiliar privado para recalcular y actualizar la cuota en la BBDD.
     * Utiliza la lógica de negocio de la clase Inscripcion y el repositorio.
     */
    private double recalcularYGuardarCuota(int idInscripcion) {
        // 1. Recuperamos a TODOS los socios que quedan en la inscripción (familiares)
        List<Socio> sociosActuales = socioRepository.findAllByInscripcionId(idInscripcion);
        
        // 2. Usamos tu clase de dominio para calcular el precio
        Inscripcion inscripcionTemp = new Inscripcion();
        // Nota: En tu modelo 'Inscripcion.calcularCuota' itera sobre 'sociosVinculados'.
        // Al pasarle la lista de la BD, recalculamos el precio correcto según adultos/niños.
        inscripcionTemp.setSociosVinculados(sociosActuales);
        inscripcionTemp.calcularCuota(); 
        
        double nuevaCuota = inscripcionTemp.getCuota();
        
        // 3. Guardamos el nuevo precio en la BBDD usando el método existente
        inscripcionRepository.updateInscripcionAFamiliar(idInscripcion, nuevaCuota);
        
        return nuevaCuota;
    }
}