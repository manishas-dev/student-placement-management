
package com.placement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DBConnection {

    public static Connection getConnection() throws SQLException {

        Properties properties = new Properties();

        try (FileInputStream input =
                new FileInputStream("config.properties")) {

            properties.load(input);

        } catch (IOException e) {
            throw new SQLException(
                    "Unable to load config.properties", e);
        }

        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

        return DriverManager.getConnection(url, user, password);
    }
}
