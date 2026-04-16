package es.uco.pw.grupo12.api;

import es.uco.pw.grupo12.model.domain.alquiler.Alquiler;
import es.uco.pw.grupo12.model.repository.AlquilerRepository;
import es.uco.pw.grupo12.model.repository.EmbarcacionRepository;

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
@RequestMapping("/api/alquileres")
public class AlquilerRestController {

    private final AlquilerRepository alquilerRepository;
    private final EmbarcacionRepository embarcacionRepository; 
    

    public AlquilerRestController(AlquilerRepository alquilerRepository, EmbarcacionRepository embarcacionRepository) {
        this.alquilerRepository = alquilerRepository;
        this.embarcacionRepository = embarcacionRepository;
    }

    
    // 1. Obtener la lista completa de alquileres (GET)
    // URL: /api/alquileres
    @GetMapping
    public ResponseEntity<List<Alquiler>> getAllAlquileres() {
        List<Alquiler> alquileres = alquilerRepository.findAll();

        if (alquileres.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        
        return new ResponseEntity<>(alquileres, HttpStatus.OK);
    }
    
    //2. Obtener la lista de alquileres futuros dada una fecha (GET)
    // URL: /api/alquileres/futuros?fecha=2025-10-01

    @GetMapping("/futuros")
    public ResponseEntity<List<Alquiler>> getAlquileresFuturos(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        List<Alquiler> alquileres = alquilerRepository.findFuturosAlquileresParametro(fecha);

        if (alquileres.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        
        return new ResponseEntity<>(alquileres, HttpStatus.OK);
    }
    
    // 3. Obtener la información concreta de un alquiler dado su identificador (GET)
    // URL: /api/alquileres/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Alquiler> getAlquilerById(@PathVariable int id) {
        
        Alquiler alquiler = alquilerRepository.findAlquilerById(id);

        if (alquiler != null) {
            return new ResponseEntity<>(alquiler, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 5. Crear un alquiler para una embarcación disponible (POST)
    @PostMapping
    public ResponseEntity<Alquiler> crearAlquiler(@RequestBody Alquiler alquiler) {
        ResponseEntity<Alquiler> response;

        // 1. Extraemos datos clave
        String matricula = alquiler.getEmbarcacion().getMatricula();
        LocalDate inicio = alquiler.getFechaInicio();
        LocalDate fin = alquiler.getFechaFin();

        // 2. Comprobamos disponibilidad
        boolean ocupado = embarcacionRepository.checkDisponibilidadAlquiler(matricula, inicio, fin) 
                       || embarcacionRepository.checkDisponibilidadReserva(matricula, inicio, fin);

        if (ocupado) {
            // Si está ocupado, devolvemos conflicto (409)
            response = new ResponseEntity<>(HttpStatus.CONFLICT);
        } else {
            // 3. Intentamos guardar directamente
            // Si el socio o el barco no existen, fallará la FK en la base de datos y devolverá -1
            int id = alquilerRepository.save(alquiler);
            if (id > 0) {
                alquiler.setIdAlquiler(id);
                response = new ResponseEntity<>(alquiler, HttpStatus.CREATED);
            } else {
                // Falló al guardar (ID de socio o barco incorrectos)
                response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        return response;
    }
    
    // 1. Vincular a un nuevo socio (pasajero) a un alquiler futuro (PATCH)
    @PatchMapping("/{id}/addPasajero")
public ResponseEntity<String> addPasajero(
        @PathVariable int id,
        @RequestParam String dni) {

    // 1. Limpieza del DNI
    if (dni == null || dni.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("El DNI es obligatorio.");
    }
    String dniLimpio = dni.trim().toUpperCase();
    // 2. Buscar el alquiler
    Alquiler alquiler = alquilerRepository.findAlquilerById(id);
    if (alquiler == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El alquiler no existe.");
    }
    // 3. Validar estado del alquiler (Fecha)
    if (!alquiler.getFechaInicio().isAfter(LocalDate.now())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se puede modificar un alquiler pasado o en curso.");
    }
    // Comprobar plazas
    int maxPlazas = alquiler.getEmbarcacion().getPlazas(); 
    List<String> pasajerosActuales = alquilerRepository.findPasajerosByAlquiler(id);
    int ocupacionActual = pasajerosActuales.size();
    if (ocupacionActual + 1 >= maxPlazas) { 
        return ResponseEntity.status(HttpStatus.CONFLICT).body("La embarcación ha alcanzado su capacidad máxima.");
    }
    
    // 4. Verificar si ya está añadido
    if (pasajerosActuales.contains(dniLimpio)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El pasajero ya está incluido en este alquiler.");
    }
    // 5. Guardar

    //cambio el nombre de variable guardado por isPasajeroEliminado. Aplicando al regla de nombrado 1.
    boolean isPasajeroVinculado = alquilerRepository.addPasajero(id, dniLimpio);
    if (!isPasajeroVinculado) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo añadir. Verifique que el DNI exista en el sistema.");
    }
        return ResponseEntity.ok("Pasajero añadido correctamente.");
    }

    // 2. Desvincular a un socio de un alquiler futuro (PATCH)
    @PatchMapping("/{id}/removePasajero")
public ResponseEntity<String> removePasajero(
        @PathVariable int id,
        @RequestParam String dni) {

    String dniLimpio = dni.trim().toUpperCase();
    
    Alquiler alquiler = alquilerRepository.findAlquilerById(id);
    if (alquiler == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Alquiler no existe");
    }
    if (!alquiler.getFechaInicio().isAfter(LocalDate.now())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El alquiler ya está en curso o finalizado");
    }
    
    //cambio el nombre de variable guardado por isPasajeroEliminado. Aplicando al regla de nombrado 1.
    boolean isPasajeroEliminado = alquilerRepository.removePasajero(id, dniLimpio);

    if (!isPasajeroEliminado) {
        // Si devuelve false, es probable que el pasajero no estuviera en la lista
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo eliminar: El pasajero no estaba asociado a este alquiler.");
    }

    return ResponseEntity.ok("Pasajero eliminado correctamente");
    }


    // 3. Cancelar un alquiler que aún no se haya realizado (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelarAlquiler(@PathVariable int id) {

        Alquiler alquiler = alquilerRepository.findAlquilerById(id);

        if (alquiler == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Alquiler no encontrado.");
        }
        // Solo futuros
        if (!alquiler.getFechaInicio().isAfter(LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se puede cancelar un alquiler que ya ha iniciado o finalizado.");
        }
        // Ejecutar cancelación
        boolean isAlquilerCancelado = alquilerRepository.cancelarAlquiler(id);
        if (!isAlquilerCancelado) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al intentar cancelar el alquiler.");
        }

        return ResponseEntity.ok("Alquiler cancelado correctamente.");
    }

}