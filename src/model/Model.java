package model;

import java.util.HashMap;

public abstract class Model {
    public void setAttributes(HashMap<String, Object> attributes) {
        attributes.forEach(this::setAttribute);
    };
    public abstract boolean save();
    public abstract void setAttribute(String attributeName, Object value);
    public abstract String toString();
}
