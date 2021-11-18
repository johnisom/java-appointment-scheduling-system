package model;

import helper.dbaccess.dao.DBCountry;

public class Country extends Model {
    private int id;
    private String name;

    public Country(int id, String name) {
        this.id = id;
        this.name = name;
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

    @Override
    public boolean save() {
        return DBCountry.saveUpdatedCountry(this);
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
            default:
        }
    }

    @Override
    public String toString() {
        return String.format("<Country : id=%d, name=\"%s\">",
                getId(),
                getName());
    }

}
