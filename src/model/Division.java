package model;

import helper.dbaccess.dao.DBCountry;
import helper.dbaccess.dao.DBDivision;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

public class Division extends Model {
    public static class CountryNotFoundException extends Exception {};
    private int id;
    private int countryId;
    private String name;
    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;

    private Country country;

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
    public boolean save() {
        return DBDivision.saveUpdatedDivision(this);
    }

    @Override
    public void setAttribute(String attributeName, Object value) {
        switch (attributeName) {
            case "id":
                setId((Integer) value);
                return;
            case "countryId":
                setCountryId((Integer) value);
                return;
            case "name":
                setName((String) value);
                return;
            case "createdBy":
                setCreatedBy((String) value);
                return;
            case "updatedBy":
                setUpdatedBy((String) value);
                return;
            case "createdAt":
                setCreatedAt((Instant) value);
                return;
            case "updatedAt":
                setUpdatedAt((Instant) value);
                return;
            default:
        }
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCountryName() throws CountryNotFoundException {
        return getCountry().getName();
    }

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
