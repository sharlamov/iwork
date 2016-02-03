package com.model;

import java.util.ArrayList;
import java.util.List;

public class DataSet extends ArrayList<Object[]> {

    private List<String> names;

    public DataSet(List<String> names, List<Object[]> list) {
        this.names = names;
        this.addAll(list);
    }

    private int findField(String fieldName) {
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

    public Object getValue(int row, int col) {
        return isEmpty() ? null : get(row)[col];
    }

    public Object getValueByName(String fieldName, int row) {
        int col = findField(fieldName);
        return col == -1 ? null : getValue(row, col);
    }

    public Object getValueByName(String fieldName, int row, Object defValue) {
        Object obj = getValueByName(fieldName, row);
        return obj == null ? defValue : obj;
    }

    public void setValueByName(String fieldName, int row, Object value) {
        int col = findField(fieldName);
        if (col != -1) {
            get(row)[col] = value;
        }
    }

    public List<String> getNames() {
        return names;
    }

    @Override
    public String toString() {
        return "DataSet{" +
                "names=" + names +
                '}';
    }
}
