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
import es.uco.pw.grupo12.model.domain.reserva.Reserva;
import es.uco.pw.grupo12.model.domain.socio.Socio;

@Repository
public class ReservaRepository extends AbstractRepository {

    public ReservaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        setSQLQueriesFileName("db/sql.properties");
    }

    public boolean isReservada(String matricula, LocalDate fecha) {
        String query = sqlQueries.getProperty("select-reserva-by-matricula-and-fecha");
        if (query == null) return true;
        try {
            jdbcTemplate.queryForObject(query, Integer.class, matricula, fecha);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (DataAccessException e) {
            return true;
        }
    }

    public boolean saveReserva(Reserva reserva) {
        String query = sqlQueries.getProperty("insert-reserva");
        if (query == null) return false;
        try {
            int rowsAffected = jdbcTemplate.update(query,
                    reserva.getSocioSolicitante().getDni(),
                    reserva.getEmbarcacion().getMatricula(),
                    reserva.getFechaActividad(),
                    reserva.getPlazasSolicitadas(),
                    reserva.getDescripcion(),
                    reserva.getPrecioTotal());
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Reserva> findAll() {
        String query = sqlQueries.getProperty("select-all-reservas");
        if (query == null) return new ArrayList<>();
        try {
            return jdbcTemplate.query(query, new ReservaRowMapper());
        } catch (DataAccessException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Reserva> findByFechaPosterior(LocalDate fecha) {
        String query = sqlQueries.getProperty("select-reservas-posteriores-fecha");
        if (query == null) return new ArrayList<>();
        try {
            return jdbcTemplate.query(query, new ReservaRowMapper(), fecha);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Reserva findById(int id) {
        String query = sqlQueries.getProperty("select-reserva-by-id");
        if (query == null) return null;
        try {
            return jdbcTemplate.queryForObject(query, new ReservaRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean existeReservaEnFecha(String matricula, LocalDate fecha) {
        String query = sqlQueries.getProperty("check-reserva-embarcacion-disponible");
        try {
            jdbcTemplate.queryForObject(query, Integer.class, matricula, fecha);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public boolean updateFechaReserva(int idReserva, LocalDate nuevaFecha) {
        String query = sqlQueries.getProperty("update-reserva-fecha");
        try {
            return jdbcTemplate.update(query, nuevaFecha, idReserva) > 0;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- CORREGIDO: Ahora acepta el precio ---
    public boolean updateDatosReserva(int idReserva, String descripcion, int plazas, double nuevoPrecio) {
        String query = sqlQueries.getProperty("update-reserva-datos");
        if (query == null) return false;
        try {
            return jdbcTemplate.update(query, descripcion, plazas, nuevoPrecio, idReserva) > 0;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteReserva(int idReserva) {
        String query = sqlQueries.getProperty("delete-reserva");
        try {
            return jdbcTemplate.update(query, idReserva) > 0;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static class ReservaRowMapper implements RowMapper<Reserva> {
        @Override
        public Reserva mapRow(ResultSet rs, int rowNum) throws SQLException {
            Reserva reserva = new Reserva();
            reserva.setIdReserva(rs.getInt("id_reserva"));
            reserva.setFechaActividad(rs.getDate("fecha_actividad").toLocalDate());
            reserva.setDescripcion(rs.getString("descripcion"));
            reserva.setPlazasSolicitadas(rs.getInt("plazas_solicitadas")); 
            // Si la columna precio_total existe en tu tabla, podrías leerla aquí también:
            // reserva.setPrecioTotal(rs.getDouble("precio_total"));
            
            Socio socio = new Socio();
            socio.setDni(rs.getString("dni_socio_solicitante_fk"));
            reserva.setSocioSolicitante(socio); 

            Embarcacion embarcacion = new Embarcacion();
            embarcacion.setMatricula(rs.getString("matricula_embarcacion_fk"));
            reserva.setEmbarcacion(embarcacion);

            return reserva;
        }
    }
}