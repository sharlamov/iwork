package com.model;


import java.math.BigDecimal;

public class CustomItem {

    private BigDecimal id;
    private String label;

    public CustomItem() {
        super();
    }

    public CustomItem(BigDecimal id, String label) {
        super();
        this.id = id;
        this.label = label;
    }

    public CustomItem(Object id, Object label) {
        if (id instanceof BigDecimal) {
            this.id = (BigDecimal) id;
            this.label = label == null ? " " :label.toString();
        }
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String toStringValue() {
        return (id == null ? "" : id) + "#" + (label == null ? "" : label);
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        CustomItem other = (CustomItem) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (other == null || !id.equals(other.id))
            return false;
        return true;
    }
}
