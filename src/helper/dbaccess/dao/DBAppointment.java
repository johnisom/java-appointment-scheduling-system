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

/**
 * The DAO object/class that is used to perform all database operations pertaining to the Appointment model.
 * @see Appointment
 */
public abstract class DBAppointment {
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
    public static final String appointmentTableName = "appointments";
    /**
     * The name of the id column in the database.
     */
    public static final String appointmentIdColumnName = "Appointment_ID";
    /**
     * The name of the contact id column in the database.
     */
    public static final String contactIdColumnName = "Contact_ID";
    /**
     * The name of the customer id column in the database.
     */
    public static final String customerIdColumnName = "Customer_ID";
    /**
     * The name of the user id column in the database.
     */
    public static final String userIdColumnName = "User_ID";
    /**
     * The name of the title column in the database.
     */
    public static final String appointmentTitleColumnName = "Title";
    /**
     * The name of the description column in the database.
     */
    public static final String appointmentDescriptionColumnName = "Description";
    /**
     * The name of the location column in the database.
     */
    public static final String appointmentLocationColumnName = "Location";
    /**
     * The name of the type column in the database.
     */
    public static final String appointmentTypeColumnName = "Type";
    /**
     * The name of the starting time column in the database.
     */
    public static final String appointmentStartsAtColumnName = "Start";
    /**
     * The name of the ending time column in the database.
     */
    public static final String appointmentEndsAtColumnName = "End";
    /**
     * The name of the time of creation column in the database.
     */
    public static final String appointmentCreatedAtColumnName = "Create_Date";
    /**
     * The name of the time of last update column in the database.
     */
    public static final String appointmentUpdatedAtColumnName = "Last_Update";
    /**
     * The name of the method of creation column in the database.
     */
    public static final String appointmentCreatedByColumnName = "Created_By";
    /**
     * The name of the method of last update column in the database.
     */
    public static final String appointmentUpdatedByColumnName = "Last_Updated_By";

    /**
     * The SQL template for grabbing all appointments.
     */
    private static final String selectAllAppointmentsSQL = String.format("SELECT * FROM %s.%s ORDER BY %s ASC;",
            schemaName,
            appointmentTableName,
            appointmentStartsAtColumnName);
    /**
     * The SQL template for grabbing all appointments related to a contact.
     */
    private static final String selectAppointmentsForContactId = String.format("SELECT * FROM %s.%s WHERE %s = ? ORDER BY %s ASC;",
            schemaName,
            appointmentTableName,
            contactIdColumnName,
            appointmentStartsAtColumnName);
    /**
     * The SQL template for grabbing all appointments within a time range.
     */
    private static final String selectAppointmentsWithinTimeRangeSQL = String.format("SELECT * FROM %s.%s WHERE %s BETWEEN ? AND ? ORDER BY %s ASC;",
            schemaName,
            appointmentTableName,
            appointmentStartsAtColumnName,
            appointmentStartsAtColumnName);
    /**
     * The SQL template for grabbing all appointments within a time range and related to a user.
     */
    private static final String selectAppointmentsWithinTimeRangeWithUserIdSQL = String.format("SELECT * FROM %s.%s WHERE %s = ? AND %s BETWEEN ? AND ? ORDER BY %s ASC;",
            schemaName,
            appointmentTableName,
            userIdColumnName,
            appointmentStartsAtColumnName,
            appointmentStartsAtColumnName);
    /**
     * The SQL template for grabbing all appointments overlapping with a given start and end time.
     */
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
    /**
     * The SQL template for updating a single appointment.
     */
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
    /**
     * The SQL template for creating a single appointment.
     */
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
    /**
     * The SQL template for deleting a single appointment given just the id..
     */
    private static final String deleteAppointmentSQL = String.format("DELETE FROM %s.%s WHERE %s = ?",
            schemaName,
            appointmentTableName,
            appointmentIdColumnName);
    /**
     * The SQL template for finding a single appointment given jus the id.
     */
    private static final String findAppointmentSQL = String.format("SELECT * FROM %s.%s WHERE %s = ?",
            schemaName,
            appointmentTableName,
            appointmentIdColumnName);
    /**
     * The SQL template for generating the count by month and type report.
     */
    private static final String getAppointmentsCountByMonthAndTypeSQL = String.format("SELECT MONTHNAME(%s) AS month, %s, COUNT(%s) AS count FROM %s.%s GROUP BY MONTHNAME(%s), %s;",
            appointmentStartsAtColumnName,
            appointmentTypeColumnName,
            userIdColumnName,
            schemaName,
            appointmentTableName,
            appointmentStartsAtColumnName,
            appointmentTypeColumnName);
    /**
     * The SQL template for generating the count by weekday and type report.
     */
    private static final String getAppointmentsCountByWeekdayAndTypeSQL = String.format("SELECT DAYNAME(%s) AS weekday, %s, COUNT(%s) AS count FROM %s.%s GROUP BY DAYNAME(%s), %s;",
            appointmentStartsAtColumnName,
            appointmentTypeColumnName,
            userIdColumnName,
            schemaName,
            appointmentTableName,
            appointmentStartsAtColumnName,
            appointmentTypeColumnName);


    /**
     * Grabs all appointments from the database.
     * @return the appointments.
     */
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

    /**
     * Given a contact id, grabs all associated appointments from the database.
     * @param contactId the contact id.
     * @return the appointments.
     */
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

    /**
     * Given a time range, grabs all appointments that start within that time range.
     * @param from the starting time of the time range.
     * @param to the ending time of the time range.
     * @return the appointments.
     */
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

    /**
     * Given a time range and user id, grabs all appointments associated with that user that start within that time range.
     * @param from the starting time of the time range.
     * @param to the ending time of the time range.
     * @param userId the user id.
     * @return the appointments.
     */
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

    /**
     * Given a time range, grabs all appointments that overlap with the given time range.
     * @param startsAt the starting time of the time range.
     * @param endsAt the ending time of the time range.
     * @return the appointments.
     */
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

    /**
     * Grabs all appointments that start within the next 1 month.
     * @return the appointments.
     */
    public static ObservableList<Appointment> getAllAppointmentsStartingWithinNextMonth() {
        Instant now = Instant.now();
        Instant in1Month = now.atOffset(ZoneOffset.UTC).plusMonths(1).toInstant();
        return getAllAppointmentsStartingWithinTimeRange(now, in1Month);
    }

    /**
     * Grabs all appointments that start within the next 1 week.
     * @return the appointments.
     */
    public static ObservableList<Appointment> getAllAppointmentsStartingWithinNextWeek() {
        Instant now = Instant.now();
        Instant in1Week = now.atOffset(ZoneOffset.UTC).plusWeeks(1).toInstant();
        return getAllAppointmentsStartingWithinTimeRange(now, in1Week);
    }

    /**
     * Given a user id, grabs all appointments associated with that user that start within the next 15 minutes.
     * @param userId the user id.
     * @return hr appointments.
     */
    public static ObservableList<Appointment> getAppointmentsStartingWithinNext15MinsForUserId(int userId) {
        Instant now = Instant.now();
        Instant in15Minutes = now.atOffset(ZoneOffset.UTC).plusMinutes(15).toInstant();
        return getAllAppointmentsStartingWithinTimeRangeForUserId(now, in15Minutes, userId);
    }

    /**
     * Grabs the month, type, and count of appointments grouped by month &amp; type for a report.
     * @return the report data.
     */
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

    /**
     * Grabs the weekday, type, and count of appointments grouped by month &amp; type for a report.
     * @return the report data.
     */
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

    /**
     * Updates an appointment record in the database given an appointment model.
     * @param appointment the appointment with the fields populated.
     * @return true if the appointment was updated, false if there was an issue.
     */
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

    /**
     * Creates an appointment record in the database given an appointment model.
     * @param appointment the appointment with the fields populated.
     * @return true if the appointment was created, false if there was an issue.
     */
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

    /**
     * Deletes an appointment record from the database given an appointment id.
     * @param appointmentId the appointment id.
     * @return true if the appointment was deleted, false if there was an issue.
     */
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

    /**
     * Given an id, grabs the associated appointment from the database.
     * @param id the id.
     * @return the appointment.
     */
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

    /**
     * Given a result set that is in the middle of being used, build an appointment with the current row.
     * @param rs the ResultSet.
     * @return the new Appointment model object.
     * @throws SQLException if extracting fields fails.
     */
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
