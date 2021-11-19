package helper.dbaccess.dao;

import helper.dbaccess.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Division;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

/**
 * The DAO object/class that is used to perform all database operations pertaining to the Division model.
 * @see Division
 */
public abstract class DBDivision {
    /**
     * The maximum number of times an operation can be retried before giving up.
     */
    private static final int maxRetries = 3;

    /**
     * The name of the schema in the database.
     */
    public static final String schemaName = "client_schedule";
    /**
     * The name of the divisionTable in the database.
     */
    public static final String divisionTableName = "first_level_divisions";
    /**
     * The name of the id column in the database.
     */
    public static final String divisionIdColumnName = "Division_ID";
    /**
     * The name of the country id column in the database.
     */
    public static final String countryIdColumnName = "Country_ID";
    /**
     * The name of the name column in the database.
     */
    public static final String divisionNameColumnName = "Division";
    /**
     * The name of the time of creation column in the database.
     */
    public static final String divisionCreatedByColumnName = "Created_By";
    /**
     * The name of the method of last update in the database.
     */
    public static final String divisionUpdatedByColumnName = "Last_Updated_By";
    /**
     * The name of the time of creation in the database.
     */
    public static final String divisionCreatedAtColumnName = "Create_Date";
    /**
     * The name of the time of last update column in the database.
     */
    public static final String divisionUpdatedAtColumnName = "Last_Update";

    /**
     * The SQL template for grabbing all divisions given a country id.
     */
    private static final String selectDivisionsWithCountryIdSQL = String.format("SELECT * FROM %s.%s WHERE %s = ?;",
            schemaName,
            divisionTableName,
            countryIdColumnName);
    /**
     * The SQL template for finding a single division given just the id.
     */
    private static final String findDivisionSQL = String.format("SELECT * FROM %s.%s WHERE %s = ?",
            schemaName,
            divisionTableName,
            divisionIdColumnName);

    /**
     * Grabs all divisions from the database given a country id.
     * @param selectedCountryId the id of the associated country.
     * @return the divisions.
     */
    public static ObservableList<Division> getDivisionsWithCountryId(Integer selectedCountryId) {
        ObservableList<Division> result = FXCollections.observableArrayList();

        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(selectDivisionsWithCountryIdSQL);
                ps.setInt(1, selectedCountryId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    result.add(buildDivision(rs));
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

        return result;
    }

    /**
     * Given an id, grabs the associated division from the database.
     * @param divisionId the division id.
     * @return the division.
     */
    public static Optional<Division> getDivisionFromId(int divisionId) {
        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(findDivisionSQL);
                ps.setInt(1, divisionId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return Optional.of(buildDivision(rs));
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
     * Given a result set that is in the middle of being used, build a division with the current row.
     * @param rs the ResultSet.
     * @return the new Division model object.
     * @throws SQLException if extracting fields fails.
     */
    private static Division buildDivision(ResultSet rs) throws SQLException {
        int divisionId = rs.getInt(divisionIdColumnName);
        int countryId = rs.getInt(countryIdColumnName);
        String divisionName = rs.getString(divisionNameColumnName);
        String createdBy = rs.getString(divisionCreatedByColumnName);
        String updatedBy = rs.getString(divisionUpdatedByColumnName);
        Timestamp createdAt = rs.getTimestamp(divisionCreatedAtColumnName);
        Timestamp updatedAt = rs.getTimestamp(divisionUpdatedAtColumnName);

        return new Division(divisionId, countryId, divisionName, createdBy, updatedBy, createdAt, updatedAt);
    }
}
