package helper.loginactivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public abstract class LoginActivityLogger {
    private static final String filename = "login_activity.txt";

    public static void logAttempt(String username, boolean wasLoginSuccessful) throws IOException {
        createLogFileIfNotExists();
        appendToLogFile(String.format("[%s] [INFO] Login Attempt for user \"%s\" [Success = %s]%n", getFormattedNow(), username, wasLoginSuccessful));
    }

    private static void createLogFileIfNotExists() throws IOException {
        File file = new File(filename);
        file.createNewFile();
    }

    private static void appendToLogFile(String entry) throws IOException {
        try (FileWriter fileWriter = new FileWriter(filename, true)) {
            fileWriter.append(entry);
        }
    }

    private static String getFormattedNow() {
        OffsetDateTime now = Instant.now().atOffset(ZoneOffset.UTC);
        return now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
