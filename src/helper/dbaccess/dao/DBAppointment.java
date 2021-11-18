package helper.dbaccess.dao;

import helper.dbaccess.DBConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class DBAppointment {
    private static final int maxRetries = 3;

    public static final String schemaName = "client_schedule";
    public static final String appointmentTableName = "appointments";
    public static final String appointmentIdColumnName = "Appointment_ID";
    public static final String contactIdColumnName = "Contact_ID";
    public static final String customerIdColumnName = "Customer_ID";
    public static final String userIdColumnName = "User_ID";
    public static final String appointmentTitleColumnName = "Title";
    public static final String appointmentDescriptionColumnName = "Description";
    public static final String appointmentLocationColumnName = "Location";
    public static final String appointmentTypeColumnName = "Type";
    public static final String appointmentStartsAtColumnName = "Start";
    public static final String appointmentEndsAtColumnName = "End";
    public static final String appointmentCreatedAtColumnName = "Create_Date";
    public static final String appointmentUpdatedAtColumnName = "Last_Update";
    public static final String appointmentCreatedByColumnName = "Created_By";
    public static final String appointmentUpdatedByColumnName = "Last_Updated_By";

    private static final String selectAllAppointmentsSQL = String.format("SELECT * FROM %s.%s ORDER BY %s ASC;",
            schemaName,
            appointmentTableName,
            appointmentStartsAtColumnName);
    private static final String selectAppointmentsForContactId = String.format("SELECT * FROM %s.%s WHERE %s = ? ORDER BY %s ASC;",
            schemaName,
            appointmentTableName,
            contactIdColumnName,
            appointmentStartsAtColumnName);
    private static final String selectAppointmentsWithinTimeRangeSQL = String.format("SELECT * FROM %s.%s WHERE %s BETWEEN ? AND ? ORDER BY %s ASC;",
            schemaName,
            appointmentTableName,
            appointmentStartsAtColumnName,
            appointmentStartsAtColumnName);
    private static final String selectAppointmentsWithinTimeRangeWithUserIdSQL = String.format("SELECT * FROM %s.%s WHERE %s = ? AND %s BETWEEN ? AND ? ORDER BY %s ASC;",
            schemaName,
            appointmentTableName,
            userIdColumnName,
            appointmentStartsAtColumnName,
            appointmentStartsAtColumnName);
    private static final String selectAppointmentsOverlappingWithTimeRangeSQL = String.format("SELECT * FROM %s.%s WHERE %s > ? AND %s < ? OR %s > ? AND %s < ? OR %s <= ? AND %s >= ? ORDER BY %s ASC;",
            schemaName,
            appointmentTableName,
            appointmentStartsAtColumnName,
            appointmentStartsAtColumnName,
            appointmentEndsAtColumnName,
            appointmentEndsAtColumnName,
            appointmentStartsAtColumnName,
            appointmentEndsAtColumnName,
            appointmentStartsAtColumnName);
    private static final String updateAppointmentSQL = String.format("UPDATE %s.%s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = NOW(), %s = ?, %s = 'desktop-app' WHERE %s = ?;",
            schemaName,
            appointmentTableName,
            contactIdColumnName,
            customerIdColumnName,
            userIdColumnName,
            appointmentTitleColumnName,
            appointmentDescriptionColumnName,
            appointmentLocationColumnName,
            appointmentTypeColumnName,
            appointmentStartsAtColumnName,
            appointmentEndsAtColumnName,
            appointmentCreatedAtColumnName,
            appointmentUpdatedAtColumnName,
            appointmentCreatedByColumnName,
            appointmentUpdatedByColumnName,
            appointmentIdColumnName);
    private static final String createAppointmentSQL = String.format("INSERT INTO %s.%s(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), 'desktop-app', 'desktop-app');",
            schemaName,
            appointmentTableName,
            contactIdColumnName,
            customerIdColumnName,
            userIdColumnName,
            appointmentTitleColumnName,
            appointmentDescriptionColumnName,
            appointmentLocationColumnName,
            appointmentTypeColumnName,
            appointmentStartsAtColumnName,
            appointmentEndsAtColumnName,
            appointmentCreatedAtColumnName,
            appointmentUpdatedAtColumnName,
            appointmentCreatedByColumnName,
            appointmentUpdatedByColumnName);
    private static final String deleteAppointmentSQL = String.format("DELETE FROM %s.%s WHERE %s = ?",
            schemaName,
            appointmentTableName,
            appointmentIdColumnName);
    private static final String findAppointmentSQL = String.format("SELECT * FROM %s.%s WHERE %s = ?",
            schemaName,
            appointmentTableName,
            appointmentIdColumnName);
    private static final String getAppointmentsCountByMonthAndTypeSQL = String.format("SELECT MONTHNAME(%s) AS month, %s, COUNT(%s) AS count FROM %s.%s GROUP BY MONTHNAME(%s), %s;",
            appointmentStartsAtColumnName,
            appointmentTypeColumnName,
            userIdColumnName,
            schemaName,
            appointmentTableName,
            appointmentStartsAtColumnName,
            appointmentTypeColumnName);
    private static final String getAppointmentsCountByWeekdayAndTypeSQL = String.format("SELECT DAYNAME(%s) AS weekday, %s, COUNT(%s) AS count FROM %s.%s GROUP BY DAYNAME(%s), %s;",
            appointmentStartsAtColumnName,
            appointmentTypeColumnName,
            userIdColumnName,
            schemaName,
            appointmentTableName,
            appointmentStartsAtColumnName,
            appointmentTypeColumnName);


    public static ObservableList<Appointment> getAllAppointments() {
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();

        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(selectAllAppointmentsSQL);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    allAppointments.add(buildAppointment(rs));
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

        return allAppointments;
    }

    public static ObservableList<Appointment> getAllAppointmentsForContactId(int contactId) {
        ObservableList<Appointment> appointmentsForContactId = FXCollections.observableArrayList();

        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(selectAppointmentsForContactId);
                ps.setInt(1, contactId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    appointmentsForContactId.add(buildAppointment(rs));
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

        return appointmentsForContactId;
    }

    public static ObservableList<Appointment> getAllAppointmentsStartingWithinTimeRange(Instant from, Instant to) {
        ObservableList<Appointment> appointmentsStartingWithinTimeRange = FXCollections.observableArrayList();

        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(selectAppointmentsWithinTimeRangeSQL);
                ps.setTimestamp(1, Timestamp.from(from));
                ps.setTimestamp(2, Timestamp.from(to));
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    appointmentsStartingWithinTimeRange.add(buildAppointment(rs));
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

        return appointmentsStartingWithinTimeRange;
    }

    public static ObservableList<Appointment> getAllAppointmentsStartingWithinTimeRangeForUserId(Instant from, Instant to, int userId) {
        ObservableList<Appointment> appointmentsStartingWithinTimeRange = FXCollections.observableArrayList();

        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(selectAppointmentsWithinTimeRangeWithUserIdSQL);
                ps.setInt(1, userId);
                ps.setTimestamp(2, Timestamp.from(from));
                ps.setTimestamp(3, Timestamp.from(to));
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    appointmentsStartingWithinTimeRange.add(buildAppointment(rs));
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

        return appointmentsStartingWithinTimeRange;
    }

    public static ObservableList<Appointment> getAllAppointmentsOverlappingWithTimeRange(Instant startsAt, Instant endsAt) {
        ObservableList<Appointment> appointmentsOverlappingWithTimeRange = FXCollections.observableArrayList();
        Timestamp startsAtTimestamp = Timestamp.from(startsAt);
        Timestamp endsAtTimestamp = Timestamp.from(endsAt);
        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(selectAppointmentsOverlappingWithTimeRangeSQL);
                ps.setTimestamp(1, startsAtTimestamp);
                ps.setTimestamp(2, endsAtTimestamp);
                ps.setTimestamp(3, startsAtTimestamp);
                ps.setTimestamp(4, endsAtTimestamp);
                ps.setTimestamp(5, startsAtTimestamp);
                ps.setTimestamp(6, endsAtTimestamp);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    appointmentsOverlappingWithTimeRange.add(buildAppointment(rs));
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

        return appointmentsOverlappingWithTimeRange;
    }

    public static ObservableList<Appointment> getAllAppointmentsStartingWithinNextMonth() {
        Instant now = Instant.now();
        Instant in1Month = now.atOffset(ZoneOffset.UTC).plusMonths(1).toInstant();
        return getAllAppointmentsStartingWithinTimeRange(now, in1Month);
    }

    public static ObservableList<Appointment> getAllAppointmentsStartingWithinNextWeek() {
        Instant now = Instant.now();
        Instant in1Week = now.atOffset(ZoneOffset.UTC).plusWeeks(1).toInstant();
        return getAllAppointmentsStartingWithinTimeRange(now, in1Week);
    }

    public static ObservableList<Appointment> getAppointmentsStartingWithinNext15MinsForUserId(int userId) {
        Instant now = Instant.now();
        Instant in15Minutes = now.atOffset(ZoneOffset.UTC).plusMinutes(15).toInstant();
        return getAllAppointmentsStartingWithinTimeRangeForUserId(now, in15Minutes, userId);
    }

    public static ObservableList<List<StringProperty>> getAppointmentsCountByMonthAndType() {
        ObservableList<List<StringProperty>> result = FXCollections.observableArrayList();
        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(getAppointmentsCountByMonthAndTypeSQL);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    List<StringProperty> entry = new ArrayList<>(3);
                    entry.add(new SimpleStringProperty(rs.getString("month")));
                    entry.add(new SimpleStringProperty(rs.getString(appointmentTypeColumnName)));
                    entry.add(new SimpleStringProperty(String.valueOf(rs.getInt("count"))));
                    result.add(entry);
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

    public static ObservableList<List<StringProperty>> getAppointmentsCountByWeekdayAndType() {
        ObservableList<List<StringProperty>> result = FXCollections.observableArrayList();
        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(getAppointmentsCountByWeekdayAndTypeSQL);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    List<StringProperty> entry = new ArrayList<>(3);
                    entry.add(new SimpleStringProperty(rs.getString("weekday")));
                    entry.add(new SimpleStringProperty(rs.getString(appointmentTypeColumnName)));
                    entry.add(new SimpleStringProperty(String.valueOf(rs.getInt("count"))));
                    result.add(entry);
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

    public static boolean updateAppointment(Appointment appointment) {
        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(updateAppointmentSQL);
                ps.setInt(1, appointment.getContactId());
                ps.setInt(2, appointment.getCustomerId());
                ps.setInt(3, appointment.getUserId());
                ps.setString(4, appointment.getTitle());
                ps.setString(5, appointment.getDescription());
                ps.setString(6, appointment.getLocation());
                ps.setString(7, appointment.getType());
                ps.setTimestamp(8, Timestamp.from(appointment.getStartsAt()));
                ps.setTimestamp(9, Timestamp.from(appointment.getEndsAt()));
                ps.setTimestamp(10, Timestamp.from(appointment.getUpdatedAt()));
                ps.setString(11, appointment.getUpdatedBy());
                ps.setInt(12, appointment.getId());
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

    public static Optional<Appointment> createAppointment(Appointment appointment) {
        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(createAppointmentSQL, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setInt(1, appointment.getContactId());
                ps.setInt(2, appointment.getCustomerId());
                ps.setInt(3, appointment.getUserId());
                ps.setString(4, appointment.getTitle());
                ps.setString(5, appointment.getDescription());
                ps.setString(6, appointment.getLocation());
                ps.setString(7, appointment.getType());
                ps.setTimestamp(8, Timestamp.from(appointment.getStartsAt()));
                ps.setTimestamp(9, Timestamp.from(appointment.getEndsAt()));
                if (ps.executeUpdate() == 1) {
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        return getAppointmentFromId(rs.getInt(1));
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

    public static boolean deleteAppointmentFromId(int appointmentId) {
        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(deleteAppointmentSQL);
                ps.setInt(1, appointmentId);

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

    public static Optional<Appointment> getAppointmentFromId(int id) {
        for (int count = 0; count < maxRetries; ++count) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(findAppointmentSQL);
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return Optional.of(buildAppointment(rs));
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

    private static Appointment buildAppointment(ResultSet rs) throws SQLException {
        int appointmentId = rs.getInt(appointmentIdColumnName);
        int contactId = rs.getInt(contactIdColumnName);
        int customerId = rs.getInt(customerIdColumnName);
        int userId = rs.getInt(userIdColumnName);
        String appointmentTitle = rs.getString(appointmentTitleColumnName);
        String appointmentDescription = rs.getString(appointmentDescriptionColumnName);
        String appointmentLocation = rs.getString(appointmentLocationColumnName);
        String appointmentType = rs.getString(appointmentTypeColumnName);
        Timestamp appointmentStartsAt = rs.getTimestamp(appointmentStartsAtColumnName);
        Timestamp appointmentEndsAt = rs.getTimestamp(appointmentEndsAtColumnName);
        Timestamp appointmentCreatedAt = rs.getTimestamp(appointmentCreatedAtColumnName);
        Timestamp appointmentUpdatedAt = rs.getTimestamp(appointmentUpdatedAtColumnName);
        String appointmentCreatedBy = rs.getString(appointmentCreatedByColumnName);
        String appointmentUpdatedBy = rs.getString(appointmentUpdatedByColumnName);
        return new Appointment(appointmentId,
                contactId,
                customerId,
                userId,
                appointmentTitle,
                appointmentDescription,
                appointmentLocation,
                appointmentType,
                appointmentStartsAt,
                appointmentEndsAt,
                appointmentCreatedAt,
                appointmentUpdatedAt,
                appointmentCreatedBy,
                appointmentUpdatedBy);
    }
}
