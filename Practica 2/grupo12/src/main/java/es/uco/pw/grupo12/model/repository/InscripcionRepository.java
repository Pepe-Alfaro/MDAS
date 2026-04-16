package es.uco.pw.grupo12.model.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException; 
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper; 
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import es.uco.pw.grupo12.model.domain.inscripcion.Inscripcion;
import es.uco.pw.grupo12.model.domain.inscripcion.TipoInscripcion; 
import es.uco.pw.grupo12.model.domain.socio.Socio;

@Repository
public class InscripcionRepository extends AbstractRepository {

    public InscripcionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        setSQLQueriesFileName("db/sql.properties");
    }

    
    // Guarda una inscripción individual para un socio titular.
    
    public int saveInscripcionIndividual(Socio socioTitular) {
        try {
            String query = sqlQueries.getProperty("insert-inscripcion-individual");
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, socioTitular.getDni());
                    ps.setDouble(2, 300.0); // Cuota individual por defecto
                    ps.setDate(3, Date.valueOf(socioTitular.getFechaInscripcion()));
                    return ps;
                }
            }, keyHolder);

            // Obtenemos el ID generado
            if (keyHolder.getKey() != null) {
                return keyHolder.getKey().intValue();
            } else {
                return -1; // No se pudo obtener el ID
            }
            
        } catch (DataAccessException e) {
            System.err.println("Error al insertar inscripción individual para: " + socioTitular.getDni());
            e.printStackTrace();
            return -1;
        }
    }

    private static class InscripcionRowMapper implements RowMapper<Inscripcion> {
        @Override
        public Inscripcion mapRow(ResultSet rs, int rowNum) throws SQLException {
            Inscripcion inscripcion = new Inscripcion();
            inscripcion.setIdInscripcion(rs.getInt("id_inscripcion"));
            inscripcion.setCuota(rs.getDouble("cuota"));
            inscripcion.setFechaCreacion(rs.getDate("fecha_creacion").toLocalDate());
            inscripcion.setTipo(TipoInscripcion.valueOf(rs.getString("tipo")));
            
            // Guardamos el DNI del titular en el objeto Socio anidado
            Socio titular = new Socio();
            titular.setDni(rs.getString("dni_socio_titular_fk"));
            inscripcion.setSocioTitular(titular); 

            return inscripcion;
        }
    }

private static class InscripcionWithCountRowMapper implements RowMapper<Inscripcion> {
    @Override
    public Inscripcion mapRow(ResultSet rs, int rowNum) throws SQLException {
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setIdInscripcion(rs.getInt("id_inscripcion"));
        inscripcion.setCuota(rs.getDouble("cuota"));
        inscripcion.setFechaCreacion(rs.getDate("fecha_creacion").toLocalDate());
        inscripcion.setTipo(TipoInscripcion.valueOf(rs.getString("tipo")));

        Socio titular = new Socio();
        titular.setDni(rs.getString("dni_socio_titular_fk"));
        inscripcion.setSocioTitular(titular); 
        // Mapeamos el resultado del COUNT(*) a nuestro nuevo campo
        inscripcion.setNumeroFamiliares(rs.getLong("num_familiares"));

        return inscripcion;
    }
}

    // Recupera la Inscripción (ID, tipo y cuota) del socio titular por su DNI.

    public Inscripcion findInscripcionByTitularDni(String dniTitular) {
        try {
            String query = sqlQueries.getProperty("select-inscripcion-byTitularDni");
            return jdbcTemplate.queryForObject(query, new InscripcionRowMapper(), dniTitular);
        } catch (EmptyResultDataAccessException e) {
            return null; // No se encontró, lo cual es normal
        } catch (DataAccessException e) {
            System.err.println("Error al buscar inscripción por DNI titular: " + dniTitular);
            e.printStackTrace();
            return null;
        }
    }

    
    // Actualiza la inscripción a tipo FAMILIAR y establece la nueva cuota.

    public boolean updateInscripcionAFamiliar(int idInscripcion, double nuevaCuota) {
        try {
            String query = sqlQueries.getProperty("update-inscripcion-aFamiliar");
            int rowsAffected = jdbcTemplate.update(query, nuevaCuota, idInscripcion);
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            System.err.println("Error al actualizar inscripción a FAMILIAR: " + idInscripcion);
            e.printStackTrace();
            return false;
    }
 }
    
    // Obtiene la lista de todas las inscripciones con el DNI del titular,

public List<Inscripcion> findAllWithFamiliarCount() {
    try {
        String query = sqlQueries.getProperty("select-inscripciones-listado");
        if (query == null) {
            System.err.println("La query 'select-inscripciones-listado' no se encontró.");
            return new ArrayList<>();
        }

        // Usamos el nuevo RowMapper que acabamos de crear
        return jdbcTemplate.query(query, new InscripcionWithCountRowMapper());

    } catch (DataAccessException e) {
        System.err.println("Error al buscar el listado de inscripciones");
        e.printStackTrace();
        return new ArrayList<>();
    }
}

    //Busca inscripciones filtrando por su tipo (INDIVIDUAL o FAMILIAR).
    public List<Inscripcion> findInscripcionesByTipo(TipoInscripcion tipo) {
        try {
            String query = sqlQueries.getProperty("select-inscripciones-by-tipo");
            
            // Usamos el InscripcionRowMapper que ya tienes definido en la clase
            return jdbcTemplate.query(query, new InscripcionRowMapper(), tipo.toString());

        } catch (DataAccessException e) {
            System.err.println("Error al buscar inscripciones del tipo: " + tipo);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
     // Busca una inscripción por su ID.
    
    public Inscripcion findById(int id) {
        String query = sqlQueries.getProperty("select-inscripcion-by-id");
        if (query == null) return null;
        try {
            // Reutilizamos el RowMapper que ya tienes definido en esta clase
            return jdbcTemplate.queryForObject(query, new InscripcionRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    //elimina una inscripcion de la base de datos.
    public boolean deleteInscripcion(int idInscripcion) {
        try {
            String query = sqlQueries.getProperty("delete-inscripcion");
            int rowsAffected = jdbcTemplate.update(query, idInscripcion);
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            System.err.println("Error al eliminar inscripción: " + idInscripcion);
            e.printStackTrace();
            return false;
        }
    }
}
