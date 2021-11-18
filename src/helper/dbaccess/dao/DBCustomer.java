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

public abstract class DBCustomer {
    private static final int maxRetries = 3;

    private static final String schemaName = "client_schedule";
    private static final String customerTableName = "customers";
    private static final String customerIdColumnName = "Customer_ID";
    private static final String divisionIdColumnName = "Division_ID";
    private static final String customerNameColumnName = "Customer_Name";
    private static final String customerAddressColumnName = "Address";
    private static final String customerPostalCodeColumnName = "Postal_Code";
    private static final String customerPhoneNumberColumnName = "Phone";
    private static final String customerCreatedAtColumnName = "Create_Date";
    private static final String customerUpdatedAtColumnName = "Last_Update";
    private static final String customerCreatedByColumnName = "Created_By";
    private static final String customerUpdatedByColumnName = "Last_Updated_By";

    private static final String selectAllCustomersSQL = String.format("SELECT * FROM %s.%s;", schemaName, customerTableName);
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
    private static final String deleteCustomerSQL = String.format("DELETE FROM %s.%s WHERE %s = ?",
            schemaName,
            customerTableName,
            customerIdColumnName);
    private static final String findCustomerSQL = String.format("SELECT * FROM %s.%s WHERE %s = ?",
            schemaName,
            customerTableName,
            customerIdColumnName);

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
