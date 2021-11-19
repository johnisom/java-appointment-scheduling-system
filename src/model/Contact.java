package model;

/**
 * This model represents a contact.
 * @see Model
 */
public class Contact extends Model {
    /**
     * The id of the contact.
     */
    private int id;
    /**
     * The name of the contact.
     */
    private String name;
    /**
     * The email of the contact.
     */
    private String email;

    /**
     * The constructor for the contact.
     * @param id the id.
     * @param name the name.
     * @param email the email.
     */
    public Contact(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
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
     * Gets the email.
     * @return the email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     * @param email the email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("<Contact : id=%d name=\"%s\" email=\"%s\">",
                getId(),
                getName(),
                getEmail());
    }

}
