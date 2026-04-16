package es.uco.pw.grupo12;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import es.uco.pw.grupo12.model.domain.socio.Socio;
import es.uco.pw.grupo12.model.domain.inscripcion.Inscripcion;
import es.uco.pw.grupo12.model.domain.alquiler.Alquiler;
import es.uco.pw.grupo12.model.domain.embarcacion.Embarcacion;
import es.uco.pw.grupo12.model.domain.embarcacion.TipoEmbarcacion;
import es.uco.pw.grupo12.model.domain.patron.Patron;
import es.uco.pw.grupo12.model.domain.reserva.Reserva;

public class Grupo12ClientApplication {

    private static final String BASE_URL = "http://localhost:8080/api";
    private static RestTemplate rest = new RestTemplate();

    public static void main(String[] args) {
        System.out.println("INICIANDO PRUEBAS DE CLIENTE API REST...");
        sendGetRequests();
        System.out.println("\n==================================================");
        System.out.println("==================================================");
        sendPostRequests();
    }

    /**
     * Envía todas las peticiones GET solicitadas, separadas por temática.
     */
    private static void sendGetRequests() {
        System.out.println("\n************** PETICIONES GET **************");

        // SECCIÓN 1: SOCIOS
        System.out.println("\n--- [GET] SECCIÓN SOCIOS ---");

        // Obtener la lista completa de socios
        System.out.println("\n==== REQUEST: GET listar todos los socios (Exito) ====");
        try {
            ResponseEntity<Socio[]> response = rest.getForEntity(BASE_URL + "/socios", Socio[].class);
            Socio[] sociosArray = response.getBody();

            if (sociosArray != null) {
                List<Socio> socios = Arrays.asList(sociosArray);
                System.out.println("Status: " + response.getStatusCode());
                System.out.println("Total socios recuperados: " + socios.size());
                // Muestra la info detallada
                socios.forEach(s -> System.out.println("   -> " + s.getNombre() + " " + s.getApellidos() + " (DNI: " + s.getDni() + ")"));
            } else {
                System.out.println("La lista de socios está vacía.");
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Obtener la información de un socio dado su DNI
        System.out.println("\n==== REQUEST: GET socio por DNI (Exito) ====");
        String dniExistente = "12312312P"; 
        try {
            Socio socio = rest.getForObject(BASE_URL + "/socios/{dni}", Socio.class, dniExistente);
            System.out.println("Socio recuperado: " + socio.toString());
        } catch (HttpClientErrorException e) {
            System.out.println("Error recuperando socio: " + e.getMessage());
        }

        System.out.println("\n==== REQUEST: GET socio por DNI (Error - No existe) ====");
        try {
            rest.getForObject(BASE_URL + "/socios/{dni}", Socio.class, "00000000X");
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada correctamente: " + e.getStatusCode());
        }

        // SECCIÓN 2: INSCRIPCIONES
        List<Socio> todosLosSocios;
        try {
            ResponseEntity<Socio[]> resSocios = rest.getForEntity(BASE_URL + "/socios", Socio[].class);
            todosLosSocios = (resSocios.getBody() != null) ? Arrays.asList(resSocios.getBody()) : List.of();
        } catch (Exception e) {
            todosLosSocios = List.of(); // Si falla, lista vacía
        }
        System.out.println("\n--- [GET] SECCIÓN INSCRIPCIONES ---");

        // Obtener la lista de inscripciones individuales
        System.out.println("\n==== REQUEST: GET inscripciones individuales (Exito) ====");
        try {
            ResponseEntity<Inscripcion[]> response = rest.getForEntity(BASE_URL + "/inscripciones/individuales", Inscripcion[].class);
            Inscripcion[] indivArray = response.getBody();
            
            if (indivArray != null) {
                List<Inscripcion> indiv = Arrays.asList(indivArray);
                System.out.println("Inscripciones individuales recuperadas: " + indiv.size());
                // Bucle para mostrar detalles
                for (Inscripcion i : indiv) {
                    System.out.println("   -> ID: " + i.getIdInscripcion() + 
                                       " | Titular DNI: " + (i.getSocioTitular() != null ? i.getSocioTitular().getDni() : "null") + 
                                       " | Cuota: " + i.getCuota());
                }
            } else {
                System.out.println("No hay inscripciones individuales.");
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getMessage());
        }

       // Obtener la lista de inscripciones familiares
        System.out.println("\n==== REQUEST: GET inscripciones familiares (Exito) ====");
        try {
            ResponseEntity<Inscripcion[]> response = rest.getForEntity(BASE_URL + "/inscripciones/familiares", Inscripcion[].class);
            Inscripcion[] famArray = response.getBody();
            
            if (famArray != null) {
                List<Inscripcion> fam = Arrays.asList(famArray);
                System.out.println("Inscripciones familiares recuperadas: " + fam.size());
                
                for (Inscripcion f : fam) {
                    long numFamiliares = todosLosSocios.stream()
                        .filter(s -> s.getIdInscripcionFk() == f.getIdInscripcion()) // Coincide el ID
                        .filter(s -> !s.getDni().equals(f.getSocioTitular().getDni())) // Que no sea el titular
                        .count();
                    System.out.println("   -> ID: " + f.getIdInscripcion() + 
                                       " | Titular DNI: " + (f.getSocioTitular() != null ? f.getSocioTitular().getDni() : "null") + 
                                       " | Nº Familiares: " + numFamiliares +  // Usamos nuestro cálculo
                                       " | Cuota: " + f.getCuota());
                }
            } else {
                System.out.println("No hay inscripciones familiares.");
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Obtener la información de una inscripción dado el DNI del socio titular
        System.out.println("\n==== REQUEST: GET inscripción por titular (Exito) ====");
        try {
            Inscripcion inscripcion = rest.getForObject(BASE_URL + "/inscripciones/titular/{dni}", Inscripcion.class, dniExistente);
            System.out.println("Inscripción recuperada -> ID: " + inscripcion.getIdInscripcion() + 
                               ", Tipo: " + inscripcion.getTipo() + 
                               ", Cuota: " + inscripcion.getCuota());
        } catch (HttpClientErrorException e) {
            System.out.println("Nota: Puede que el DNI " + dniExistente + " no sea titular o no tenga inscripción.");
        }

        System.out.println("\n==== REQUEST: GET inscripción por titular (Error - No existe) ====");
        try {
            rest.getForObject(BASE_URL + "/inscripciones/titular/{dni}", Inscripcion.class, "99999999Z");
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada correctamente: " + e.getStatusCode());
        }

        // SECCIÓN 3: FLOTA Y PATRONES
        System.out.println("\n--- [GET] SECCIÓN FLOTA Y PATRONES ---");

        // Obtener la lista completa de embarcaciones
        System.out.println("\n==== REQUEST: GET todas las embarcaciones (Exito) ====");
        try {
            ResponseEntity<Embarcacion[]> response = rest.getForEntity(BASE_URL + "/embarcaciones", Embarcacion[].class);
            Embarcacion[] barcosArray = response.getBody();
            
            if (barcosArray != null) {
                List<Embarcacion> barcos = Arrays.asList(barcosArray);
                System.out.println("Total embarcaciones: " + barcos.size());
                barcos.forEach(b -> System.out.println("   -> " + b.getNombre() + " (Matrícula: " + b.getMatricula() + ") - Tipo: " + b.getTipo()));
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Obtener la lista de embarcaciones según el tipo
        System.out.println("\n==== REQUEST: GET embarcaciones por tipo VELERO (Exito) ====");
        try {
            ResponseEntity<Embarcacion[]> response = rest.getForEntity(BASE_URL + "/embarcaciones/tipo/VELERO", Embarcacion[].class);
            Embarcacion[] velerosArray = response.getBody();
            
            if (velerosArray != null) {
                List<Embarcacion> veleros = Arrays.asList(velerosArray);
                System.out.println("Veleros encontrados: " + veleros.size());
                // Bucle para mostrar detalles
                veleros.forEach(v -> System.out.println("   -> " + v.getNombre() + " (" + v.getMatricula() + ")"));
            } else {
                System.out.println("No se encontraron veleros.");
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\n==== REQUEST: GET embarcaciones por tipo (Error - Tipo inválido) ====");
        try {
            rest.getForEntity(BASE_URL + "/embarcaciones/tipo/NAVE_ESPACIAL", Embarcacion[].class);
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada correctamente: " + e.getStatusCode());
        }

        // Obtener la lista completa de patrones
        System.out.println("\n==== REQUEST: GET todos los patrones (Exito) ====");
        try {
            ResponseEntity<Patron[]> response = rest.getForEntity(BASE_URL + "/patrones", Patron[].class);
            Patron[] patronesArray = response.getBody();
            
            if (patronesArray != null) {
                List<Patron> patrones = Arrays.asList(patronesArray);
                System.out.println("Total patrones: " + patrones.size());
                // Bucle para mostrar detalles
                patrones.forEach(p -> System.out.println("   -> " + p.getNombre() + " " + p.getApellidos() + " (DNI: " + p.getDni() + ")"));
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // SECCIÓN 4: ALQUILERES
        System.out.println("\n--- [GET] SECCIÓN ALQUILERES ---");

        // Obtener la lista completa de alquileres
        System.out.println("\n==== REQUEST: GET todos los alquileres ====");
        try {
            ResponseEntity<Alquiler[]> response = rest.getForEntity(BASE_URL + "/alquileres", Alquiler[].class);
            
            if (response.getBody() != null) {
                List<Alquiler> alquileres = Arrays.asList(response.getBody());
                System.out.println("Alquileres encontrados: " + alquileres.size());
                for (Alquiler a : alquileres) {
                    // Usamos el toString() de Alquiler
                    System.out.println(a); 
                    System.out.println("--------------");
                }
            } else {
                System.out.println("No hay alquileres registrados (204 No Content).");
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Obtener la lista de alquileres futuros dada una fecha
        System.out.println("\n==== REQUEST: GET alquileres futuros ====");
        String fechaConsulta = "2025-01-01"; // Pon una fecha anterior a los alquileres que tengas
        try {
            ResponseEntity<Alquiler[]> response = rest.getForEntity(BASE_URL + "/alquileres/futuros?fecha=" + fechaConsulta, Alquiler[].class);
            
            if (response.getBody() != null) {
                List<Alquiler> futuros = Arrays.asList(response.getBody());
                System.out.println("Alquileres futuros desde " + fechaConsulta + ": " + futuros.size());
                for (Alquiler a : futuros) {
                    System.out.println(" - ID: " + a.getIdAlquiler() + " | Fecha Inicio: " + a.getFechaInicio() + " | Barco: " + a.getEmbarcacion().getNombre());
                }
            } else {
                System.out.println("No hay alquileres futuros a partir de " + fechaConsulta);
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Obtener la información concreta de un alquiler por ID
        System.out.println("\n==== REQUEST: GET alquiler por ID ====");
        int idAlquilerEjemplo = 2; // Cambia según datos existentes
        try {
            Alquiler alquiler = rest.getForObject(BASE_URL + "/alquileres/{id}", Alquiler.class, idAlquilerEjemplo);
            System.out.println("Alquiler recuperado:");
            System.out.println(" - ID: " + alquiler.getIdAlquiler());
            System.out.println(" - Fecha Inicio: " + alquiler.getFechaInicio());
            System.out.println(" - Fecha Fin: " + alquiler.getFechaFin());
            System.out.println(" - Embarcación: " + alquiler.getEmbarcacion().getNombre());
        } catch (HttpClientErrorException e) {
            System.out.println("Error (alquiler no encontrado): " + e.getStatusCode());
        }

        // Obtener embarcaciones disponibles entre dos fechas → GET
        System.out.println("\n==== REQUEST: GET embarcaciones disponibles ====");
        String inicio = "2025-06-01";
        String fin = "2025-06-10";
        try {
            ResponseEntity<Embarcacion[]> response = rest.getForEntity(
                BASE_URL + "/embarcaciones/disponibles?inicio=" + inicio + "&fin=" + fin,
                Embarcacion[].class
            );

            if (response.getBody() != null) {
                List<Embarcacion> disponibles = Arrays.asList(response.getBody());
                System.out.println("Embarcaciones disponibles (" + inicio + " a " + fin + "): " + disponibles.size());
                disponibles.forEach(b ->
                    System.out.println(" - " + b.getNombre() + " (" + b.getMatricula() + ")")
                );
            } else {
                System.out.println("No hay embarcaciones disponibles.");
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error consultando disponibilidad: " + e.getMessage());
        }

        // SECCIÓN 5: RESERVAS
        System.out.println("\n--- [GET] SECCIÓN RESERVAS / ACTIVIDADES ---");

        // Obtener lista completa de reservas
        System.out.println("\n==== REQUEST: GET todas las reservas ====");
        try {
            ResponseEntity<Inscripcion[]> response = rest.getForEntity(BASE_URL + "/reservas", Inscripcion[].class);

            if (response.getBody() != null) {
                List<Inscripcion> reservas = Arrays.asList(response.getBody());
                System.out.println("Reservas encontradas: " + reservas.size());
            } else {
                System.out.println("No hay reservas registradas.");
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getStatusCode());
        }

        // 2. Obtener reservas futuras dada una fecha
        System.out.println("\n==== REQUEST: GET reservas futuras ====");
        String fechaReservas = "2026-01-01";
        try {
            ResponseEntity<Inscripcion[]> response = rest.getForEntity(
                BASE_URL + "/reservas/futuras?fecha=" + fechaReservas,
                Inscripcion[].class
            );

            if (response.getBody() != null) {
                List<Inscripcion> futuras = Arrays.asList(response.getBody());
                System.out.println("Reservas futuras desde " + fechaReservas + ": " + futuras.size());
            } else {
                System.out.println("No hay reservas futuras.");
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getStatusCode());
        }

        // Obtener la información concreta de una reserva por ID
        System.out.println("\n==== REQUEST: GET reserva por ID ====");
        int idReservaEjemplo = 1; // Ajusta este ID según tus datos

        try {
        Reserva reserva = rest.getForObject(BASE_URL + "/reservas/{id}", Reserva.class, idReservaEjemplo);
    
    if (reserva != null) {
        System.out.println("--- Datos de la Reserva ---");
        System.out.println("ID Reserva: " + reserva.getIdReserva());
        System.out.println("Fecha: " + reserva.getFechaActividad());
        System.out.println("Descripción: " + reserva.getDescripcion());
        System.out.println("Plazas: " + reserva.getPlazasSolicitadas());
        System.out.println("Precio Total: " + reserva.getPrecioTotal() + " €");
        
        // Accedemos a los objetos anidados para sacar sus datos clave
        if (reserva.getSocioSolicitante() != null) {
            System.out.println("Socio: " + reserva.getSocioSolicitante().getNombre() + " (DNI: " + reserva.getSocioSolicitante().getDni() + ")");
        }
        
        if (reserva.getEmbarcacion() != null) {
            System.out.println("Embarcación: " + reserva.getEmbarcacion().getNombre() + " (Matrícula: " + reserva.getEmbarcacion().getMatricula() + ")");
        }
        System.out.println("---------------------------");
    } 

} catch (HttpClientErrorException e) {
    System.out.println("Error recuperando reserva: " + e.getStatusCode());
}
    }

    /**
     * Funcion de solicitudes POST
     */
    private static void sendPostRequests() {
        System.out.println("\n************** PETICIONES POST **************");

        // SECCIÓN 1: SOCIOS
        System.out.println("\n--- [POST] SECCIÓN SOCIOS ---");

        // Crear un nuevo socio sin asociación previa
        System.out.println("\n==== REQUEST: POST crear socio nuevo (Exito) ====");
        Socio nuevoSocio = new Socio("88776655X", "Nuevo", "Usuario", LocalDate.of(1990, 1, 1), "Calle Test 1", LocalDate.now());
        try {
            ResponseEntity<String> response = rest.postForEntity(BASE_URL + "/socios", nuevoSocio, String.class);
            System.out.println("Status: " + response.getStatusCode());
            System.out.println("Body: " + response.getBody());
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getResponseBodyAsString());
        }

        System.out.println("\n==== REQUEST: POST crear socio (Error - DNI Duplicado) ====");
        // Intentamos insertar el mismo socio otra vez
        try {
            rest.postForEntity(BASE_URL + "/socios", nuevoSocio, String.class);
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada correctamente: " + e.getStatusCode());
        }

        // Crear un nuevo socio asociándolo a una inscripción familiar ya existente
        System.out.println("\n==== REQUEST: POST crear socio familiar (Exito) ====");
        Socio familiar = new Socio("99887766F", "Familiar", "Hijo", LocalDate.of(2010, 5, 5), "Calle Test 1", LocalDate.now());
        int idInscripcionExistente = 1; 
        try {
            ResponseEntity<String> response = rest.postForEntity(BASE_URL + "/socios/inscripcion/" + idInscripcionExistente, familiar, String.class);
            System.out.println("Status: " + response.getStatusCode());
            System.out.println("Body: " + response.getBody());
        } catch (HttpClientErrorException e) {
            System.out.println("Error (verifica que ID inscripcion exista): " + e.getResponseBodyAsString());
        }

        System.out.println("\n==== REQUEST: POST crear socio familiar (Error - ID Inscripción no existe) ====");
        try {
            rest.postForEntity(BASE_URL + "/socios/inscripcion/9999", familiar, String.class);
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada correctamente: " + e.getStatusCode());
        }


        // SECCIÓN 2: INSCRIPCIONES
        System.out.println("\n--- [POST] SECCIÓN INSCRIPCIONES ---");

        // Crear una inscripción para un socio titular
        System.out.println("Creando socio auxiliar para la prueba...");
        Socio socioAux = new Socio();
        socioAux.setDni("99833736L"); // Un DNI que sepas que no existe
        socioAux.setNombre("Test");
        socioAux.setApellidos("Titular");
        socioAux.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        socioAux.setDireccion("Calle Test");

        // Llamamos a tu método crearSocio (que sí permite nulos en inscripción)
        rest.postForEntity(BASE_URL + "/socios", socioAux, String.class);
        System.out.println("\n==== REQUEST: POST crear inscripción para titular (Exito) ====");
        Inscripcion nuevaInscripcion = new Inscripcion();
        Socio titularParaInscripcion = new Socio();
        titularParaInscripcion.setDni("99833736L"); // DNI del socio creado antes
        nuevaInscripcion.setSocioTitular(titularParaInscripcion);
        
        try {
            ResponseEntity<Inscripcion> response = rest.postForEntity(BASE_URL + "/inscripciones", nuevaInscripcion, Inscripcion.class);
            System.out.println("Status: " + response.getStatusCode());
            System.out.println("Inscripción creada ID: " + response.getBody().getIdInscripcion());
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getResponseBodyAsString());
        }

        System.out.println("\n==== REQUEST: POST crear inscripción (Error - Socio no existe) ====");
        Inscripcion inscripcionFake = new Inscripcion();
        Socio titularFake = new Socio();
        titularFake.setDni("0000FAKE");
        inscripcionFake.setSocioTitular(titularFake);
        try {
            rest.postForEntity(BASE_URL + "/inscripciones", inscripcionFake, Inscripcion.class);
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada correctamente: " + e.getStatusCode());
        }

        // SECCIÓN 3: FLOTA Y PATRONES
        System.out.println("\n--- [POST] SECCIÓN FLOTA Y PATRONES ---");

        // Crear una nueva embarcación (sin patrón)
        System.out.println("\n==== REQUEST: POST crear embarcación (Exito) ====");
        Embarcacion barco = new Embarcacion("MAT-2025", TipoEmbarcacion.VELERO, "La Perla Negra", 6, new BigDecimal("12.5"), null);
        try {
            ResponseEntity<String> response = rest.postForEntity(BASE_URL + "/embarcaciones", barco, String.class);
            System.out.println("Status: " + response.getStatusCode());
            System.out.println("Body: " + response.getBody());
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getResponseBodyAsString());
        }

        System.out.println("\n==== REQUEST: POST crear embarcación (Error - Matrícula vacía) ====");
        Embarcacion barcoMal = new Embarcacion(null, TipoEmbarcacion.LANCHA, "Error", 4, BigDecimal.TEN, null);        try {
            rest.postForEntity(BASE_URL + "/embarcaciones", barcoMal, String.class);
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada correctamente: " + e.getStatusCode());
        }

        // 5. Crear un nuevo patrón (sin embarcación)
        System.out.println("\n==== REQUEST: POST crear patrón (Exito) ====");
        Patron patron = new Patron("44455566P", "Jacobo", "Esparraga", LocalDate.of(1975, 10, 10), LocalDate.of(2000, 1, 1));
        try {
            ResponseEntity<String> response = rest.postForEntity(BASE_URL + "/patrones", patron, String.class);
            System.out.println("Status: " + response.getStatusCode());
            System.out.println("Body: " + response.getBody());
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getResponseBodyAsString());
        }

        System.out.println("\n==== REQUEST: POST crear patrón (Error - DNI inválido) ====");
        Patron patronMal = new Patron("-1", "Mal", "Patron", LocalDate.now(), LocalDate.now());
        try {
            rest.postForEntity(BASE_URL + "/patrones", patronMal, String.class);
        } catch (HttpClientErrorException e) {
            System.out.println("Excepción capturada correctamente: " + e.getStatusCode());
        }

        // SECCIÓN 4: CREAR ALQUILER
        System.out.println("\n--- [POST] SECCIÓN ALQUILERES ---");

        // 5. Crear un alquiler para una embarcación disponible
        System.out.println("\n==== REQUEST: POST crear alquiler ====");
        try {
            Alquiler nuevoAlquiler = new Alquiler();
            nuevoAlquiler.setFechaInicio(LocalDate.of(2025, 7, 1));
            nuevoAlquiler.setFechaFin(LocalDate.of(2025, 7, 7));

            Embarcacion disponible = new Embarcacion();
            disponible.setMatricula(""); // Cambiar por uno existente
            nuevoAlquiler.setEmbarcacion(disponible);

            ResponseEntity<String> response = rest.postForEntity(
                BASE_URL + "/alquileres",
                nuevoAlquiler,
                String.class
            );

            System.out.println("Status: " + response.getStatusCode());
            System.out.println("Body: " + response.getBody());

        } catch (HttpClientErrorException e) {
            System.out.println("Error creando alquiler: " + e.getResponseBodyAsString());
        }

        // SECCIÓN 5: CREAR RESERVA
        System.out.println("\n--- [POST] SECCIÓN RESERVAS ---");

        System.out.println("\n==== REQUEST: POST crear reserva ====");
        try {
            Reserva nuevaReserva = new Reserva();

            // Fecha de la actividad
            nuevaReserva.setFechaActividad(LocalDate.of(2026, 8, 1));
        nuevaReserva.setPlazasSolicitadas(3);
        nuevaReserva.setDescripcion("Excursión de prueba");

        // Embarcación disponible
        Embarcacion barcoReserva = new Embarcacion();
        barcoReserva.setMatricula("LDFG4"); 
        nuevaReserva.setEmbarcacion(barcoReserva);

        // POST al servidor
        ResponseEntity<String> response = rest.postForEntity(
        BASE_URL + "/reservas",
        nuevaReserva,
        String.class
        );

        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Body: " + response.getBody());

        } catch (HttpClientErrorException e) {
        System.out.println("Error al crear reserva: " + e.getResponseBodyAsString());
        }

    }
}