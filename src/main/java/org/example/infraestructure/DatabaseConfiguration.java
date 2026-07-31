package org.example.infraestructure;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

public class DatabaseConfiguration {
    private DatabaseConfiguration() {
    }

    public static DataSource dataSource() {

        JdbcDataSource ds = new JdbcDataSource();

        ds.setURL(
                "jdbc:h2:mem:reservationdb;" +
                        "DB_CLOSE_DELAY=-1;" +
                        "MODE=PostgreSQL"
        );

        ds.setUser("sa");

        ds.setPassword("");

        return ds;

    }
}
