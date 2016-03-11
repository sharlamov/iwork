package com.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataSet extends ArrayList<Object[]> implements Copyable<DataSet> {

    private List<String> names;

    public DataSet(List<String> names, List<Object[]> list) {
        this.names = names;
        this.addAll(list);
    }

    public DataSet(List<String> names, Object[] list) {
        this.names = names;
        this.addAll(Collections.singletonList(list));
    }

    public DataSet(List<String> names) {
        this.names = names;
    }

    public DataSet() {
        this.names = new ArrayList<String>();
        add(new Object[0]);
    }

    public DataSet(DataSet dataSet) {
        this(dataSet.names, dataSet);
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

    public BigDecimal getNumberValue(String fieldName, int row) {
        Object obj = getValueByName(fieldName, row);
        return obj != null ? new BigDecimal(obj.toString()) : BigDecimal.ZERO;
    }

    public String getStringValue(String fieldName, int row) {
        Object obj = getValueByName(fieldName, row);
        return obj != null ? obj.toString() : "";
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

    public DataSet copy() {
        DataSet newDataSet = new DataSet(names);

        for (Object[] row : this) {
            Object[] newRow = new Object[row.length];

            for (int i = 0; i < row.length; i++) {
                if (row[i] instanceof BigDecimal) {
                    newRow[i] = new BigDecimal(row[i].toString());
                } else if (row[i] instanceof String) {
                    newRow[i] = row[i].toString() + "";
                } else if (row[i] instanceof CustomItem) {
                    newRow[i] = ((CustomItem) row[i]).copy();
                } else if (row[i] instanceof java.sql.Timestamp) {
                    newRow[i] = Timestamp.valueOf(row[i].toString());
                } else
                    newRow[i] = row[i];
            }
            newDataSet.add(newRow);
        }

        return newDataSet;
    }

    public boolean isDifferent(DataSet aDataSet){
        if(size() != aDataSet.size() || names.size() != aDataSet.names.size())
            return true;

        for (int i = 0; i < this.size(); i++) {
            Object[] fRow = get(i);
            Object[] aRow = aDataSet.get(i);

            for (int i1 = 0; i1 < fRow.length; i1++) {
                if(fRow[i1].equals(aRow[i1]))
                    return true;
            }
        }

        return false;
    }
}
