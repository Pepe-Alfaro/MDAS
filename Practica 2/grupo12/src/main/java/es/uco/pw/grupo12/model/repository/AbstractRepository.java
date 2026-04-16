package es.uco.pw.grupo12.model.repository;


import java.io.IOException;
import java.util.Properties;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class AbstractRepository {

    protected JdbcTemplate jdbcTemplate;
    protected Properties sqlQueries;
    protected String sqlQueriesFileName;

    public void setSQLQueriesFileName(String sqlQueriesFileName) {
        this.sqlQueriesFileName = sqlQueriesFileName;
        createProperties();
    }

    public void createProperties() {
    sqlQueries = new Properties();
    try (var inputStream = getClass().getClassLoader().getResourceAsStream(this.sqlQueriesFileName)) {
        if (inputStream == null) {
            throw new IOException("Archivo no encontrado en el classpath: " + this.sqlQueriesFileName);
        }
        sqlQueries.load(inputStream);
    } catch (IOException e) {
        System.err.println("Error creando el objeto Properties para las queries SQL desde " + this.sqlQueriesFileName);
        e.printStackTrace();
    }
}
}
