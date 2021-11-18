package helper.locale;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public abstract class LocaleHelper {
    private static ResourceBundle currentResourceBundle;
    private static Locale currentLocale;
    private static ZoneId currentZoneId;

    public static Locale getLocale() {
        if (currentLocale == null) {
            setCurrentLocale();
        }

        return currentLocale;
    }

    public static ResourceBundle getResourceBundle() {
        if (currentLocale == null) {
            setCurrentLocale();
        }

        if (currentResourceBundle == null) {
            setCurrentResourceBundle();
        }

        return currentResourceBundle;
    }

    public static ZoneId getZoneId() {
        if (currentLocale == null) {
            setCurrentLocale();
        }
        if (currentResourceBundle == null) {
            setCurrentResourceBundle();
        }

        if (currentZoneId == null) {
            setCurrentZoneId();
        }

        return currentZoneId;
    }

    public static String getTranslation(String key) {
        return getTranslation(key, "");
    }

    public static String getTranslation(String key, String defaultStr) {
        if (key.isBlank()) return defaultStr;

        try {
            return getResourceBundle().getString(key);
        } catch (MissingResourceException e) {
            e.printStackTrace();
        }
        return defaultStr;
    };

    public static String getLocation() {
        return getLocale().getDisplayCountry();
    }

    private synchronized static void setCurrentResourceBundle() {
        currentResourceBundle = ResourceBundle.getBundle("helper.locale/LangTrans", currentLocale);
    }

    private synchronized static void setCurrentLocale() {
        currentLocale = Locale.getDefault();
    }


    private synchronized static void setCurrentZoneId() {
        currentZoneId = ZoneId.systemDefault();
    }

    public static String formatInstant(Instant instant) {
        OffsetDateTime offsetStartsAt = instant.atOffset(LocaleHelper.getZoneId().getRules().getOffset(instant));
        return offsetStartsAt.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
    }
}
