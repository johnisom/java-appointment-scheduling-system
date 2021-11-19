package helper.dbaccess.dao;

import helper.dbaccess.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

/**
 * The DAO object/class that is used to perform all database operations pertaining to the Customer model.
 * @see Customer
 */
public abstract class DBCustomer {
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
    private static final String customerTableName = "customers";
    /**
     * The name of the id column in the database.
     */
    private static final String customerIdColumnName = "Customer_ID";
    /**
     * The name of the division id column in the database.
     */
    private static final String divisionIdColumnName = "Division_ID";
    /**
     * The name of the name column in the database.
     */
    private static final String customerNameColumnName = "Customer_Name";
    /**
     * The name of the address column in the database.
     */
    private static final String customerAddressColumnName = "Address";
    /**
     * The name of the postal code column in the database.
     */
    private static final String customerPostalCodeColumnName = "Postal_Code";
    /**
     * The name of the phone number column in the database.
     */
    private static final String customerPhoneNumberColumnName = "Phone";
    /**
     * The name of the created at column in the database.
     */
    private static final String customerCreatedAtColumnName = "Create_Date";
    /**
     * The name of the updated at column in the database.
     */
    private static final String customerUpdatedAtColumnName = "Last_Update";
    /**
     * The name of the created by column in the database.
     */
    private static final String customerCreatedByColumnName = "Created_By";
    /**
     * The name of the updated by column in the database.
     */
    private static final String customerUpdatedByColumnName = "Last_Updated_By";

    /**
     * The SQL template for grabbing all customers.
     */
    private static final String selectAllCustomersSQL = String.format("SELECT * FROM %s.%s;", schemaName, customerTableName);
    /**
     * The SQL template for updating a single customer.
     */
    private static final String updateCustomerSQL = String.format("UPDATE %s.%s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = NOW(), %s = ?, %s = 'desktop-app' WHERE %s = ?;",
            schemaName,
            customerTableName,
            divisionIdColumnName,
            customerNameColumnName,
            customerAddressColumnName,
            customerPostalCodeColumnName,
            customerPhoneNumberColumnName,
            customerCreatedAtColumnName,
            customerUpdatedAtColumnName,
            customerCreatedByColumnName,
            customerUpdatedByColumnName,
            customerIdColumnName);
    /**
     * The SQL template for creating a single customer.
     */
    private static final String createCustomerSQL = String.format("INSERT INTO %s.%s(%s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, NOW(), NOW(), 'desktop-app', 'desktop-app');",
            schemaName,
            customerTableName,
            divisionIdColumnName,
            customerNameColumnName,
            customerAddressColumnName,
            customerPostalCodeColumnName,
            customerPhoneNumberColumnName,
            customerCreatedAtColumnName,
            customerUpdatedAtColumnName,
            customerCreatedByColumnName,
            customerUpdatedByColumnName);
    /**
     * The SQL template for deleting a single customer.
     */
    private static final String deleteCustomerSQL = String.format("DELETE FROM %s.%s WHERE %s = ?",
            schemaName,
            customerTableName,
            customerIdColumnName);
    /**
     * The SQL template for finding a single customer given just the id.
     */
    private static final String findCustomerSQL = String.format("SELECT * FROM %s.%s WHERE %s = ?",
            schemaName,
            customerTableName,
            customerIdColumnName);

    /**
     * Grabs all customers from the database.
     * @return the customers.
     */
    public static ObservableList<Customer> getAllCustomers() {
        ObservableList<Customer> allCustomers = FXCollections.observableArrayList();

        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(selectAllCustomersSQL);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    allCustomers.add(buildCustomer(rs));
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

        return allCustomers;
    }

    /**
     * Creates a customer record in the database given a customer model.
     * @param customer the customer with the fields populated.
     * @return a new Customer model that has the latest after the creation.
     */
    public static Optional<Customer> createCustomer(Customer customer) {
        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(createCustomerSQL, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setInt(1, customer.getDivisionId());
                ps.setString(2, customer.getName());
                ps.setString(3, customer.getAddress());
                ps.setString(4, customer.getPostalCode());
                ps.setString(5, customer.getPhoneNumber());
                if (ps.executeUpdate() == 1) {
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        return getCustomerFromId(rs.getInt(1));
                    }
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
        return Optional.empty();
    }

    /**
     * Updates a customer record in the database given a customer model.
     * @param customer the customer with the fields populated.
     * @return true if the customer was updated, false if there was an issue.
     */
    public static boolean updateCustomer(Customer customer) {
        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(updateCustomerSQL);
                ps.setInt(1, customer.getDivisionId());
                ps.setString(2, customer.getName());
                ps.setString(3, customer.getAddress());
                ps.setString(4, customer.getPostalCode());
                ps.setString(5, customer.getPhoneNumber());
                ps.setTimestamp(6, Timestamp.from(customer.getCreatedAt() == null ? Instant.now() : customer.getCreatedAt()));
                ps.setString(7, customer.getCreatedBy());
                ps.setInt(8, customer.getId());
                return ps.executeUpdate() == 1;
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

    /**
     * Deletes a customer record from the database given a customer id.
     * @param customerId the customer id.
     * @return true if the customer was deleted, false if there was an issue.
     */
    public static boolean deleteCustomerFromId(int customerId) {
        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(deleteCustomerSQL);
                ps.setInt(1, customerId);

                return ps.executeUpdate() == 1;
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
        return false;
    }

    /**
     * Given an id, grabs the associated customer from the database.
     * @param customerId the customer id.
     * @return the customer.
     */
    public static Optional<Customer> getCustomerFromId(int customerId) {
        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(findCustomerSQL);
                ps.setInt(1, customerId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return Optional.of(buildCustomer(rs));
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
     * Given a result set that is in the middle of being used, build a customer with the current row.
     * @param rs the ResultSet.
     * @return the new Customer model object.
     * @throws SQLException if extracting fields fails.
     */
    private static Customer buildCustomer(ResultSet rs) throws SQLException {
        int customerId = rs.getInt(customerIdColumnName);
        int divisionId = rs.getInt(divisionIdColumnName);
        String customerName = rs.getString(customerNameColumnName);
        String customerAddress = rs.getString(customerAddressColumnName);
        String customerPostalCode = rs.getString(customerPostalCodeColumnName);
        String customerPhoneNumber = rs.getString(customerPhoneNumberColumnName);
        Timestamp customerCreatedAt = rs.getTimestamp(customerCreatedAtColumnName);
        Timestamp customerUpdatedAt = rs.getTimestamp(customerUpdatedAtColumnName);
        String customerCreatedBy = rs.getString(customerCreatedByColumnName);
        String customerUpdatedBy = rs.getString(customerUpdatedByColumnName);
        return new Customer(customerId,
                divisionId,
                customerName,
                customerAddress,
                customerPostalCode,
                customerPhoneNumber,
                customerCreatedAt,
                customerUpdatedAt,
                customerCreatedBy,
                customerUpdatedBy);
    }
}
