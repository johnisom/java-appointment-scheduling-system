package helper.dbaccess.dao;

import helper.dbaccess.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Country;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * The DAO object/class that is used to perform all database operations pertaining to the Country model.
 * @see Country
 */
public abstract class DBCountry {
    /**
     * The maximum number of times an operation can be retried before giving up.
     */
    private static final int maxRetries = 3;

    /**
     * The name of the schema in the database.
     */
    public static final String schemaName = "client_schedule";
    /**
     * The name of the country table in the database.
     */
    public static final String countryTableName = "countries";
    /**
     * The name of the country id column in the database.
     */
    public static final String countryIdColumnName = "Country_ID";
    /**
     * The name of the country name column in the database.
     */
    public static final String countryNameColumnName = "Country";

    /**
     * The SQL template for grabbing all countries.
     */
    private static final String selectAllCountriesSQL = String.format("SELECT * FROM %s.%s;", schemaName, countryTableName);
    /**
     * The SQL template for finding a single country given just the id.
     */
    private static final String findCountrySQL = String.format("SELECT * FROM %s.%s WHERE %s = ?", schemaName, countryTableName, countryIdColumnName);

    /**
     * Grabs all countries from the database.
     * @return the countries.
     */
    public static ObservableList<Country> getAllCountries() {
        ObservableList<Country> allCountries = FXCollections.observableArrayList();

        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(selectAllCountriesSQL);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    allCountries.add(buildCountry(rs));
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

        return allCountries;
    }

    /**
     * Given an id, grabs the associated country from the database.
     * @param countryId the country id.
     * @return the country.
     */
    public static Optional<Country> getCountryFromId(int countryId) {
        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(findCountrySQL);
                ps.setInt(1, countryId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return Optional.of(buildCountry(rs));
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
                count  = maxRetries;
            }
        }
        return Optional.empty();
    }

    /**
     * Given a result set that is in the middle of being used, build a country with the current row.
     * @param rs the ResultSet.
     * @return the new Country model object.
     * @throws SQLException if extracting fields fails.
     */
    private static Country buildCountry(ResultSet rs) throws SQLException {
        int countryId = rs.getInt(countryIdColumnName);
        String countryName = rs.getString(countryNameColumnName);
        return new Country(countryId, countryName);
    }
}
