package com.park.ease.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * DBConnectionUtil provides a centralized database connection utility
 * for the ParkEase system.
 * 
 * Reads database configuration from the db.properties file located
 * in the classpath (src/main/resources). Loads the JDBC driver once
 * during class initialization using a static block.
 * 
 * Required properties in db.properties:
 *   db.driver   - JDBC driver class name
 *   db.url      - Database connection URL
 *   db.user     - Database username
 *   db.password - Database password
 */
public class DBConnectionUtil {

    // Properties object holding database configuration values
    private static Properties props = new Properties();

    static {
        // Load database properties and register JDBC driver at class load time
        try (InputStream is = DBConnectionUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                props.load(is);
                Class.forName(props.getProperty("db.driver"));
            } else {
                System.err.println("DBConnectionUtil: db.properties file not found in classpath.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("DBConnectionUtil: JDBC driver not found - " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("DBConnectionUtil: Failed to load database properties - " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates and returns a new database connection using the configured properties.
     * Caller is responsible for closing the connection after use.
     * 
     * @return a new SQL Connection object
     * @throws java.sql.SQLException if the connection cannot be established
     */
    public static Connection getConnection() throws java.sql.SQLException {
        return DriverManager.getConnection(
            props.getProperty("db.url"),
            props.getProperty("db.user"),
            props.getProperty("db.password")
        );
    }
}