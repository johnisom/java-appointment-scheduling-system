package helper.dbaccess.dao;

import helper.dbaccess.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

/**
 * The DAO object/class that is used to perform all database operations pertaining to the User model.
 * @see User
 */
public abstract class DBUser {
    /**
     * The maximum number of times an operation can be retried before giving up.
     */
    private static final int maxRetries = 3;

    /**
     * The name of the schema in the database.
     */
    private static final String schemaName = "client_schedule";
    /**
     * The name of the table in the database.
     */
    private static final String userTableName = "users";
    /**
     * The name of the id column  in the database.
     */
    private static final String userIdColumnName = "User_ID";
    /**
     * The name of the username column in the database.
     */
    private static final String userUsernameColumnName = "User_Name";
    /**
     * The name of the password column in the database.
     */
    private static final String userPasswordColumnName = "Password";
    /**
     * The name of the time of creation in the database.
     */
    private static final String userCreatedAtColumnName = "Create_Date";
    /**
     * The name of the time of last update in the database.
     */
    private static final String userUpdatedAtColumnName = "Last_Update";
    /**
     * The name of the method of creation in the database.
     */
    private static final String userCreatedByColumnName = "Created_By";
    /**
     * The name of the method of last creation in the database.
     */
    private static final String userUpdatedByColumnName = "Last_Updated_By";

    /**
     * The SQL template for grabbing all users.
     */
    private static final String selectAllUsersSQL = String.format("SELECT * FROM %s.%s;", schemaName, userTableName);
    /**
     * The SQL template for finding a single user given just the id.
     */
    private static final String findUserSQL = String.format("SELECT * FROM %s.%s WHERE %s = ? LIMIT 1;",
            schemaName,
            userTableName,
            userIdColumnName);
    /**
     * The SQL template for finding a single user given username and password.
     */
    private static final String findUserByUsernameAndPasswordSQL = String.format("SELECT * FROM %s.%s WHERE %s = ? AND %s = ? LIMIT 1;",
            schemaName,
            userTableName,
            userUsernameColumnName,
            userPasswordColumnName);


    /**
     * Grabs all users from the database.
     * @return the users.
     */
    public static ObservableList<User> getAllUsers() {
        ObservableList<User> allUsers = FXCollections.observableArrayList();

        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(selectAllUsersSQL);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    allUsers.add(buildUser(rs));
                }

                count = maxRetries;
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (DBConnection.ConnectionNotOpen e) {
                e.printStackTrace();
                if (DBConnection.openConnection()) {
                    continue;
                }

                count = maxRetries;
            }
        }

        return allUsers;
    }

    /**
     * Given an id, grabs the associated user from the database.
     * @param userId the user id.
     * @return the user.
     */
    public static Optional<User> getUserFromId(int userId) {
        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(findUserSQL);
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return Optional.of(buildUser(rs));
                } else {
                    count = maxRetries;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (DBConnection.ConnectionNotOpen e) {
                e.printStackTrace();
                if (DBConnection.openConnection()) {
                    continue;
                }
                count = maxRetries;
            }
        }
        return Optional.empty();
    }

    /**
     * Given a username and a password, grabs the associated user from the database.
     * @param username the user username.
     * @param password the user password.
     * @return the user.
     */
    public static Optional<User> getUserFromUsernameAndPassword(String username, String password) {
        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(findUserByUsernameAndPasswordSQL);
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return Optional.of(buildUser(rs));
                } else {
                    count = maxRetries;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (DBConnection.ConnectionNotOpen e) {
                e.printStackTrace();
                if (DBConnection.openConnection()) {
                    continue;
                }

                count = maxRetries;
            }
        }
        return Optional.empty();
    }

    /**
     * Given a result set that is in the middle of being used, build a user with the current row.
     * @param rs the ResultSet.
     * @return the new User model object.
     * @throws SQLException if extracting fields fails.
     */
    private static User buildUser(ResultSet rs) throws SQLException {
        int userId = rs.getInt(userIdColumnName);
        String userUsername = rs.getString(userUsernameColumnName);
        String userPassword = rs.getString(userPasswordColumnName);
        Timestamp userCreatedAt = rs.getTimestamp(userCreatedAtColumnName);
        Timestamp userUpdatedAt = rs.getTimestamp(userUpdatedAtColumnName);
        String userCreatedBy = rs.getString(userCreatedByColumnName);
        String userUpdatedBy = rs.getString(userUpdatedByColumnName);
        return new User(userId,
                userUsername,
                userPassword,
                userCreatedAt,
                userUpdatedAt,
                userCreatedBy,
                userUpdatedBy);
    }
}
