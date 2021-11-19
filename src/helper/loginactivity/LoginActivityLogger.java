package helper.loginactivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * An abstract class with only static methods and not meant to be instantiated.
 * It is used to log log-in attempts to the log file.
 */
public final class LoginActivityLogger {
    /**
     * The file name to log the attempts to.
     */
    private static final String filename = "login_activity.txt";

    /**
     * Given the username and whether the login was successful,
     * logs the result into the log file, creating the file if necessary.
     * @param username the attempted username.
     * @param wasLoginSuccessful whether the login was successful.
     * @throws IOException if log file could not be created.
     * @see #createLogFileIfNotExists()
     */
    public static void logAttempt(String username, boolean wasLoginSuccessful) throws IOException {
        createLogFileIfNotExists();
        appendToLogFile(String.format("[%s] [INFO] Login Attempt for user \"%s\" [Success = %s]%n", getFormattedNow(), username, wasLoginSuccessful));
    }

    /**
     * Creates the log file.
     * @throws IOException if the log file could not be created.
     * @see #logAttempt(String, boolean)
     */
    private static void createLogFileIfNotExists() throws IOException {
        File file = new File(filename);
        file.createNewFile();
    }

    /**
     * Given a string, simply appends to the end of the log file.
     * @param entry the string to be logged.
     * @throws IOException if the file could not be opened or written to.
     */
    private static void appendToLogFile(String entry) throws IOException {
        try (FileWriter fileWriter = new FileWriter(filename, true)) {
            fileWriter.append(entry);
        }
    }

    /**
     * Returns the current timestamp in UTC-0, formatted using DateTimeFormatter.ISO_OFFSET_DATE_TIME.
     * @return the current formatted instant.
     * @see DateTimeFormatter#ISO_OFFSET_DATE_TIME
     */
    private static String getFormattedNow() {
        OffsetDateTime now = Instant.now().atOffset(ZoneOffset.UTC);
        return now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
