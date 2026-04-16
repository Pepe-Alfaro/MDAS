package es.uco.pw.grupo12.model.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import es.uco.pw.grupo12.model.domain.embarcacion.Embarcacion;
import es.uco.pw.grupo12.model.domain.embarcacion.TipoEmbarcacion;
import es.uco.pw.grupo12.model.domain.patron.Patron;

@Repository
public class EmbarcacionRepository extends AbstractRepository {

    public EmbarcacionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        setSQLQueriesFileName("db/sql.properties"); 
    }

    
     
    private static class EmbarcacionRowMapper implements RowMapper<Embarcacion> {
        @Override
        public Embarcacion mapRow(ResultSet rs, int rowNum) throws SQLException {
            Embarcacion embarcacion = new Embarcacion();
            embarcacion.setMatricula(rs.getString("matricula"));
            embarcacion.setNombre(rs.getString("nombre"));
            embarcacion.setTipo(TipoEmbarcacion.valueOf(rs.getString("tipo")));
            embarcacion.setPlazas(rs.getInt("plazas"));
            embarcacion.setDimensiones(rs.getBigDecimal("dimensiones")); 

            String dniPatron = rs.getString("dni_patron_asignado_fk");
            if (dniPatron != null) {
                Patron patron = new Patron();
                patron.setDni(dniPatron);
                embarcacion.setPatron(patron);
            } else {
                embarcacion.setPatron(new Patron()); 
            }
            return embarcacion;
        }
    }

    public Embarcacion findByMatricula(String matricula) {
        try {
            String query = sqlQueries.getProperty("select-embarcacion-byMatricula");
            return jdbcTemplate.queryForObject(query, new EmbarcacionRowMapper(), matricula);
        } catch (EmptyResultDataAccessException e) {
            return null; 
        } catch (DataAccessException e) {
            System.err.println("Error al buscar embarcación por matrícula: " + matricula);
            e.printStackTrace();
            return null;
        }
    }

    public Embarcacion findByNombre(String nombre) {
        try {
            String query = sqlQueries.getProperty("select-embarcacion-byNombre");
            return jdbcTemplate.queryForObject(query, new EmbarcacionRowMapper(), nombre);
        } catch (EmptyResultDataAccessException e) {
            return null; 
        } catch (DataAccessException e) {
            System.err.println("Error al buscar embarcación por nombre: " + nombre);
            e.printStackTrace();
            return null;
        }
    }

    public boolean save(Embarcacion embarcacion) {
        try {
            String query = sqlQueries.getProperty("insert-embarcacion");
            
            int rows = jdbcTemplate.update(query,
                    embarcacion.getMatricula(),
                    embarcacion.getTipo().toString(),
                    embarcacion.getNombre(),
                    embarcacion.getPlazas(),
                    embarcacion.getDimensiones(),
                    null 
            );
            return rows > 0;
        } catch (DataAccessException e) {
            System.err.println("Error al insertar embarcación: " + embarcacion.getMatricula());
            e.printStackTrace();
            return false;
        }
    }

    public List<Embarcacion> findAll() {
        try {
            String query = sqlQueries.getProperty("select-allEmbarcaciones");
            return jdbcTemplate.query(query, new EmbarcacionRowMapper());
        } catch (DataAccessException e) {
            System.err.println("Error al buscar todas las embarcaciones");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public List<Embarcacion> findByTipo(TipoEmbarcacion tipo) {
        try {
            String query = sqlQueries.getProperty("select-embarcacion-byTipo");
            if (query == null) {
                System.err.println("La query 'select-embarcacion-byTipo' no se encontró.");
                return new ArrayList<>();
            }
            
            return jdbcTemplate.query(query, new EmbarcacionRowMapper(), tipo.toString());

        } catch (DataAccessException e) {
            System.err.println("Error al buscar embarcaciones por tipo: " + tipo);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Asocia un patrón a una embarcación.
   
    public boolean asociarPatron(String matricula, String dniPatron) {
        try {
            String query = sqlQueries.getProperty("update-embarcacion-asociarPatron"); 
            if (query == null) {
                System.err.println("Error: no se encontró la consulta 'update-embarcacion-asociarPatron' en sql.properties");
                return false;
            }
            
            // Si el DNI es "-1" (valor por defecto), lo guardamos como NULL en la BD
            String patronDniFinal = (dniPatron != null && dniPatron.equals("-1")) ? null : dniPatron;
            
            int rows = jdbcTemplate.update(query, patronDniFinal, matricula);
            return rows > 0;
        } catch (DataAccessException e) {
            System.err.println("Error al asociar patrón " + dniPatron + " a embarcación " + matricula);
            e.printStackTrace();
            return false;
        }
    }

    // Busca embarcaciones que no tengan reservas en un rango de fechas.
   
    public List<Embarcacion> findEmbarcacionesDisponibles(LocalDate fechaInicio, LocalDate fechaFin) {
        try {
           
            String query = sqlQueries.getProperty("select-embarcaciones-disponibles");
            if (query == null) {
                System.err.println("La query 'select-embarcaciones-disponibles' no se encontró.");
                return new ArrayList<>();
            }
            
            
            return jdbcTemplate.query(query, new EmbarcacionRowMapper(), fechaInicio, fechaFin);

        } catch (DataAccessException e) {
            System.err.println("Error al buscar embarcaciones disponibles entre " + fechaInicio + " y " + fechaFin);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

public boolean checkDisponibilidadAlquiler(String matricula, LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            String query = sqlQueries.getProperty("check-alquiler-disponibilidad");
            Integer count = jdbcTemplate.queryForObject(query, Integer.class, matricula, fechaFin, fechaInicio);
            return (count != null && count > 0);
        } catch (DataAccessException e) {
            return true; // Ante error, asumimos ocupado por seguridad
        }
    }

    public boolean checkDisponibilidadReserva(String matricula, LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            String query = sqlQueries.getProperty("check-reserva-disponibilidad");
            Integer count = jdbcTemplate.queryForObject(query, Integer.class, matricula, fechaInicio, fechaFin);
            return (count != null && count > 0);
        } catch (DataAccessException e) {
            return true;
        }
    }

    // Método exclusivo para la API: Busca embarcaciones libres de Alquileres Y Reservas.
    public List<Embarcacion> findDisponiblesParaApi(LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            String query = sqlQueries.getProperty("select-embarcaciones-disponibles-api");
            return jdbcTemplate.query(query, new EmbarcacionRowMapper(),
                java.sql.Date.valueOf(fechaFin),    // Param 1
                java.sql.Date.valueOf(fechaInicio), // Param 2
                java.sql.Date.valueOf(fechaInicio), // Param 3
                java.sql.Date.valueOf(fechaFin)     // Param 4
            );

        } catch (DataAccessException e) {
            System.err.println("Error API buscando embarcaciones disponibles");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Actualiza los datos de una embarcación existente.
    public int update(Embarcacion embarcacion) {
        try {
            String query = sqlQueries.getProperty("update-embarcacion");
            
            return jdbcTemplate.update(query,
                embarcacion.getNombre(),
                embarcacion.getTipo().toString(), // Guardamos el Enum como String
                embarcacion.getPlazas(),
                embarcacion.getDimensiones(),
                embarcacion.getMatricula() // Se usa en el WHERE
            );
            
        } catch (DataAccessException e) {
            System.err.println("Error al actualizar la embarcación: " + embarcacion.getMatricula());
            e.printStackTrace();
            return 0;
        }
    }
    
    // Comprueba si una embarcación tiene historial (Alquileres o Reservas).
    // Devuelve true si está vinculada a algo (NO se puede borrar).
    
    public boolean tieneHistorial(String matricula) {
        try {
            String queryAlquiler = sqlQueries.getProperty("count-alquileres-embarcacion");
            String queryReserva = sqlQueries.getProperty("count-reservas-embarcacion");

            Integer numAlquileres = jdbcTemplate.queryForObject(queryAlquiler, Integer.class, matricula);
            Integer numReservas = jdbcTemplate.queryForObject(queryReserva, Integer.class, matricula);

            // Si tiene algún alquiler O alguna reserva, tiene historial
            return (numAlquileres != null && numAlquileres > 0) || (numReservas != null && numReservas > 0);

        } catch (DataAccessException e) {
            System.err.println("Error comprobando historial de la embarcación: " + matricula);
            return true; // Ante la duda, bloqueamos el borrado por seguridad
        }
    }

    
    //Elimina una embarcación por su matrícula.
    public boolean delete(String matricula) {
        try {
            String query = sqlQueries.getProperty("delete-embarcacion");
            int rows = jdbcTemplate.update(query, matricula);
            return rows > 0;
        } catch (DataAccessException e) {
            System.err.println("Error al borrar la embarcación: " + matricula);
            e.printStackTrace();
            return false;
        }
    }
}