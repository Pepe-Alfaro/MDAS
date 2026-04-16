package es.uco.pw.grupo12.api;

import es.uco.pw.grupo12.model.domain.embarcacion.Embarcacion;
import es.uco.pw.grupo12.model.domain.reserva.Reserva;
import es.uco.pw.grupo12.model.domain.socio.Socio;
import es.uco.pw.grupo12.model.repository.EmbarcacionRepository;
import es.uco.pw.grupo12.model.repository.ReservaRepository;
import es.uco.pw.grupo12.model.repository.SocioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaRestController {

    private final ReservaRepository reservaRepository;
    private final SocioRepository socioRepository;
    private final EmbarcacionRepository embarcacionRepository;

    public ReservaRestController(ReservaRepository reservaRepository,
                                 SocioRepository socioRepository,
                                 EmbarcacionRepository embarcacionRepository) {
        this.reservaRepository = reservaRepository;
        this.socioRepository = socioRepository;
        this.embarcacionRepository = embarcacionRepository;
    }

    // 1. Obtener la lista completa de reservas (GET)
    @GetMapping
    public ResponseEntity<List<Reserva>> listarReservas() {
        List<Reserva> reservas = reservaRepository.findAll();

        if (reservas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }

    // 2. Obtener la lista de reservas futuras dada una fecha (GET)
    @GetMapping("/futuras/{fecha}")
    public ResponseEntity<List<Reserva>> listarReservasFuturas(@PathVariable("fecha") LocalDate fecha) {
        List<Reserva> reservas = reservaRepository.findByFechaPosterior(fecha);

        if (reservas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }

    // 3. Obtener la información concreta de una reserva dado su identificador (GET)
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtenerReservaPorId(@PathVariable("id") int id) {
        Reserva reserva = reservaRepository.findById(id);

        if (reserva == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(reserva, HttpStatus.OK);
    }

    // 4. Crear una nueva reserva (POST)
    @PostMapping
    public ResponseEntity<?> crearReserva(@RequestBody Reserva reserva) {

        // 1. Validar datos
        if (reserva.getSocioSolicitante() == null || reserva.getSocioSolicitante().getDni() == null ||
            reserva.getEmbarcacion() == null || reserva.getEmbarcacion().getMatricula() == null ||
            reserva.getFechaActividad() == null) {
            return new ResponseEntity<>("Faltan datos (DNI, Matrícula o Fecha)", HttpStatus.BAD_REQUEST);
        }

        String matricula = reserva.getEmbarcacion().getMatricula();
        LocalDate fecha = reserva.getFechaActividad();

        // 2. Recuperar entidades completas
        Socio socio = socioRepository.findSocioByDni(reserva.getSocioSolicitante().getDni());
        Embarcacion barco = embarcacionRepository.findByMatricula(matricula);

        if (socio == null || barco == null) {
            return new ResponseEntity<>("Socio o Embarcación no encontrados", HttpStatus.BAD_REQUEST);
        }

        // Asignamos los objetos completos a la reserva
        reserva.setSocioSolicitante(socio);
        reserva.setEmbarcacion(barco);

        // 3. Validaciones

        // Validar Patrón
        if (barco.getPatron() == null || "-1".equals(barco.getPatron().getDni())) {
            return new ResponseEntity<>("La embarcación no tiene patrón asignado", HttpStatus.CONFLICT);
        }

        // Validar Disponibilidad
        if (embarcacionRepository.checkDisponibilidadReserva(matricula, fecha, fecha) ||
            embarcacionRepository.checkDisponibilidadAlquiler(matricula, fecha, fecha)) {
            return new ResponseEntity<>("La embarcación no está disponible en esa fecha", HttpStatus.CONFLICT);
        }

        // Validar Capacidad
        int plazasDisponibles = barco.getPlazas() - 1; // Restamos al patrón
        if (reserva.getPlazasSolicitadas() > plazasDisponibles) {
            return new ResponseEntity<>("Excede la capacidad máxima (" + plazasDisponibles + " plazas)", HttpStatus.BAD_REQUEST);
        }

        // Calcular Precio (Automático)
        reserva.setPlazasSolicitadas(reserva.getPlazasSolicitadas());

        // 4. Guardar
        if (reservaRepository.saveReserva(reserva)) {
            return new ResponseEntity<>("Reserva creada. Precio: " + reserva.getPrecioTotal() + "€", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Error al guardar la reserva", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // -------------------------------------------------------------------------
    // 1. Modificar fecha a una posterior (PATCH)
    // -------------------------------------------------------------------------
    @PatchMapping("/{id}/fecha")
    public ResponseEntity<?> cambiarFecha(
            @PathVariable("id") int idReserva,
            @RequestParam("nuevaFecha") String nuevaFechaStr) {

        // Parseamos la fecha que llega como String
        LocalDate nuevaFecha;
        try {
            nuevaFecha = LocalDate.parse(nuevaFechaStr);
        } catch (Exception e) {
            return new ResponseEntity<>("Formato de fecha inválido. Use AAAA-MM-DD", HttpStatus.BAD_REQUEST);
        }

        Reserva reserva = reservaRepository.findById(idReserva);
        if (reserva == null) {
            return new ResponseEntity<>("Reserva no encontrada", HttpStatus.NOT_FOUND);
        }

        // 1. Validar que la reserva original es FUTURA
        if (!reserva.getFechaActividad().isAfter(LocalDate.now())) {
            return new ResponseEntity<>("No se puede modificar una reserva que ya ha pasado o es hoy.", HttpStatus.BAD_REQUEST);
        }

        // 2. Validar que la NUEVA fecha es futura
        if (!nuevaFecha.isAfter(LocalDate.now())) {
            return new ResponseEntity<>("La nueva fecha debe ser posterior a hoy.", HttpStatus.BAD_REQUEST);
        }

        // 3. Validar que la nueva fecha es POSTERIOR a la antigua
        if (!nuevaFecha.isAfter(reserva.getFechaActividad())) {
             return new ResponseEntity<>("La nueva fecha debe ser posterior a la fecha original.", HttpStatus.BAD_REQUEST);
        }

        String matricula = reserva.getEmbarcacion().getMatricula();

        // 4. Validar Disponibilidad COMPLETA
        boolean ocupadaPorReserva = embarcacionRepository.checkDisponibilidadReserva(matricula, nuevaFecha, nuevaFecha);
        boolean ocupadaPorAlquiler = embarcacionRepository.checkDisponibilidadAlquiler(matricula, nuevaFecha, nuevaFecha);

        if (ocupadaPorReserva || ocupadaPorAlquiler) {
            return new ResponseEntity<>("La embarcación no está disponible en la nueva fecha solicitada.", HttpStatus.CONFLICT);
        }

        // 5. Realizar actualización
        if (reservaRepository.updateFechaReserva(idReserva, nuevaFecha)) {
            return new ResponseEntity<>("Fecha actualizada correctamente.", HttpStatus.OK);
        }

        return new ResponseEntity<>("Error interno al actualizar la fecha.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 2. Modificar datos: descripción y plazas (PATCH)
    @PatchMapping("/{id}/datos")
    public ResponseEntity<?> modificarDatos(
            @PathVariable("id") int idReserva,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("plazas") int plazas) {

        Reserva reserva = reservaRepository.findById(idReserva);
        if (reserva == null) {
            return new ResponseEntity<>("Reserva no encontrada", HttpStatus.NOT_FOUND);
        }

        // 1. Validar que la reserva es futura
        if (!reserva.getFechaActividad().isAfter(LocalDate.now())) {
            return new ResponseEntity<>("No se pueden modificar los datos de una reserva pasada.", HttpStatus.BAD_REQUEST);
        }

        // 2. Recuperar la embarcación para ver su capacidad real
        Embarcacion barco = embarcacionRepository.findByMatricula(reserva.getEmbarcacion().getMatricula());
        if (barco == null) {
            return new ResponseEntity<>("Error: La embarcación asociada no existe.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 3. Validar capacidad (restando al patrón)
        int maxPlazas = barco.getPlazas() - 1;

        if (plazas <= 0) {
             return new ResponseEntity<>("El número de plazas debe ser mayor que 0.", HttpStatus.BAD_REQUEST);
        }
        if (plazas > maxPlazas) {
            return new ResponseEntity<>("Las plazas solicitadas (" + plazas + ") superan el máximo permitido (" + maxPlazas + ") para esta embarcación.", HttpStatus.BAD_REQUEST);
        }

        // 4. Calcular NUEVO PRECIO
        double nuevoPrecio = plazas * 40.0;

        // 5. Actualizar en BD
        if (reservaRepository.updateDatosReserva(idReserva, descripcion, plazas, nuevoPrecio)) {
            return new ResponseEntity<>("Datos actualizados correctamente. Nuevo precio: " + nuevoPrecio + "€", HttpStatus.OK);
        }

        return new ResponseEntity<>("Error al actualizar los datos.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 3. Cancelar reserva futura (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelarReserva(@PathVariable("id") int idReserva) {

        Reserva reserva = reservaRepository.findById(idReserva);

        if (reserva == null) {
            return new ResponseEntity<>("Reserva no encontrada.", HttpStatus.NOT_FOUND);
        }

        // 1. Validar que la actividad NO se haya realizado (Fecha futura)
        if (!reserva.getFechaActividad().isAfter(LocalDate.now())) {
            return new ResponseEntity<>("No se puede cancelar una reserva pasada o que es hoy.", HttpStatus.CONFLICT);
        }

        // 2. Borrar
        if (reservaRepository.deleteReserva(idReserva)) {
            return new ResponseEntity<>("Reserva cancelada correctamente.", HttpStatus.OK);
        }

        return new ResponseEntity<>("Error interno al cancelar la reserva.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}