package helper.dbaccess.dao;

import helper.dbaccess.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Contact;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * The DAO object/class that is used to perform all database operations pertaining to the Contact model.
 * @see Contact
 */
public abstract class DBContact {
    /**
     * The maximum number of times an operation can be retried before giving up.
     */
    private static final int maxRetries = 3;

    /**
     * The name of the schema in the database.
     */
    public static final String schemaName = "client_schedule";
    /**
     * The name of the table in the database.
     */
    public static final String contactTableName = "contacts";
    /**
     * The name of the contact id column in the database.
     */
    public static final String contactIdColumnName = "Contact_ID";
    /**
     * The name of the contact name column in the database.
     */
    public static final String contactNameColumnName = "Contact_Name";
    /**
     * The name of the contact email column in the database.
     */
    public static final String contactEmailColumnName = "Email";

    /**
     * The SQL template for grabbing all contacts.
     */
    private static final String selectAllContactsSQL = String.format("SELECT * FROM %s.%s", schemaName, contactTableName);
    /**
     * The SQL template for finding a single contact given just the id.
     */
    private static final String findContactSQL = String.format("SELECT * FROM %s.%s WHERE %s = ?", schemaName, contactTableName, contactIdColumnName);

    /**
     * Grabs all contacts from the database.
     * @return the contacts.
     */
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

    /**
     * Given an id, grabs the associated contact from the database.
     * @param id the contact id.
     * @return the contact.
     */
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

    /**
     * Given a result set that is in the middle of being used, build a contact with the current row.
     * @param rs the ResultSet.
     * @return the new Contact model object.
     * @throws SQLException if extracting fields fails.
     */
    private static Contact buildContact(ResultSet rs) throws SQLException {
        int contactId = rs.getInt(contactIdColumnName);
        String contactName = rs.getString(contactNameColumnName);
        String contactEmail = rs.getString(contactEmailColumnName);
        return new Contact(contactId, contactName, contactEmail);
    }
}
