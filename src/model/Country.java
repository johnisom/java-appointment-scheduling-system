package model;

/**
 * This Model represents a country.
 * @see Model
 */
public class Country extends Model {
    /**
     * The id of the country.
     */
    private int id;
    /**
     * The name of the country.
     */
    private String name;

    /**
     * The constructor.
     * @param id the id.
     * @param name the name.
     */
    public Country(int id, String name) {
        this.id = id;
        this.name = name;
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

    @Override
    public String toString() {
        return String.format("<Country : id=%d, name=\"%s\">",
                getId(),
                getName());
    }

}
