package es.uco.pw.grupo12.model.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import es.uco.pw.grupo12.model.domain.patron.Patron;

@Repository
public class PatronRepository extends AbstractRepository {

    
    public PatronRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        setSQLQueriesFileName("db/sql.properties"); // Carga el archivo de propiedades
    }

   
    private static class PatronRowMapper implements RowMapper<Patron> {
        @Override
        public Patron mapRow(ResultSet rs, int rowNum) throws SQLException {
            Patron patron = new Patron();
            patron.setDni(rs.getString("dni"));
            patron.setNombre(rs.getString("nombre"));
            patron.setApellidos(rs.getString("apellidos"));
            patron.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());
            patron.setFechaExpedicionTitulo(rs.getDate("fecha_expedicion_titulo").toLocalDate());
            return patron;
        }
    }

    
     // Da de alta un nuevo patrón en la base de datos.
   
    public boolean darDeAltaPatron(Patron patron) {
        try {
            String query = sqlQueries.getProperty("insert-patron");
            if (query == null) {
                System.err.println("Error: no se encontró la consulta 'insert-patron' en sql.properties");
                return false;
            }
            int rows = jdbcTemplate.update(query, patron.getDni(), patron.getNombre(), patron.getApellidos(),
                    patron.getFechaNacimiento(), patron.getFechaExpedicionTitulo());
            return rows > 0;
        } catch (DataAccessException e) {
            System.err.println("Error al insertar patrón: " + patron.getDni());
            e.printStackTrace();
            return false;
        }
    }

    
    // Obtiene una lista de todos los patrones registrados.
    
    public List<Patron> obtenerPatrones() {
        try {
            String query = sqlQueries.getProperty("select-allPatrones");
            if (query == null) {
                System.err.println("Error: no se encontró la consulta 'select-allPatrones' en sql.properties");
                return new ArrayList<>();
            }
            return jdbcTemplate.query(query, new PatronRowMapper());
        } catch (DataAccessException e) {
            System.err.println("Error al obtener todos los patrones");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    
    // Busca un patrón por su DNI.
    public Patron findByDni(String dni) {
        try {
            String query = sqlQueries.getProperty("select-patron-byDni");
            if (query == null) {
                System.err.println("Error: no se encontró la consulta 'select-patron-byDni' en sql.properties");
                return null;
            }
            return jdbcTemplate.queryForObject(query, new PatronRowMapper(), dni);
        } catch (EmptyResultDataAccessException e) {
            return null; 
        } catch (DataAccessException e) {
            System.err.println("Error al buscar patrón: " + dni);
            e.printStackTrace();
            return null;
        }
    }

    
     //Comprueba si un patrón ya está asignado a CUALQUIER embarcación.
    public boolean estaAsignado(String dniPatron) {
        try {
            String query = sqlQueries.getProperty("select-embarcacion-byPatron");
            if (query == null) {
                System.err.println("Error: no se encontró la consulta 'select-embarcacion-byPatron' en sql.properties");
                return false;
            }
            
            List<String> matriculas = jdbcTemplate.query(query, 
                (rs, rowNum) -> rs.getString("matricula"), 
                dniPatron);
            
            // Si la lista no está vacía, es que está asignado
            return !matriculas.isEmpty(); 

        } catch (DataAccessException e) {
            System.err.println("Error al comprobar si patrón está asignado: " + dniPatron);
            e.printStackTrace();
            return true; 
        }
    }
    
    // Actualiza los datos de un patrón existente.
    public int update(Patron patron) {
        try {
            String query = sqlQueries.getProperty("update-patron");
            
            return jdbcTemplate.update(query,
                patron.getNombre(),
                patron.getApellidos(),
                java.sql.Date.valueOf(patron.getFechaNacimiento()),
                java.sql.Date.valueOf(patron.getFechaExpedicionTitulo()),
                patron.getDni() // El DNI se usa para buscar cuál actualizar
            );
            
        } catch (Exception e) {
            System.err.println("Error al actualizar el patrón: " + patron.getDni());
            // e.printStackTrace(); // Descomentar para depurar
            return 0;
        }
    }
    
    // Elimina un patrón de la base de datos dado su DNI.
    public boolean delete(String dni) {
        try {
            String query = sqlQueries.getProperty("delete-patron");
            int rows = jdbcTemplate.update(query, dni);
            return rows > 0;
        } catch (DataAccessException e) {
            System.err.println("Error al borrar el patrón: " + dni);
            e.printStackTrace();
            return false;
        }
    }

}