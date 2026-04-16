package es.uco.pw.grupo12;

import java.time.LocalDate;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

// Importamos los modelos necesarios
import es.uco.pw.grupo12.model.domain.socio.Socio;
import es.uco.pw.grupo12.model.domain.embarcacion.Embarcacion;
import es.uco.pw.grupo12.model.domain.inscripcion.Inscripcion;
import es.uco.pw.grupo12.model.domain.patron.Patron;

public class Grupo12ClientApplication_2 {

    private static final String BASE_URL = "http://localhost:8080/api";
    private static RestTemplate rest = new RestTemplate();

    public static void main(String[] args) {
    
        rest.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        System.out.println("INICIANDO PRUEBAS AVANZADAS (PUT, PATCH, DELETE)...");
        System.out.println("==================================================");

        // 1. Pruebas de PUT 
        sendPutRequests();

        System.out.println("\n==================================================");

        // 2. Pruebas de PATCH 
        sendPatchRequests();

        System.out.println("\n==================================================");

        // 3. Pruebas de DELETE 
        sendDeleteRequests();
        
        System.out.println("\n==== FIN DE PRUEBAS ====");
    }

    /**
     * 1. Actualizar una inscripción individual para convertirla en una familiar (PUT)
     */
    private static void sendPutRequests() {
        System.out.println("\n************** PETICIONES PUT **************");
        
        // Asumimos que existe la inscripción con ID 2 (Individual) creada previamente.
        int idInscripcion = 2; 

        // CASO 1: ÉXITO - Convertir a Familiar
        System.out.println("\n==== REQUEST: PUT convertir inscripción a FAMILIAR (Exito) ====");
        Inscripcion datosNuevos = new Inscripcion();
        datosNuevos.setCuota(300.0);

        try {
            rest.put(BASE_URL + "/inscripciones/{id}/familiar", datosNuevos, idInscripcion);
            System.out.println("Solicitud PUT enviada correctamente para ID: " + idInscripcion);
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getResponseBodyAsString());
        }

        // CASO 2: ERROR - Inscripción inexistente
        System.out.println("\n==== REQUEST: PUT convertir inscripción (Error - ID no existe) ====");
        try {
            rest.put(BASE_URL + "/inscripciones/{id}/familiar", datosNuevos, 9999);
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada correctamente: " + e.getStatusCode());
        }
    }

    /**
     * Peticiones PATCH para Socios e Inscripciones
     */
    private static void sendPatchRequests() {
        System.out.println("\n************** PETICIONES PATCH **************");

      
        // Actualizar los campos de información de un socio, excepto el DNI (PATCH)
       
        System.out.println("\n--- [PATCH] 1. ACTUALIZAR DATOS SOCIO ---");
        
        String dniSocio = "89548432F"; 
        
        Socio updates = new Socio();
        updates.setDireccion("Nueva Calle Damasco");
        updates.setNombre("Elías");

        // CASO ÉXITO
        
        System.out.println("\n==== REQUEST: PATCH actualizar socio (Exito) DNI: " + dniSocio + " ====");
        try {
            // patchForObject ejecuta el PATCH y devuelve la respuesta del servidor
            String respuesta = rest.patchForObject(BASE_URL + "/socios/{dni}", updates, String.class, dniSocio);
            System.out.println("Respuesta: " + respuesta);
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getResponseBodyAsString());
        }

        // CASO ERROR (DNI Inexistente)
        System.out.println("\n==== REQUEST: PATCH actualizar socio (Error - No existe) ====");
        try {
            rest.patchForObject(BASE_URL + "/socios/{dni}", updates, String.class, "00000000Z");
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada correctamente: " + e.getStatusCode());
        }

     
        // Vincular a un nuevo miembro en una inscripción familiar (PATCH)
       
        System.out.println("\n--- [PATCH] 3. VINCULAR FAMILIAR ---");
        
        // Primero creamos un socio "suelto" para tener a alguien a quien vincular
        Socio socioParaVincular = new Socio("11122233V", "antonio", "Flores", LocalDate.of(2000,1,1), "Calle", LocalDate.now());
        try { rest.postForEntity(BASE_URL + "/socios", socioParaVincular, String.class); } catch (Exception e) {}

        int idInscripcion = 4; 

        // CASO ÉXITO
        System.out.println("\n==== REQUEST: PATCH vincular socio (Exito) ====");
        try {
            // Enviamos un objeto Socio con solo el DNI en el cuerpo
            Socio body = new Socio();
            body.setDni("54242532J");
            
            String respuesta = rest.patchForObject(BASE_URL + "/inscripciones/{id}/vincular", body, String.class, idInscripcion);
            System.out.println("Respuesta: " + respuesta);
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getResponseBodyAsString());
        }

        // CASO ERROR (ID incorrecto)
        System.out.println("\n==== REQUEST: PATCH vincular socio (Error - ID incorrecto) ====");
        try {
            Socio body = new Socio();
            body.setDni("111222233V");
            rest.patchForObject(BASE_URL + "/inscripciones/9999/vincular", body, String.class);
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada correctamente: " + e.getStatusCode());
        }

       
        // Desvincular a un miembro de una inscripción familiar (PATCH)
       
        System.out.println("\n--- [PATCH] 4. DESVINCULAR FAMILIAR ---");

        // CASO ÉXITO (Desvincular)
        try {
            Socio body = new Socio();
            body.setDni("54242532J");
            
            
            System.out.println("\n==== REQUEST: PATCH desvincular socio (Exito) DNI: " + body.getDni() + " ====");

            String respuesta = rest.patchForObject(BASE_URL + "/inscripciones/{id}/desvincular", body, String.class, 4);
            System.out.println("Respuesta: " + respuesta);
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getResponseBodyAsString());
        }

        // CASO ERROR (Intentar desvincular al Titular - PROHIBIDO)
        System.out.println("\n==== REQUEST: PATCH desvincular titular (Error) ====");
        try {
            Socio bodyTitular = new Socio();
            bodyTitular.setDni("88776655X"); // Asumimos que este es el titular de la inscripción 1
            
            rest.patchForObject(BASE_URL + "/inscripciones/{id}/desvincular", bodyTitular, String.class, idInscripcion);
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada correctamente (Conflict): " + e.getStatusCode());
            System.out.println("Mensaje: " + e.getResponseBodyAsString());
        }

       
        // ACTUALIZAR EMBARCACIÓN
       
        System.out.println("\n--- [PATCH] ACTUALIZAR EMBARCACIÓN ---");

        //  (Exito)
        System.out.println("\n==== REQUEST: PATCH actualizar embarcación (Exito) ====");
        String matricula = "ABCD5"; 
        Embarcacion cambiosBarco = new Embarcacion();
        cambiosBarco.setNombre("Titanic 2.0"); // Cambiamos el nombre
        cambiosBarco.setPlazas(6); // Ampliamos plazas
        
        try {
            Embarcacion actualizado = rest.patchForObject(BASE_URL + "/embarcaciones/" + matricula, cambiosBarco, Embarcacion.class);
            System.out.println("Embarcación actualizada: " + actualizado.getNombre() + ", Plazas: " + actualizado.getPlazas());
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Caso Error: Actualizar barco que no existe
        System.out.println("\n==== REQUEST: PATCH actualizar embarcación (Error - No existe) ====");
        try {
            rest.patchForObject(BASE_URL + "/embarcaciones/MAT-INEXISTENTE", cambiosBarco, Embarcacion.class);
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada: " + e.getStatusCode());
        }

       
        // ACTUALIZAR PATRÓN
        System.out.println("\n--- [PATCH] ACTUALIZAR PATRÓN ---");

        // (Exito)
        System.out.println("\n==== REQUEST: PATCH actualizar patrón (Exito) ====");
        String dniPatron = "73427343U"; 
        Patron cambiosPatron = new Patron();
        cambiosPatron.setNombre("Ezequiel"); // Cambiamos nombre
        
        try {
            Patron actualizado = rest.patchForObject(BASE_URL + "/patrones/" + dniPatron, cambiosPatron, Patron.class);
            System.out.println("Patrón actualizado: " + actualizado.getNombre() + " " + actualizado.getApellidos());
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Caso Error: Actualizar patrón que no existe
        System.out.println("\n==== REQUEST: PATCH actualizar patrón (Error - No existe) ====");
        try {
            rest.patchForObject(BASE_URL + "/patrones/00000000Z", cambiosPatron, Patron.class);
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada: " + e.getStatusCode());
        }
    
        // SECCIÓN 3: VINCULAR PATRÓN A EMBARCACIÓN
      
        System.out.println("\n--- [PATCH] VINCULAR PATRÓN ---");

        //  (Exito)
        System.out.println("\n==== REQUEST: PATCH vincular patrón (Exito) ====");
        Patron patronParaVincular = new Patron();
        patronParaVincular.setDni("24125312Y");
        
        try {
            // Tu controlador devuelve un String, por eso usamos String.class
            String respuesta = rest.patchForObject(BASE_URL + "/embarcaciones/" + matricula + "/patron", patronParaVincular, String.class);
            System.out.println("Resultado: " + respuesta);
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getResponseBodyAsString());
        }

        // Caso Error: Vincular a barco inexistente
        System.out.println("\n==== REQUEST: PATCH vincular (Error - Barco no existe) ====");
        try {
            rest.patchForObject(BASE_URL + "/embarcaciones/FAKE-123/patron", patronParaVincular, String.class);
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada: " + e.getResponseBodyAsString());
        }
       
        // SECCIÓN 4: DESVINCULAR PATRÓN
       
        System.out.println("\n--- [PATCH] DESVINCULAR PATRÓN ---");

        // (Exito)
        System.out.println("\n==== REQUEST: PATCH desvincular patrón (Exito) ====");
        try {
            // El método desvincularPatron no requiere body (@RequestBody), pero patchForObject pide un objeto. Enviamos null o un objeto vacío.
            String respuesta = rest.patchForObject(BASE_URL + "/embarcaciones/KFH3/patron/desvincular", null, String.class);
            System.out.println("Resultado: " + respuesta);
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getResponseBodyAsString());
        }
        
        // Caso Error: Desvincular de barco inexistente
        System.out.println("\n==== REQUEST: PATCH desvincular (Error - Barco no existe) ====");
        try {
            rest.patchForObject(BASE_URL + "/embarcaciones/FAKE-123/patron/desvincular", null, String.class);
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada: " + e.getResponseBodyAsString());
        }

        // SECCIÓN: ALQUILERES

        System.out.println("\n--- [PATCH] ALQUILERES: VINCULAR/DESVINCULAR PASAJEROS ---");

        int idAlquiler = 4; 
        String dniPasajero = "99958844B"; // DNI Para vincular a alquiler existente
        String dniPasajeroDesvincular = "74743587T"; // Para desvincular

        // 1. Vincular a un nuevo socio (no titular)
        System.out.println("\n==== REQUEST: PATCH vincular pasajero a alquiler (Exito) ====");
        try {
            String url = BASE_URL + "/alquileres/{id}/addPasajero?dni={dni}";
            
            // Enviamos 'null' como body porque el dato va en la URL
            String respuesta = rest.patchForObject(url, null, String.class, idAlquiler, dniPasajero);
            System.out.println("Respuesta: " + respuesta);
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getResponseBodyAsString());
        }

        // Caso Error: Vincular a un alquiler pasado o inexistente
        System.out.println("\n==== REQUEST: PATCH vincular pasajero (Error - Alquiler no existe) ====");
        try {
            String url = BASE_URL + "/alquileres/{id}/addPasajero?dni={dni}";
            rest.patchForObject(url, null, String.class, 9999, dniPasajero);
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada correctamente: " + e.getStatusCode());
            System.out.println("Mensaje: " + e.getResponseBodyAsString());
        }

        // 2. Desvincular a un socio
        System.out.println("\n==== REQUEST: PATCH desvincular pasajero de alquiler (Exito) ====");
        try {
            // Controlador: @RequestParam String dni
            String url = BASE_URL + "/alquileres/{id}/removePasajero?dni={dni}";
            
            String respuesta = rest.patchForObject(url, null, String.class, idAlquiler, dniPasajeroDesvincular);
            System.out.println("Respuesta: " + respuesta);
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getResponseBodyAsString());
        }

        // Caso Error: Desvincular de un alquiler ya realizado (pasado)
        System.out.println("\n==== REQUEST: PATCH desvincular pasajero (Error - Alquiler finalizado/Socio no existe) ====");
        try {
            String url = BASE_URL + "/alquileres/{id}/removePasajero?dni={dni}";
            rest.patchForObject(url, null, String.class, 3, "00000000Z"); // DNI falso
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada correctamente: " + e.getStatusCode());
        }

        // SECCIÓN: RESERVAS
        System.out.println("\n--- [PATCH] RESERVAS: MODIFICAR FECHA Y DATOS ---");

        int idReserva = 2; // ID de una reserva existente

        // 1. Modificar la fecha de una reserva futura
        System.out.println("\n==== REQUEST: PATCH modificar fecha reserva (Exito) ====");
        try {
            // Controlador: @RequestParam("nuevaFecha")
            LocalDate nuevaFecha = LocalDate.of(2026, 7, 27); // Fecha futura
            String url = BASE_URL + "/reservas/{id}/fecha?nuevaFecha={f}";

            String respuesta = rest.patchForObject(url, null, String.class, idReserva, nuevaFecha);
            System.out.println("Respuesta: " + respuesta);
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getResponseBodyAsString());
        }

        // Caso Error: Fecha pasada
        System.out.println("\n==== REQUEST: PATCH modificar fecha reserva (Error - Fecha pasada) ====");
        try {
            LocalDate fechaPasada = LocalDate.of(2020, 1, 1);
            String url = BASE_URL + "/reservas/{id}/fecha?nuevaFecha={f}";
            rest.patchForObject(url, null, String.class, idReserva, fechaPasada);
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada correctamente: " + e.getStatusCode());
            System.out.println("Mensaje: " + e.getResponseBodyAsString());
        }

        // 2. Modificar los datos de la reserva (descripción y plazas)
        System.out.println("\n==== REQUEST: PATCH modificar datos reserva (Exito) ====");
        try {
            // Controlador: @RequestParam descripcion, @RequestParam plazas
            String nuevaDesc = "Excursión VIP Actualizada";
            int nuevasPlazas = 4; // Cantidad válida
            
            String url = BASE_URL + "/reservas/{id}/datos?descripcion={d}&plazas={p}";
            String respuesta = rest.patchForObject(url, null, String.class, idReserva, nuevaDesc, nuevasPlazas);
            System.out.println("Respuesta: " + respuesta);
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getResponseBodyAsString());
        }

        // Caso Error: Exceder capacidad embarcación
        System.out.println("\n==== REQUEST: PATCH modificar datos reserva (Error - Excede capacidad) ====");
        try {
            int plazasExcesivas = 500; // Asumiendo que el barco tiene menos de 500 plazas
            String url = BASE_URL + "/reservas/{id}/datos?descripcion={d}&plazas={p}";
            rest.patchForObject(url, null, String.class, idReserva, "Intento Fallido", plazasExcesivas);
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada correctamente: " + e.getStatusCode());
            System.out.println("Mensaje: " + e.getResponseBodyAsString());
        }
    }
    

    // Peticiones DELETE
    private static void sendDeleteRequests() {
        System.out.println("\n************** PETICIONES DELETE **************");

        String dniTitular = "98765432Y"; 

        // 6. Eliminar a un socio si no está vinculado a ninguna inscripción (DELETE)
        System.out.println("\n--- [DELETE] 6. ELIMINAR SOCIO ---");
        System.out.println("\n==== REQUEST: DELETE socio con inscripción (Error - Conflict) ====");
        try {
            rest.delete(BASE_URL + "/socios/{dni}", dniTitular);
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada correctamente: " + e.getStatusCode());
            System.out.println("Mensaje: " + e.getResponseBodyAsString());
        }
 
        //  Cancelar una inscripción individual o familiar (DELETE)
        System.out.println("\n--- [DELETE] 5. CANCELAR INSCRIPCIÓN ---");
        System.out.println("\n==== REQUEST: DELETE cancelar inscripción por titular (Exito) ====");
        try {
            rest.delete(BASE_URL + "/inscripciones/titular/{dni}", dniTitular);
            System.out.println("Solicitud DELETE enviada correctamente. La inscripción ha sido borrada y los socios liberados con dni: " + dniTitular);
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getResponseBodyAsString());
        }

        //  Eliminar a un socio (Reintento tras liberar)
        System.out.println("\n==== REQUEST: DELETE socio liberado (Exito) ====");
        try {
            // Ahora que ya no tiene inscripción (porque la borramos en el paso anterior), sí debería dejar borrarlo.
            rest.delete(BASE_URL + "/socios/{dni}", dniTitular);
            System.out.println("Solicitud DELETE enviada correctamente. El socio ha sido eliminado.");
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getResponseBodyAsString());
        }
       
       
        // SECCIÓN 5: ELIMINAR EMBARCACIÓN
    
        System.out.println("\n--- [DELETE] ELIMINAR EMBARCACIÓN ---");

        // 5. Eliminar una embarcación (Exito)
        System.out.println("\n==== REQUEST: DELETE embarcación (Exito) ====");
        String matricula = "DFR3";
        try {
            rest.delete(BASE_URL + "/embarcaciones/" + matricula);
            System.out.println("Embarcación " + matricula + " eliminada correctamente.");
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getResponseBodyAsString());
        }

        // Caso Error: Eliminar barco que no existe (o ya fue eliminado)
        System.out.println("\n==== REQUEST: DELETE embarcación (Error - No existe) ====");
        try {
            rest.delete(BASE_URL + "/embarcaciones/" + matricula); 
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }

    
        // SECCIÓN 6: ELIMINAR PATRÓN
        
        System.out.println("\n--- [DELETE] ELIMINAR PATRÓN ---");

        // 6. Eliminar un patrón (Exito)
        System.out.println("\n==== REQUEST: DELETE patrón (Exito) ====");
        String dniPatron = "73427343U";
        try {
            rest.delete(BASE_URL + "/patrones/" + dniPatron);
            System.out.println("Patrón " + dniPatron + " eliminado correctamente.");
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getResponseBodyAsString());
        }

        // Caso Error: Eliminar patrón que no existe
        System.out.println("\n==== REQUEST: DELETE patrón (Error - No existe) ====");
        try {
            rest.delete(BASE_URL + "/patrones/00000000Z");
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }

        // SECCIÓN: CANCELAR ALQUILER
        System.out.println("\n--- [DELETE] 3. CANCELAR ALQUILER ---");

        int idAlquiler = 9;

        // 3. Cancelar un alquiler (Exito)
        System.out.println("\n==== REQUEST: DELETE cancelar alquiler (Exito) ====");
        try {
            rest.delete(BASE_URL + "/alquileres/{id}", idAlquiler);
            System.out.println("Alquiler ID " + idAlquiler + " cancelado correctamente.");
        } catch (HttpClientErrorException e) {
            // Si ya se borró en una prueba anterior, saldrá 404, lo controlamos
            System.out.println("Error (o ya borrado): " + e.getResponseBodyAsString());
        }

        // Caso Error: Cancelar un alquiler que no existe
        System.out.println("\n==== REQUEST: DELETE cancelar alquiler (Error - No existe) ====");
        try {
            rest.delete(BASE_URL + "/alquileres/{id}", 9999);
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada correctamente: " + e.getStatusCode());
        }

        // SECCIÓN: CANCELAR RESERVA
        
        System.out.println("\n--- [DELETE] 3. CANCELAR RESERVA ---");

        int idReserva = 4;

        // 3. Cancelar una reserva (Exito)
        System.out.println("\n==== REQUEST: DELETE cancelar reserva (Exito) ====");
        try {
            rest.delete(BASE_URL + "/reservas/{id}", idReserva);
            System.out.println("Reserva ID " + idReserva + " cancelada correctamente.");
        } catch (HttpClientErrorException e) {
            System.out.println("Error (o ya borrada): " + e.getResponseBodyAsString());
        }

        // Caso Error: Cancelar una reserva pasada (simulado, o ID inexistente)
        System.out.println("\n==== REQUEST: DELETE cancelar reserva (Error - No existe/Pasada) ====");
        try {
            rest.delete(BASE_URL + "/reservas/{id}", 9999);
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada correctamente: " + e.getStatusCode());
            System.out.println("Mensaje: " + e.getResponseBodyAsString());
        }
    }
}
