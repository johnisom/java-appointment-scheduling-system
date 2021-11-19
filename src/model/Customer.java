package model;

import helper.dbaccess.dao.DBDivision;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

/**
 * This Model represents a customer.
 * @see Model
 */
public class Customer extends Model {
    /**
     * The exception that is thrown if the division does not exist in the database.
     */
    public static class DivisionNotFoundException extends Exception {}

    /**
     * The id of the customer.
     */
    private int id;
    /**
     * The divisionId of the customer.
     */
    private int divisionId;
    /**
     * The name of the customer.
     */
    private String name;
    /**
     * The address of the customer.
     */
    private String address;
    /**
     * The postalCode of the customer.
     */
    private String postalCode;
    /**
     * The phoneNumber of the customer.
     */
    private String phoneNumber;
    /**
     * The time of creation of the customer.
     */
    private Instant createdAt;
    /**
     * The time of last update of the customer.
     */
    private Instant updatedAt;
    /**
     * The method of creation of the customer.
     */
    private String createdBy;
    /**
     * The method of last update of the customer.
     */
    private String updatedBy;

    /**
     * The first-level division associated with the customer.
     */
    private Division division;

    /**
     * The full-param constructor for the customer.
     * @param id the id.
     * @param divisionId the divisionId.
     * @param name the name.
     * @param address the address.
     * @param postalCode the postalCode.
     * @param phoneNumber the phoneNumber.
     * @param createdAt the time of creation.
     * @param updatedAt the time of last update.
     * @param createdBy the method of creation.
     * @param updatedBy the method of last update.
     */
    public Customer(int id, int divisionId, String name, String address, String postalCode, String phoneNumber, Timestamp createdAt, Timestamp updatedAt, String createdBy, String updatedBy) {
        this.id = id;
        this.divisionId = divisionId;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        if (createdAt != null) this.createdAt = createdAt.toInstant();
        if (updatedAt != null) this.updatedAt = updatedAt.toInstant();
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    /**
     * The empty-param constructor for the customer.
     */
    public Customer() {
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
     * Gets the divisionId.
     * @return the divisionId.
     */
    public int getDivisionId() {
        return divisionId;
    }

    /**
     * Sets the divisionId.
     * @param divisionId the divisionId.
     */
    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
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
     * Gets the address.
     * @return the address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address.
     * @param address the address.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the postalCode.
     * @return the postalCode.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the postalCode.
     * @param postalCode the postalCode.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Gets the phoneNumber.
     * @return the phoneNumber.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phoneNumber.
     * @param phoneNumber the phoneNumber.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
     * Gets the division associated with the customer.
     * @return the division.
     * @throws DivisionNotFoundException if the division does not exist in the database.
     */
    public Division getDivision() throws DivisionNotFoundException {
        // lazy-load division

        if (division == null) {
            Optional<Division> newDivision = DBDivision.getDivisionFromId(getDivisionId());
            if (newDivision.isEmpty()) {
                throw new DivisionNotFoundException();
            } else {
                this.division = newDivision.get();
                return division;
            }
        } else {
            return division;
        }
    }

    /**
     * Gets the name of the division associated with the customer.
     * @return the division's name.
     * @throws DivisionNotFoundException if the division does not exist in the database.
     */
    public String getDivisionName() throws DivisionNotFoundException {
        return getDivision().getName();
    }

    @Override
    public String toString() {
        return String.format("<Customer : id=%d divisionId=%d name=\"%s\" address=\"%s\" postalCode=\"%s\" phoneNumber=\"%s\" createdAt=[%s] updatedAt=[%s] createdBy=\"%s\" updatedBy=\"%s\">",
                getId(),
                getDivisionId(),
                getName(),
                getAddress(),
                getPostalCode(),
                getPhoneNumber(),
                getCreatedAt(),
                getUpdatedAt(),
                getCreatedBy(),
                getUpdatedBy());
    }
}
