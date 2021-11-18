package helper.dbaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DBConnection {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "sqlUser"; // Username
    private static final String password = "Passw0rd!"; // Password
    private static Connection connection;  // Connection Interface

    public static class ConnectionNotOpen extends Exception {}

    public static boolean openConnection() {
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            System.out.println("Connection successful!");
            return true;
        } catch(SQLException|ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed!");
        } catch(SQLException|NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws ConnectionNotOpen {
        try {
            if (connection.isClosed()) {
                throw new ConnectionNotOpen();
            }
        } catch (SQLException|NullPointerException e) {
            e.printStackTrace();
            throw new ConnectionNotOpen();
        }
        return connection;
    };
}
