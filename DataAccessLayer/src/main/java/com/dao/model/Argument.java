package com.dao.model;

public class Argument {
    private String name;
    private Object value;

    public Argument(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public Argument(String name) {
        this.name = name;
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
}
