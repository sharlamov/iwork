package com.dao.model;


import java.math.BigDecimal;

public class CustomItem {

    private BigDecimal id;
    private String label;

    public CustomItem(BigDecimal id, String label) {
        this.id = id;
        this.label = label;
    }

    public CustomItem(int id, String label) {
        this.id = new BigDecimal(id);
        this.label = label;
    }

    public CustomItem(Object id, Object label) {
        if (id instanceof BigDecimal) {
            this.id = (BigDecimal) id;
            this.label = label == null ? " " : label.toString();
        } else if (id instanceof String) {
            this.id = new BigDecimal(id.toString());
            this.label = label == null ? "" : label.toString();
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
        if (obj instanceof CustomItem) {
            CustomItem other = (CustomItem) obj;
            return !(this.id == null || other.id == null) && this.id.equals(other.id);
        } else {
            return false;
        }
    }

    public CustomItem copy() {
        BigDecimal cloneId = getId() != null ? new BigDecimal(getId().toString()) : null;
        String cloneLabel = getLabel() != null ? getLabel() + "" : "";
        return new CustomItem(cloneId, cloneLabel);
    }
}
