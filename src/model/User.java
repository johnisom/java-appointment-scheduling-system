package model;

import helper.dbaccess.dao.DBUser;

import java.sql.Timestamp;
import java.time.Instant;

public class User extends Model {
    private int id;
    private String username;
    private String password;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    public User(int id, String username, String password, Timestamp createdAt, Timestamp updatedAt, String createdBy, String updatedBy) {
        this.id = id;
        this.username = username;
        this.password = password;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    @Override
    public boolean save() {
        return DBUser.updateUser(this);
    }

    @Override
    public void setAttribute(String attributeName, Object value) {
        switch (attributeName) {
            case "id":
                setId((Integer) value);
                return;
            case "username":
                setUsername((String) value);
                return;
            case "password":
                setPassword((String) value);
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
