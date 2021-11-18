package model;

import helper.dbaccess.dao.DBContact;

public class Contact extends Model {
    private int id;
    private String name;
    private String email;

    public Contact(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean save() {
        return DBContact.saveUpdatedContact(this);
    }

    @Override
    public void setAttribute(String attributeName, Object value) {
        switch (attributeName) {
            case "id":
                setId((Integer) value);
                return;
            case "name":
                setName((String) value);
                return;
            case "email":
                setEmail((String) value);
                return;
            default:
        }
    }

    @Override
    public String toString() {
        return String.format("<Contact : id=%d name=\"%s\" email=\"%s\">",
                getId(),
                getName(),
                getEmail());
    }

}
