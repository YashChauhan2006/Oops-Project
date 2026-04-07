package com.ems.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class DBConnection {
    private static Connection connection = null;
    private static Properties props = new Properties();

    static {
        try {
            // Loading database properties
            InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db/config.properties");
            if (input == null) {
                input = new FileInputStream("db/config.properties");
            }
            props.load(input);
            
            // Load MySQL Driver - Systematically
            System.out.println("LOG: Probing for MySQL Driver...");
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("LOG: Modern Driver class 'com.mysql.cj.jdbc.Driver' found.");
            } catch (ClassNotFoundException e) {
                System.out.println("LOG: Modern Driver class not found, trying legacy 'com.mysql.jdbc.Driver'...");
                Class.forName("com.mysql.jdbc.Driver");
                System.out.println("LOG: Legacy Driver class found.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("FATAL: MySQL Connector JAR is completely missing from Classpath!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("FATAL: Could not load config.properties! Check file location.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String pass = props.getProperty("db.password");

            System.out.println("DEBUG: Connecting to -> " + url);
            System.out.println("DEBUG: Using Username -> " + user);

            if (url == null || url.isEmpty()) {
                throw new SQLException("CRITICAL ERROR: Database URL is empty! Check your config.properties file location.");
            }

            try {
                connection = DriverManager.getConnection(url, user, pass);
                System.out.println("SUCCESS: Database connected!");
            } catch (SQLException e) {
                System.err.println("ERROR: Could not establish connection to MySQL.");
                throw e;
            }
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
