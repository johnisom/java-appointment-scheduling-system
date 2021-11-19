package model;

import controller.MainController;
import helper.dbaccess.dao.DBContact;
import helper.dbaccess.dao.DBCustomer;
import helper.dbaccess.dao.DBUser;
import helper.locale.LocaleHelper;

import java.sql.Timestamp;
import java.time.*;
import java.util.Optional;

/**
 * This Model represents an appointment.
 * @see Model
 */
public class Appointment extends Model {
    /**
     * The exception that is raised if the Contact can not be found when trying to get the contact or contactName
     * @see Contact
     * @see DBContact
     */
    public static class ContactNotFoundException extends Exception {}

    /**
     * The exception that is raised if the Customer can not be found when trying to get the customer or customerName
     * @see Customer
     * @see DBCustomer
     */
    public static class CustomerNotFoundException extends Exception {}

    /**
     * The exception that is raised if the User can not be found when trying to get the user or userName
     * @see User
     * @see DBUser
     */
    public static class UserNotFoundException extends Exception {}

    /**
     * LocalTime used to represent the starting time of office hours.
     */
    public static final LocalTime OFFICE_HOUR_START_LOCAL_TIME = LocalTime.of(8, 0);
    /**
     * LocalTime used to represent the ending time of office hours.
     */
    public static final LocalTime OFFICE_HOUR_END_LOCAL_TIME = LocalTime.of(22, 0);
    /**
     * ZoneId used to represent the time zone of the office hours, which is EST/GMT-5/UTC-5.
     */
    public static final ZoneId OFFICE_HOUR_ZONE_ID = ZoneId.of("GMT-5");

    /**
     * A static method that return whether a given single time lies outside the bounds
     * of the defined office hours of 8 AM-10 PM EST.
     * A time is outside office hours if it is before the starting time or after the ending time.
     * If it is exactly either, then it is within office hours.
     * @param offsetTime the time to compare that already has an offset applied that
     * @return whether the given time is outside the defined office hours.
     */
    public static boolean isOutsideOfficeHours(OffsetTime offsetTime) {
        ZoneOffset currentZoneOffset = OFFICE_HOUR_ZONE_ID.getRules().getOffset(Instant.now());

        OffsetTime officeHourStart = OFFICE_HOUR_START_LOCAL_TIME.atOffset(currentZoneOffset);
        OffsetTime officeHourEnd = OFFICE_HOUR_END_LOCAL_TIME.atOffset(currentZoneOffset);

        return offsetTime.isBefore(officeHourStart) || offsetTime.isAfter(officeHourEnd);
    }

    /**
     * The id of the appointment.
     */
    private int id;
    /**
     * The contactId of the appointment.
     */
    private int contactId;
    /**
     * The customerId of the appointment.
     */
    private int customerId;
    /**
     * The userId of the appointment.
     */
    private int userId;
    /**
     * The title of the appointment.
     */
    private String title;
    /**
     * The description of the appointment.
     */
    private String description;
    /**
     * The location of the appointment.
     */
    private String location;
    /**
     * The type of the appointment.
     */
    private String type;
    /**
     * The starting time of the appointment.
     */
    private Instant startsAt;
    /**
     * The ending time of the appointment.
     */
    private Instant endsAt;
    /**
     * The time of creation of the appointment.
     */
    private Instant createdAt;
    /**
     * The time of last update of the appointment.
     */
    private Instant updatedAt;
    /**
     * The method of creation of the  appointment.
     */
    private String createdBy;
    /**
     * The method of last update of the  appointment.
     */
    private String updatedBy;

    /**
     * The associated Contact.
     */
    Contact contact;
    /**
     * The associated Customer.
     */
    Customer customer;
    /**
     * The associated User.
     */
    User user;

    /**
     * The no-fields constructor used by the controller.
     */
    public Appointment() {}

    /**
     * The constructor that accepts all fields.
     * @param id the id.
     * @param contactId the contactId.
     * @param customerId the customerId.
     * @param userId the userId.
     * @param title the title.
     * @param description the description.
     * @param location the location.
     * @param type the type.
     * @param startsAt the starting time.
     * @param endsAt the ending time.
     * @param createdAt the time of creation.
     * @param updatedAt the time of last update.
     * @param createdBy the method of creation.
     * @param updatedBy the method of last update.
     */
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

    /**
     * Get the id.
     * @return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * Set the id.
     * @param id the id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the contactId.
     * @return the contactId.
     */
    public int getContactId() {
        return contactId;
    }

    /**
     * Set the contactId.
     * @param contactId the contactId.
     */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    /**
     * Get the customerId.
     * @return the customerId.
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Set the customerId.
     * @param customerId the customerId.
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Get the userId.
     * @return the userId.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Set the userId.
     * @param userId the userId.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Get the title.
     * @return the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title.
     * @param title the title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the description.
     * @return the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description.
     * @param description the description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the location.
     * @return the location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set the location.
     * @param location the location.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Get the type.
     * @return the type.
     */
    public String getType() {
        return type;
    }

    /**
     * Set the type.
     * @param type the type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the starting time.
     * @return the starting time.
     */
    public Instant getStartsAt() {
        return startsAt;
    }

    /**
     * Set the starting time.
     * @param startsAt the starting time.
     */
    public void setStartsAt(Instant startsAt) {
        this.startsAt = startsAt;
    }

    /**
     * Get the ending time.
     * @return the ending time.
     */
    public Instant getEndsAt() {
        return endsAt;
    }

    /**
     * Set the ending time.
     * @param endsAt the ending time.
     */
    public void setEndsAt(Instant endsAt) {
        this.endsAt = endsAt;
    }

    /**
     * Get the time of creation.
     * @return the time of creation.
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Set the time of creation.
     * @param createdAt the time of creation.
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Get the time of last update.
     * @return the time of last update.
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Set the time of last update.
     * @param updatedAt the time of last update.
     */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Get the method of creation.
     * @return the method of creation.
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the method of creation.
     * @param createdBy the method of creation.
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the method of last update.
     * @return the method of last update.
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Set the method of last update.
     * @param updatedBy the method of last update.
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Gets the starting time but formatted using LocaleHelper#formatInstant().
     * @return the formatted starting time.
     * @see LocaleHelper#formatInstant(Instant)
     */
    public String getFormattedStartsAt() {
        return LocaleHelper.formatInstant(getStartsAt());
    }

    /**
     * Gets the ending time but formatted using LocaleHelper#formatInstant().
     * @return the formatted ending time.
     * @see LocaleHelper#formatInstant(Instant)
     */
    public String getFormattedEndsAt() {
        return LocaleHelper.formatInstant(getEndsAt());
    }

    /**
     * Gets the associated Contact.
     * @return the associated Contact.
     * @throws ContactNotFoundException if the Contact does not exist in the database.
     * @see DBContact
     */
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

    /**
     * Gets the associated Contact's name.
     * @return the associated Contact's name.
     * @throws ContactNotFoundException if the Contact does not exist in the database.
     * @see DBContact
     * @see #getContact()
     */
    public String getContactName() throws ContactNotFoundException {
        Contact c = getContact();
        if (c == null) {
            throw new ContactNotFoundException();
        } else {
            return c.getName();
        }
    }

    /**
     * Gets the associated Customer.
     * @return the associated Customer.
     * @throws CustomerNotFoundException if the Customer does not exist in the database.
     * @see DBCustomer
     */
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

    /**
     * Gets the associated Customer's name.
     * @return the associated Customer's name.
     * @throws CustomerNotFoundException if the Customer does not exist in the database.
     * @see DBCustomer
     * @see #getCustomer()
     */
    public String getCustomerName() throws CustomerNotFoundException {
        Customer c = getCustomer();
        if (c == null) {
            throw new CustomerNotFoundException();
        } else {
            return c.getName();
        }
    }

    /**
     * Gets the associated User.
     * @return the associated User.
     * @throws UserNotFoundException if the User does not exist in the database.
     */
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

    /**
     * Gets the associated User's name.
     * @return the associated User's name.
     * @throws UserNotFoundException if the User does not exist in the database.
     * @see #getUser()
     */
    public String getUserUsername() throws UserNotFoundException {
        User u = getUser();
        if (u == null) {
            throw new UserNotFoundException();
        } else {
            return u.getUsername();
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

    /**
     * Creates a string representation of the contact for use in displaying the 15-minute log-in notification.
     * @return a pretty string containing the title, id, and starting time of the appointment.
     * @see MainController#showAppointmentsWithinNext15Mins()
     */
    public String toPrettyString() {
        return String.format("Id: %d, Title: \"%s\", Starts at: %s",
                getId(),
                getTitle(),
                getFormattedStartsAt());
    }
}
