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

import es.uco.pw.grupo12.model.domain.socio.Socio;

@Repository
public class SocioRepository extends AbstractRepository {

  public SocioRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    setSQLQueriesFileName("db/sql.properties");
  }

  
  // Busca un socio por su DNI.
  
  public Socio findSocioByDni(String dni) {
    try {
      String query = sqlQueries.getProperty("select-socio-byDni");
      return jdbcTemplate.queryForObject(query, new SocioRowMapper(), dni);
    } catch (EmptyResultDataAccessException e) {
      // Esto es normal si el socio no existe, no es un error.
      return null;
    } catch (DataAccessException e) {
      System.err.println("Error al buscar socio por DNI: " + dni);
      e.printStackTrace();
      return null;
    }
  }

  
  // Guarda un nuevo socio en la base de datos.
 
  public boolean saveSocio(Socio socio) {
    try {
      String query = sqlQueries.getProperty("insert-socio");

    
      Integer inscripcionFk = (socio.getIdInscripcionFk() == -1) ? null : socio.getIdInscripcionFk();

      int rowsAffected = jdbcTemplate.update(query,
          socio.getDni(),
          socio.getNombre(),
          socio.getApellidos(),
          socio.getFechaNacimiento(),
          socio.getDireccion(),
          socio.getFechaInscripcion(),
          socio.getTituloPatron(),
          inscripcionFk 
      );
      return rowsAffected > 0;
    } catch (DataAccessException e) {
      System.err.println("Error al insertar socio: " + socio.getDni());
      e.printStackTrace();
      return false;
    }
  }

  
  // Actualiza el campo id_inscripcion_fk de un socio existente.

  
  public boolean updateInscripcionFk(String dniSocio, int idInscripcion) {
    try {
      String query = sqlQueries.getProperty("update-socio-asignarInscripcion");
      int rowsAffected = jdbcTemplate.update(query, idInscripcion, dniSocio);
      return rowsAffected > 0;
    } catch (DataAccessException e) {
      System.err.println("Error al actualizar FK de inscripción para socio: " + dniSocio);
      e.printStackTrace();
      return false;
    }
  }

  
   // Busca todos los socios (familiares) asociados a una inscripción.
  
  public List<Socio> findAllByInscripcionId(int inscripcionId) {
      try {
          String query = sqlQueries.getProperty("select-sociosByInscripcionId");
          if (query == null) {
               System.err.println("La query 'select-sociosByInscripcionId' no se encontró en sql.properties.");
               return new ArrayList<>();
          }
          return jdbcTemplate.query(query, new SocioRowMapper(), inscripcionId);

      } catch (DataAccessException e) {
          System.err.println("Error al buscar socios por ID de inscripción: " + inscripcionId);
          return new ArrayList<>(); // Devuelve lista vacía en caso de error
      }
  }

  
  // Actualiza el estado del título de patrón de un socio a verdadero.
 
  public boolean updateTituloPatron(String dniSocio) {
    try {
      String query = sqlQueries.getProperty("update-socio-tituloPatron");
      int rowsAffected = jdbcTemplate.update(query, dniSocio);
      return rowsAffected > 0;
    } catch (DataAccessException e) {
      System.err.println("Error al actualizar el título de patrón para: " + dniSocio);
      e.printStackTrace();
      return false;
    }
  }

  
  // Busca todos los socios y determina si son titulares.
 
  public List<Socio> findAllSociosWithTitularStatus() {
      try {
          String query = sqlQueries.getProperty("select-all-socios-with-titular-status");
          if (query == null) {
               System.err.println("La query 'select-all-socios-with-titular-status' no se encontró en sql.properties.");
               return new ArrayList<>();
          }
          // Usamos el NUEVO RowMapper
          return jdbcTemplate.query(query, new SocioWithTitularRowMapper());
  
      } catch (DataAccessException e) {
          System.err.println("Error al buscar todos los socios con estado de titular");
          e.printStackTrace();
          return new ArrayList<>(); 
      }
  }

// Busca todos los socios que tienen el título de patrón.
     
    public List<Socio> findSociosConTitulo() {
        try {
            String query = sqlQueries.getProperty("select-socios-con-titulo");
            return jdbcTemplate.query(query, new SocioRowMapper());
        } catch (DataAccessException e) {
            System.err.println("Error al buscar socios con título de patrón");
            return new ArrayList<>();
        }
    }


  private static class SocioRowMapper implements RowMapper<Socio> {
    @Override
    public Socio mapRow(ResultSet rs, int rowNum) throws SQLException {
      Socio socio = new Socio(
          rs.getString("dni"),
          rs.getString("nombre"),
          rs.getString("apellidos"),
          rs.getDate("fecha_nacimiento").toLocalDate(),
          rs.getString("direccion"),
          rs.getDate("fecha_inscripcion").toLocalDate() 
      );
      socio.setTituloPatron(rs.getBoolean("tiene_titulo_patron"));
      socio.setIdInscripcionFk(rs.getInt("id_inscripcion_fk"));
      
      // Corrección por si el getInt devuelve 0 para un NULL de SQL
      if (rs.wasNull()) {
        socio.setIdInscripcionFk(-1);
      }
      return socio;
    }
  }


  private static class SocioWithTitularRowMapper implements RowMapper<Socio> {
      @Override
      public Socio mapRow(ResultSet rs, int rowNum) throws SQLException {
          
          Socio socio = new Socio(
              rs.getString("dni"),
              rs.getString("nombre"),
              rs.getString("apellidos"),
              rs.getDate("fecha_nacimiento").toLocalDate(),
              rs.getString("direccion"),
              rs.getDate("fecha_inscripcion").toLocalDate() 
          );
          socio.setTituloPatron(rs.getBoolean("tiene_titulo_patron"));
          socio.setIdInscripcionFk(rs.getInt("id_inscripcion_fk"));
          
          if (rs.wasNull()) {
            socio.setIdInscripcionFk(-1);
          }

       
          rs.getString("dni_socio_titular_fk"); // Leemos la columna del JOIN
          socio.setEsTitular(!rs.wasNull()); // Si la columna NO era NULL, es titular
          
          return socio;
      }
  }

  //update socio para api
  
    public boolean updateSocio(Socio socio) {
        try {
            // Buscamos la propiedad update-socio en sql-properties
            String query = sqlQueries.getProperty("update-socio");
            if (query == null) {
                System.err.println("La query 'update-socio' no se encontró en sql.properties.");
                return false;
            }

            Integer idInscripcion = (socio.getIdInscripcionFk() == -1) ? null : socio.getIdInscripcionFk();

            int rowsAffected = jdbcTemplate.update(query,
                    socio.getNombre(),
                    socio.getApellidos(),
                    socio.getFechaNacimiento(),
                    socio.getDireccion(),
                    socio.getFechaInscripcion(),
                    socio.getTituloPatron(),
                    idInscripcion,
                    socio.getDni() // El DNI es el criterio (WHERE dni = ?)
            );

            return rowsAffected > 0;
        } catch (DataAccessException e) {
            System.err.println("Error al actualizar socio con DNI: " + socio.getDni());
            e.printStackTrace();
            return false;
        }
    }

  
    public boolean desvincularSocio(String dniSocio) {
        try {
            String query = sqlQueries.getProperty("update-socio-desvincular");
            int rowsAffected = jdbcTemplate.update(query, dniSocio);
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            System.err.println("Error al desvincular socio: " + dniSocio);
            e.printStackTrace();
            return false;
        }
    }

    
     // Desvincula a TODOS los socios de una inscripción específica.
     // Se usa antes de borrar la inscripción para no perder a los socios.
     
    public boolean desvincularTodosDeInscripcion(int idInscripcion) {
        try {
            String query = sqlQueries.getProperty("update-socios-desvincular-todos");
  
            jdbcTemplate.update(query, idInscripcion);
            return true;
        } catch (DataAccessException e) {
            System.err.println("Error al desvincular socios de la inscripción: " + idInscripcion);
            e.printStackTrace();
            return false;
        }
    }

    //Elimina un socio de la base de datos
    public boolean deleteSocio(String dni) {
        try {
            String query = sqlQueries.getProperty("delete-socio");
            if (query == null) {
                System.err.println("La query 'delete-socio' no se encontró en sql.properties.");
                return false;
            }
            
            int rowsAffected = jdbcTemplate.update(query, dni);
            return rowsAffected > 0;
            
        } catch (DataAccessException e) {
            System.err.println("Error al eliminar socio con DNI: " + dni);
            e.printStackTrace();
            return false;
        }
    }
}