package helper.dbaccess.dao;

import helper.dbaccess.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Country;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public abstract class DBCountry {
    private static final int maxRetries = 3;

    public static final String schemaName = "client_schedule";
    public static final String countryTableName = "countries";
    public static final String countryIdColumnName = "Country_ID";
    public static final String countryNameColumnName = "Country";

    private static final String selectAllCountriesSQL = String.format("SELECT * FROM %s.%s;", schemaName, countryTableName);
    private static final String updateCountrySQL = String.format("UPDATE %s.%s SET %s = ? WHERE %s = ?", schemaName, countryTableName, countryNameColumnName, countryIdColumnName);
    private static final String findCountrySQL = String.format("SELECT * FROM %s.%s WHERE %s = ?", schemaName, countryTableName, countryIdColumnName);

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

    public static boolean saveUpdatedCountry(Country country) {
        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(updateCountrySQL);
                ps.setString(1, country.getName());
                ps.setInt(2, country.getId());
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

    private static Country buildCountry(ResultSet rs) throws SQLException {
        int countryId = rs.getInt(countryIdColumnName);
        String countryName = rs.getString(countryNameColumnName);
        return new Country(countryId, countryName);
    }
}
