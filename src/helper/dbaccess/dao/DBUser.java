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

public abstract class DBUser {
    private static final int maxRetries = 3;

    private static final String schemaName = "client_schedule";
    private static final String userTableName = "users";
    private static final String userIdColumnName = "User_ID";
    private static final String userUsernameColumnName = "User_Name";
    private static final String userPasswordColumnName = "Password";
    private static final String userCreatedAtColumnName = "Create_Date";
    private static final String userUpdatedAtColumnName = "Last_Update";
    private static final String userCreatedByColumnName = "Created_By";
    private static final String userUpdatedByColumnName = "Last_Updated_By";

    private static final String selectAllUsersSQL = String.format("SELECT * FROM %s.%s;", schemaName, userTableName);
    private static final String updateUserSQL = String.format("UPDATE %s.%s SET %s = ?, %s = ?, %s = ?, %s = NOW(), %s = ?, %s = 'desktop-app' WHERE %s = ?;",
            schemaName,
            userTableName,
            userUsernameColumnName,
            userPasswordColumnName,
            userCreatedAtColumnName,
            userUpdatedAtColumnName,
            userCreatedByColumnName,
            userUpdatedByColumnName,
            userIdColumnName);
    private static final String findUserSQL = String.format("SELECT * FROM %s.%s WHERE %s = ? LIMIT 1;",
            schemaName,
            userTableName,
            userIdColumnName);
    private static final String findUserByUsernameAndPasswordSQL = String.format("SELECT * FROM %s.%s WHERE %s = ? AND %s = ? LIMIT 1;",
            schemaName,
            userTableName,
            userUsernameColumnName,
            userPasswordColumnName);


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

    public static boolean updateUser(User user) {
        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(updateUserSQL);
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
//                ps.setTimestamp(3, Timestamp.from(user.getCreatedAt()));
                ps.setTimestamp(3, Timestamp.from(user.getUpdatedAt()));
//                ps.setString(5, user.getCreatedBy());
                ps.setString(4, user.getUpdatedBy());
                ps.setInt(5, user.getId());
                return ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (DBConnection.ConnectionNotOpen e) {
                e.printStackTrace();
                if (DBConnection.openConnection()) {
                    continue;
                }

                return false;
            }
        }
        return false;
    }

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
