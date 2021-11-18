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

public abstract class DBDivision {
    private static final int maxRetries = 3;

    public static final String schemaName = "client_schedule";
    public static final String divisionTableName = "first_level_divisions";
    public static final String divisionIdColumnName = "Division_ID";
    public static final String countryIdColumnName = "Country_ID";
    public static final String divisionNameColumnName = "Division";
    public static final String divisionCreatedByColumnName = "Created_By";
    public static final String divisionUpdatedByColumnName = "Last_Updated_By";
    public static final String divisionCreatedAtColumnName = "Create_Date";
    public static final String divisionUpdatedAtColumnName = "Last_Update";

    private static final String selectAllDivisionsSQL = String.format("SELECT * FROM %s.%s;", schemaName, divisionTableName);
    private static final String selectDivisionsWithCountryIdSQL = String.format("SELECT * FROM %s.%s WHERE %s = ?;",
            schemaName,
            divisionTableName,
            countryIdColumnName);
    private static final String updateDivisionSQL = String.format("UPDATE %s.%s SET %s = ?, %s = ?, %s = ?, %s = 'desktop-app', %s = ?, %s = NOW() WHERE %s = ?",
            schemaName,
            divisionTableName,
            countryIdColumnName,
            divisionNameColumnName,
            divisionCreatedByColumnName,
            divisionUpdatedByColumnName,
            divisionCreatedAtColumnName,
            divisionUpdatedAtColumnName,
            divisionIdColumnName);
    private static final String findDivisionSQL = String.format("SELECT * FROM %s.%s WHERE %s = ?",
            schemaName,
            divisionTableName,
            divisionIdColumnName);

    public static ObservableList<Division> getAllDivisions() {
        ObservableList<Division> allDivisions = FXCollections.observableArrayList();

        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(selectAllDivisionsSQL);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    allDivisions.add(buildDivision(rs));
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

        return allDivisions;
    }

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

    public static boolean saveUpdatedDivision(Division division) {
        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(updateDivisionSQL);
                ps.setInt(1, division.getCountryId());
                ps.setString(2, division.getName());
                ps.setString(3, division.getCreatedBy());
                ps.setString(4, division.getUpdatedBy());
                ps.setTimestamp(5, Timestamp.from(division.getCreatedAt()));
                ps.setTimestamp(6, Timestamp.from(division.getUpdatedAt()));
                ps.setInt(7, division.getId());
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
