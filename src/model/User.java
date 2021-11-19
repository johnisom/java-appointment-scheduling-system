package model;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * This Model represents a user.
 * @see Model
 */
public class User extends Model {
    /**
     * The id of the user.
     */
    private int id;
    /**
     * The username of the user.
     */
    private String username;
    /**
     * The password of the user.
     */
    private String password;
    /**
     * The time of creation of the user.
     */
    private Instant createdAt;
    /**
     * The time of last update of the user.
     */
    private Instant updatedAt;
    /**
     * The method of creation of the user.
     */
    private String createdBy;
    /**
     * The method of last update of the user.
     */
    private String updatedBy;

    /**
     * The constructor for the user.
     * @param id the id.
     * @param username the username.
     * @param password the password.
     * @param createdAt the time of creation.
     * @param updatedAt the time of last update.
     * @param createdBy the method of creation.
     * @param updatedBy the method of last update.
     */
    public User(int id, String username, String password, Timestamp createdAt, Timestamp updatedAt, String createdBy, String updatedBy) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.createdAt = createdAt.toInstant();
        this.updatedAt = updatedAt.toInstant();
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
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
     * Gets the username.
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * @param username the username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password.
     * @return the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     * @param password the password.
     */
    public void setPassword(String password) {
        this.password = password;
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

    @Override
    public String toString() {
        return String.format("<User : id=%d username=\"%s\" password=\"%s\" createdAt=[%s] updatedAt=[%s] createdBy=\"%s\" updatedBy=\"%s\">",
                getId(),
                getUsername(),
                "REDACTED",
                getCreatedAt(),
                getUpdatedAt(),
                getCreatedBy(),
                getUpdatedBy());
    }
}
