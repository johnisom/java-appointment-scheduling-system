package model;

import helper.dbaccess.dao.DBCountry;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

/**
 * This Model represents a first-level division.
 * @see Model
 */
public class Division extends Model {
    /**
     * The exception that is thrown when trying to get the country or country name but the country does not exist in the database.
     * @see Country
     * @see DBCountry
     */
    public static class CountryNotFoundException extends Exception {}

    /**
     * The id of the first-level division.
     */
    private int id;
    /**
     * The countryId of the first-level division.
     */
    private int countryId;
    /**
     * The name of the first-level division.
     */
    private String name;
    /**
     * The method of creation of the first-level division.
     */
    private String createdBy;
    /**
     * The method of last update of the first-level division.
     */
    private String updatedBy;
    /**
     * The time of creation of the first-level division.
     */
    private Instant createdAt;
    /**
     * The time of last update of the first-level division.
     */
    private Instant updatedAt;

    /**
     * The associated country.
     * @see Country
     */
    private Country country;

    /**
     * The constructor for the first-level division.
     * @param id the id.
     * @param countryId the countryId.
     * @param name the name.
     * @param createdBy the method of creation.
     * @param updatedBy the method of last update.
     * @param createdAt the time of creation.
     * @param updatedAt the time of last update.
     */
    public Division(int id, int countryId, String name, String createdBy, String updatedBy, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.countryId = countryId;
        this.name = name;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.createdAt = createdAt.toInstant();
        this.updatedAt = updatedAt.toInstant();
    }

    @Override
    public String toString() {
        return String.format("<Division : id=%d countryId=%d name=\"%s\" createdBy=\"%s\" updatedBy=\"%s\" createdAt=[%s] updatedAt=[%s]>",
                getId(),
                getCountryId(),
                getName(),
                getCreatedBy(),
                getUpdatedBy(),
                getCreatedAt(),
                getUpdatedAt());
    }

    /**
     * Gets the id.
     * @return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id.
     * @param id the id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the countryId.
     * @return the countryId.
     */
    public int getCountryId() {
        return countryId;
    }

    /**
     * Sets the countryId.
     * @param countryId the countryId.
     */
    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    /**
     * Gets the name.
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name the name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the method of creation.
     * @return the method of creation.
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the method of creation.
     * @param createdBy the method of creation.
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the method of last update.
     * @return the method of last update.
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Sets the method of last update.
     * @param updatedBy the method of last update.
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Gets the time of creation.
     * @return the time of creation.
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the time of creation.
     * @param createdAt the time of creation.
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the time of last update.
     * @return the time of last update.
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the time of last update.
     * @param updatedAt the time of last update.
     */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the associated country's name.
     * @return the name of the country.
     * @throws CountryNotFoundException if the country does not exist in the database.
     * @see #getCountry()
     */
    public String getCountryName() throws CountryNotFoundException {
        return getCountry().getName();
    }

    /**
     * Gets the associated country.
     * @return the country.
     * @throws CountryNotFoundException if the country does not exist in the database.
     * @see Country
     * @see DBCountry
     */
    public Country getCountry() throws CountryNotFoundException {
        // lazy-load country

        if (country == null) {
            Optional<Country> newCountry = DBCountry.getCountryFromId(getCountryId());
            if (newCountry.isEmpty()) {
                throw new CountryNotFoundException();
            } else {
                this.country = newCountry.get();
            }
        }

        return country;
    }
}
