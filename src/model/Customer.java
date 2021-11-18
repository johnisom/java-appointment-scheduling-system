package model;

import helper.dbaccess.dao.DBCustomer;
import helper.dbaccess.dao.DBDivision;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

public class Customer extends Model {
    public static class DivisionNotFoundException extends Exception {}
    private int id;
    private int divisionId;
    private String name;
    private String address;
    private String postalCode;
    private String phoneNumber;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    private Division division;

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

    public Customer() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getDivisionName() throws DivisionNotFoundException {
        return getDivision().getName();
    }

    @Override
    public boolean save() {
        return DBCustomer.updateCustomer(this);
    }

    @Override
    public void setAttribute(String attributeName, Object value) {
        switch (attributeName) {
            case "id":
                setId((Integer) value);
                return;
            case "divisionId":
                setDivisionId((Integer) value);
                return;
            case "name":
                setName((String) value);
                return;
            case "address":
                setAddress((String) value);
                return;
            case "postalCode":
                setPostalCode((String) value);
                return;
            case "phoneNumber":
                setPhoneNumber((String) value);
                return;
            case "createdAt":
                setCreatedAt((Instant) value);
                return;
            case "updatedAt":
                setUpdatedAt((Instant) value);
                return;
            case "createdBy":
                setCreatedBy((String) value);
                return;
            case "updatedBy":
                setUpdatedBy((String) value);
                return;
            default:
        }
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
