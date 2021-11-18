package model;

import helper.dbaccess.dao.DBAppointment;
import helper.dbaccess.dao.DBContact;
import helper.dbaccess.dao.DBCustomer;
import helper.dbaccess.dao.DBUser;
import helper.locale.LocaleHelper;

import java.sql.Timestamp;
import java.time.*;
import java.util.Optional;

public class Appointment extends Model {
    public static class ContactNotFoundException extends Exception {}
    public static class CustomerNotFoundException extends Exception {}
    public static class UserNotFoundException extends Exception {}

    public static final LocalTime OFFICE_HOUR_START_LOCAL_TIME = LocalTime.of(8, 0);
    public static final LocalTime OFFICE_HOUR_END_LOCAL_TIME = LocalTime.of(22, 0);
    public static final ZoneId OFFICE_HOUR_ZONE_ID = ZoneId.of("GMT-5");

    public static boolean isOutsideOfficeHours(OffsetTime offsetTime) {
        ZoneOffset currentZoneOffset = OFFICE_HOUR_ZONE_ID.getRules().getOffset(Instant.now());

        OffsetTime officeHourStart = OFFICE_HOUR_START_LOCAL_TIME.atOffset(currentZoneOffset);
        OffsetTime officeHourEnd = OFFICE_HOUR_END_LOCAL_TIME.atOffset(currentZoneOffset);

        return offsetTime.isBefore(officeHourStart) || offsetTime.isAfter(officeHourEnd);
    }

    private int id;
    private int contactId;
    private int customerId;
    private int userId;
    private String title;
    private String description;
    private String location;
    private String type;
    private Instant startsAt;
    private Instant endsAt;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    Contact contact;
    Customer customer;
    User user;

    public Appointment() {}

    public Appointment(int id, int contactId, int customerId, int userId, String title, String description, String location, String type, Timestamp startsAt, Timestamp endsAt, Timestamp createdAt, Timestamp updatedAt, String createdBy, String updatedBy) {
        this.id = id;
        this.contactId = contactId;
        this.customerId = customerId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.startsAt = startsAt.toInstant();
        this.endsAt = endsAt.toInstant();
        this.createdAt = createdAt.toInstant();
        this.updatedAt = updatedAt.toInstant();
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Instant getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(Instant startsAt) {
        this.startsAt = startsAt;
    }

    public Instant getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Instant endsAt) {
        this.endsAt = endsAt;
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

    public String getFormattedStartsAt() {
        return LocaleHelper.formatInstant(getStartsAt());
    }

    public String getFormattedEndsAt() {
        return LocaleHelper.formatInstant(getEndsAt());
    }

    public Contact getContact() throws ContactNotFoundException {
        // lazy-load contact

        if (contact == null) {
            Optional<Contact> newContact = DBContact.getContactFromId(getContactId());
            if (newContact.isEmpty()) {
                throw new ContactNotFoundException();
            } else {
                this.contact = newContact.get();
                return contact;
            }
        } else {
            return contact;
        }
    }

    public String getContactName() throws ContactNotFoundException {
        Contact c = getContact();
        if (c == null) {
            throw new ContactNotFoundException();
        } else {
            return c.getName();
        }
    }

    public Customer getCustomer() throws CustomerNotFoundException {
        // lazy-load customer

        if (customer == null) {
            Optional<Customer> newCustomer = DBCustomer.getCustomerFromId(getCustomerId());
            if (newCustomer.isEmpty()) {
                throw new CustomerNotFoundException();
            } else {
                this.customer = newCustomer.get();
                return customer;
            }
        } else {
            return customer;
        }
    }

    public String getCustomerName() throws CustomerNotFoundException {
        Customer c = getCustomer();
        if (c == null) {
            throw new CustomerNotFoundException();
        } else {
            return c.getName();
        }
    }

    public User getUser() throws UserNotFoundException {
        // lazy-load user

        if (user == null) {
            Optional<User> newUser = DBUser.getUserFromId(getUserId());
            if (newUser.isEmpty()) {
                throw new UserNotFoundException();
            } else {
                this.user = newUser.get();
                return user;
            }
        } else {
            return user;
        }
    }

    public String getUserUsername() throws UserNotFoundException {
        User u = getUser();
        if (u == null) {
            throw new UserNotFoundException();
        } else {
            return u.getUsername();
        }
    }

    @Override
    public boolean save() {
        return DBAppointment.updateAppointment(this);
    }

    @Override
    public void setAttribute(String attributeName, Object value) {
        switch (attributeName) {
            case "id":
                setId((Integer) value);
                return;
            case "contactId":
                setContactId((Integer) value);
                return;
            case "customerId":
                setCustomerId((Integer) value);
                return;
            case "userId":
                setUserId((Integer) value);
                return;
            case "title":
                setTitle((String) value);
                return;
            case "description":
                setDescription((String) value);
                return;
            case "location":
                setLocation((String) value);
                return;
            case "type":
                setType((String) value);
                return;
            case "startsAt":
                setStartsAt((Instant) value);
                return;
            case "endsAt":
                setEndsAt((Instant) value);
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
        return String.format("<Appointment : id=%d contactId=%d customerId=%d userId=%d title=\"%s\" description=\"%s\" location=\"%s\" type=\"%s\" startsAt=[%s] endsAt=[%s] createdAt=[%s] updatedAt=[%s] createdBy=\"%s\" updatedBy=\"%s\" >",
                getId(),
                getContactId(),
                getCustomerId(),
                getUserId(),
                getTitle(),
                getDescription(),
                getLocation(),
                getType(),
                getStartsAt(),
                getEndsAt(),
                getCreatedAt(),
                getUpdatedAt(),
                getCreatedBy(),
                getUpdatedBy());
    }

    public String toPrettyString() {
        return String.format("Id: %d, Title: \"%s\", Starts at: %s",
                getId(),
                getTitle(),
                getFormattedStartsAt());
    }
}
