package helper.locale;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * An abstract class with only static methods and not meant to be instantiated.
 * It is used to get translations of strings, formatted time stamps, and other things that depend on locales.
 */
public abstract class LocaleHelper {
    /**
     * The ResourceBundle.
     */
    private static ResourceBundle currentResourceBundle;
    /**
     * The Locale.
     */
    private static Locale currentLocale;
    /**
     * The ZoneId.
     */
    private static ZoneId currentZoneId;

    /**
     * Gets the currentLocale. If null, first sets it.
     * @return the currentLocale.
     * @see #setCurrentLocale()
     */
    public static Locale getLocale() {
        if (currentLocale == null) {
            setCurrentLocale();
        }

        return currentLocale;
    }

    /**
     * Gets the currentResourceBundle. If it or currentLocale null, first sets them.
     * @return the currentResourceBundle.
     * @see #setCurrentResourceBundle()
     */
    public static ResourceBundle getResourceBundle() {
        if (currentLocale == null) {
            setCurrentLocale();
        }

        if (currentResourceBundle == null) {
            setCurrentResourceBundle();
        }

        return currentResourceBundle;
    }

    /**
     * Gets the currentZoneId. If it or currentLocale or currentResourceBundle null, first sets them.
     * @return the currentZoneId.
     * @see #setCurrentZoneId()
     */
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

    /**
     * The overloaded method that returns empty string if the translation is not found.
     * @param key the key to look up.
     * @return the translation if found, else the empty string
     * @see #getTranslation(String, String)
     */
    public static String getTranslation(String key) {
        return getTranslation(key, "");
    }

    /**
     * Given a key, returns the appropriate English or French translation.
     * If the translation is not found, returns the second parameter.
     * @param key the key to look up.
     * @param defaultStr the default value to be returned in case the key does not exist in the translation resource bundle.
     * @return the translation if found, else defaultStr.
     * @see #getTranslation(String)
     * @see ResourceBundle
     */
    public static String getTranslation(String key, String defaultStr) {
        if (key.isBlank()) return defaultStr;

        try {
            return getResourceBundle().getString(key);
        } catch (MissingResourceException e) {
            e.printStackTrace();
        }
        return defaultStr;
    }

    /**
     * Gets the country name and time zone name of the currentLocale and currentZoneI, formatted for display.
     * @return country name + time zone as a string.
     */
    public static String getLocation() {
        return String.format("%s, %s", getLocale().getDisplayCountry(), getZoneId().getDisplayName(TextStyle.FULL, getLocale()));
    }

    /**
     * Sets the currentResourceBundle.
     */
    private synchronized static void setCurrentResourceBundle() {
        currentResourceBundle = ResourceBundle.getBundle("helper.locale/LangTrans", currentLocale);
    }

    /**
     * Sets the currentLocale.
     */
    private synchronized static void setCurrentLocale() {
//        currentLocale = new Locale("en", "US");
//        currentLocale = new Locale("fr", "FR");
        currentLocale = Locale.getDefault();
    }

    /**
     * Sets the currentZoneId.
     */
    private synchronized static void setCurrentZoneId() {
        currentZoneId = ZoneId.systemDefault();
    }

    /**
     * Given an Instant, formats it into a nice, readable format.
     * @param instant the instant to be formatted.
     * @return the instant formatted in the FormatStyle.SHORT format.
     * @see FormatStyle#SHORT
     */
    public static String formatInstant(Instant instant) {
        OffsetDateTime offsetStartsAt = instant.atOffset(LocaleHelper.getZoneId().getRules().getOffset(instant));
        return offsetStartsAt.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
    }
}
