package helper.dbaccess.dao;

import helper.dbaccess.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Contact;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public abstract class DBContact {
    private static final int maxRetries = 3;

    public static final String schemaName = "client_schedule";
    public static final String contactTableName = "contacts";
    public static final String contactIdColumnName = "Contact_ID";
    public static final String contactNameColumnName = "Contact_Name";
    public static final String contactEmailColumnName = "Email";

    private static final String selectAllContactsSQL = String.format("SELECT * FROM %s.%s", schemaName, contactTableName);
    private static final String findContactSQL = String.format("SELECT * FROM %s.%s WHERE %s = ?", schemaName, contactTableName, contactIdColumnName);
    private static final String updateContactSQL = String.format("UPDATE %s.%s SET %s = ?, %s = ? WHERE %s = ?", schemaName, contactTableName, contactNameColumnName, contactEmailColumnName, contactIdColumnName);

    public static ObservableList<Contact> getAllContacts() {
        ObservableList<Contact> allContacts = FXCollections.observableArrayList();

        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(selectAllContactsSQL);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    allContacts.add(buildContact(rs));
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

        return allContacts;
    }

    public static boolean saveUpdatedContact(Contact contact) {
        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(updateContactSQL);
                ps.setString(1, contact.getName());
                ps.setString(2, contact.getEmail());
                ps.setInt(3, contact.getId());
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

    public static Optional<Contact> getContactFromId(int id) {
        for(int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(findContactSQL);
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return Optional.of(buildContact(rs));
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

    private static Contact buildContact(ResultSet rs) throws SQLException {
        int contactId = rs.getInt(contactIdColumnName);
        String contactName = rs.getString(contactNameColumnName);
        String contactEmail = rs.getString(contactEmailColumnName);
        return new Contact(contactId, contactName, contactEmail);
    }
}
