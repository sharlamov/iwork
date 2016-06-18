package com.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class RowSet<T> extends ArrayList<T> {

    private List<String> names;

    public RowSet(int initialCapacity) {
        super(initialCapacity);
    }

    public RowSet() {
    }

    public RowSet(Collection<T> c) {
        super(c);
    }

    public RowSet(T value) {
        add(value);
    }

    public int findField(String fieldName) {
        for (int i = 0; i < names.size(); i++) {
            if (fieldName.equalsIgnoreCase(names.get(i)))
                return i;
        }
        return -1;
    }

    public String getColumnName(int col) {
        return names.isEmpty() ? null : names.get(col);
    }

    public int getColumnCount() {
        return names.size();
    }

    public T getValue(int col) {
        return isEmpty() ? null : get(col);
    }

    public T getValue(String fieldName) {
        int col = findField(fieldName);
        return col == -1 ? null : getValue(col);
    }

    public void setValue(String fieldName, T value) {
        int col = findField(fieldName);
        if (col != -1) {
            set(col, value);
        }
    }

    public void addField(String fieldName, T value) {
        if (findField(fieldName) == -1) {
            names.add(fieldName);
            add(value);
        }
    }

    public void concat(RowSet<T> d1) {
        for (int i = 0; i < d1.names.size(); i++) {
            addField(d1.names.get(i), d1.get(i));
        }
    }
}
