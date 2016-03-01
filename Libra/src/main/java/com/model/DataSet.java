package com.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DataSet extends ArrayList<Object[]> {

    private List<String> names;

    public DataSet(List<String> names, List<Object[]> list) {
        this.names = names;
        this.addAll(list);
    }

    public DataSet(List<String> names) {
        this.names = names;
    }

    public DataSet() {
        this.names = new ArrayList<String>();
        add(new Object[0]);
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

    public void addField(String fieldName, Object value) {
        if (findField(fieldName) == -1) {
            names.add(fieldName);
            for (int i = 0; i < size(); i++) {
                Object[] src = get(i);
                Object[] dest = new Object[src.length + 1];
                System.arraycopy(src, 0, dest, 0, src.length);
                dest[src.length] = value;
                set(i, dest);
            }
        }
    }

    public void addDataSet(DataSet d1) {
        for (int i = 0; i < d1.names.size(); i++) {
            addField(d1.names.get(i), d1.get(0)[i]);
        }
    }

    public BigDecimal getSumByColumn(String fieldName) {
        int colNumber = findField(fieldName);
        BigDecimal res = BigDecimal.ZERO;

        if (colNumber != -1) {
            for (Object[] row : this) {
                Object value = row[colNumber];
                if (value instanceof BigDecimal) {
                    res = res.add((BigDecimal) value);
                }
            }
        }
        return res;
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
