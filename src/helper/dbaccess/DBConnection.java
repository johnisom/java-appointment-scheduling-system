package helper.dbaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * An abstract class that consists of only static members and is not meant to be instantiated.
 * This class is used to interface with the MYSQL database for this application.
 * It can open and close a connection, as well as return the connection for the other helper.dbaccess classes.
 * @see helper.dbaccess.dao.DBAppointment
 * @see helper.dbaccess.dao.DBContact
 * @see helper.dbaccess.dao.DBCountry
 * @see helper.dbaccess.dao.DBCustomer
 * @see helper.dbaccess.dao.DBDivision
 * @see helper.dbaccess.dao.DBUser
 */
public abstract class DBConnection {
    /**
     * The protocol of the MYSQL database.
     */
    private static final String protocol = "jdbc";
    /**
     * The vendor of the MYSQL database.
     */
    private static final String vendor = ":mysql:";
    /**
     * The location of the MYSQL database.
     */
    private static final String location = "//localhost/";
    /**
     * The ame of the MYSQL database.
     */
    private static final String databaseName = "client_schedule";
    /**
     * The JDBC url of the MYSQL database.
     */
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    /**
     * The driver name of the MYSQL database.
     */
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    /**
     * The username of the MYSQL database user.
     */
    private static final String userName = "sqlUser"; // Username
    /**
     * The password of the MYSQL database user.
     */
    private static final String password = "Passw0rd!"; // Password
    /**
     * The Connection that is connected to the database and is used to perform all database operations.
     */
    private static Connection connection;  // Connection Interface

    /**
     * The exception that is raised when the connection is not open when getConnection is clled
     * @see #getConnection()
     */
    public static class ConnectionNotOpen extends Exception {}

    /**
     * Opens the connection, and returns true if successful, false otherwise.
     * @return whether the connection was successfully opened.
     */
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

    /**
     * Closes the connection.
     */
    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed!");
        } catch(SQLException|NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Grabs the connection so that work can be done.
     * @return the Connection to the MYSQL database.
     * @throws ConnectionNotOpen if the connection has not yet been opened.
     */
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
    }
}
