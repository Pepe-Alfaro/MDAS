package es.uco.pw.grupo12.model.repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import es.uco.pw.grupo12.model.domain.alquiler.Alquiler;
import es.uco.pw.grupo12.model.domain.socio.Socio;

import java.util.ArrayList;
import org.springframework.jdbc.core.RowMapper;
import es.uco.pw.grupo12.model.domain.embarcacion.Embarcacion;

@Repository
public class AlquilerRepository extends AbstractRepository {

    public AlquilerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        setSQLQueriesFileName("db/sql.properties");
    }

    public int save(Alquiler alquiler) {
        try {
            String query = sqlQueries.getProperty("insert-alquiler");
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, alquiler.getSocioTitular().getDni());
                ps.setString(2, alquiler.getEmbarcacion().getMatricula());
                ps.setDate(3, Date.valueOf(alquiler.getFechaInicio()));
                ps.setDate(4, Date.valueOf(alquiler.getFechaFin()));
                ps.setDouble(5, alquiler.getPrecioTotal());
                return ps;
            }, keyHolder);

            return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : -1;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean savePasajeros(int idAlquiler, List<Socio> pasajeros) {
        try {
            String query = sqlQueries.getProperty("insert-socio-alquiler");
            jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, idAlquiler);
                    ps.setString(2, pasajeros.get(i).getDni());
                }
                @Override
                public int getBatchSize() { return pasajeros.size(); }
            });
            return true;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Alquiler> findAll() {
        try {
            String query = sqlQueries.getProperty("select-all-alquileres");
            return jdbcTemplate.query(query, new AlquilerRowMapper());
        } catch (DataAccessException e) {
            System.err.println("Error consultando todos los alquileres");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static class AlquilerRowMapper implements RowMapper<Alquiler> {
        @Override
        public Alquiler mapRow(ResultSet rs, int rowNum) throws SQLException {
            Alquiler alquiler = new Alquiler();
            alquiler.setIdAlquiler(rs.getInt("id_alquiler"));
            alquiler.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
            alquiler.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
            alquiler.setPrecioTotal(rs.getDouble("precio_total"));

            Socio titular = new Socio();
            titular.setDni(rs.getString("dni_socio_titular_fk"));
            alquiler.setSocioTitular(titular);

            Embarcacion embarcacion = new Embarcacion();
            embarcacion.setMatricula(rs.getString("matricula_embarcacion_fk"));
            alquiler.setEmbarcacion(embarcacion);
            
            return alquiler;
        }
    }

    public List<Alquiler> findFuturosAlquileres() {
        String query = sqlQueries.getProperty("select-alquileres-futuros");
        if (query == null) return new ArrayList<>();
        try {
            return jdbcTemplate.query(query, new AlquilerRowMapper());
        } catch (DataAccessException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Alquiler> findFuturosAlquileresParametro(LocalDate fecha) {
        try {
            String query = sqlQueries.getProperty("select-alquileres-futuros-parametro");
            return jdbcTemplate.query(query, new AlquilerRowMapper(), Date.valueOf(fecha));
        } catch (DataAccessException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public Alquiler findAlquilerById(int id) {
        try {
            String query = sqlQueries.getProperty("select-alquiler-by-id");
            return jdbcTemplate.queryForObject(query, new AlquilerRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> findPasajerosByAlquiler(int idAlquiler) {
        try {
            String query = sqlQueries.getProperty("select-pasajeros-by-alquiler");
            return jdbcTemplate.queryForList(query, String.class, idAlquiler);
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    public boolean addPasajero(int idAlquiler, String dniPasajero) {
        try {
            String query = sqlQueries.getProperty("insert-socio-alquiler-single");
            jdbcTemplate.update(query, idAlquiler, dniPasajero);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean removePasajero(int idAlquiler, String dniPasajero) {
        try {
            String query = sqlQueries.getProperty("delete-socio-alquiler-single");
            int rows = jdbcTemplate.update(query, idAlquiler, dniPasajero);
            return rows > 0;
        } catch (DataAccessException e) {
            return false;
        }
    }

    // --- MÉTODO MODIFICADO PARA BORRAR PASAJEROS ANTES ---
    public boolean cancelarAlquiler(int idAlquiler) {
        try {
            // 1. Borrar pasajeros asociados (si los hay)
            String queryPasajeros = sqlQueries.getProperty("delete-pasajeros-alquiler");
            if (queryPasajeros != null) {
                jdbcTemplate.update(queryPasajeros, idAlquiler);
            }

            // 2. Borrar el alquiler
            String queryAlquiler = sqlQueries.getProperty("delete-alquiler");
            int rows = jdbcTemplate.update(queryAlquiler, idAlquiler);
            return rows > 0;
        } catch (DataAccessException e) {
            System.err.println("Error al borrar alquiler " + idAlquiler + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}