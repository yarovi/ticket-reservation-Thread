package org.example.infraestructure.database;

import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;


public class SchemaInitializer {
    private final DataSource dataSource;

    public SchemaInitializer(DataSource dataSource){

        this.dataSource=dataSource;

    }

    public void initialize(){

        execute("schema.sql");
        execute("data.sql");

    }

    private void execute(String fileName) {

        try (
                Connection connection = dataSource.getConnection();

                Statement statement = connection.createStatement();

                InputStream inputStream =
                        getClass()
                                .getClassLoader()
                                .getResourceAsStream(fileName)

        ) {

            if (inputStream == null) {
                throw new RuntimeException(
                        "File not found: " + fileName
                );
            }


            String sql = new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );


            statement.execute(sql);


        } catch (Exception e) {

            throw new RuntimeException(
                    "Error executing " + fileName,
                    e
            );

        }

    }
}
