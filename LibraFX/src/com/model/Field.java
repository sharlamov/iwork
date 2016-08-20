package com.model;

public class Field {
    private String name;
    private Object value;

    public Field(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public Field(String name) {
        this(name, null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Field) {
            Field f = (Field) obj;
            return name.equalsIgnoreCase(f.getName());
        } else return false;

    }
}
