package org.example.infraestructure.config;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory {
    private final DataSource dataSource;

    public ConnectionFactory(DataSource dataSource) {

        this.dataSource = dataSource;

    }

    public Connection getConnection() throws SQLException {

        return dataSource.getConnection();

    }
}
