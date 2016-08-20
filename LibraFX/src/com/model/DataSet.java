package com.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class DataSet extends ArrayList<Object[]> implements Copyable<DataSet> {

    private List<String> names;

    public DataSet(int length) {
        names = new ArrayList<>(length);
        add(new Object[length]);
    }

    public DataSet(String... list) {
        this(list.length);
        Collections.addAll(names, list);
    }

    public DataSet(Object... list) {
        this(list.length / 2);
        for (int i = 0, pos = 0; i < list.length; i++) {
            if (i % 2 == 0) {
                names.add(list[i].toString());
            } else {
                get(0)[pos++] = list[i];
            }
        }
    }

    public DataSet(Collection<String> list) {
        names = new ArrayList<>(list);
    }

    public int findField(String fieldName) {
        for (int i = 0; i < names.size(); i++) {
            if (fieldName.equalsIgnoreCase(names.get(i)))
                return i;
        }
        return -1;
    }

    public String getColName(int col) {
        return names.isEmpty() ? null : names.get(col);
    }

    public int getColCount() {
        return names.size();
    }

    public Object getObject(int row, int col) {
        return isEmpty() ? null : get(row)[col];
    }

    public Object getObject(String fieldName) {
        if (isEmpty()) {
            return null;
        } else {
            int col = findField(fieldName);
            return col == -1 ? null : getObject(0, col);
        }
    }

    public BigDecimal getDecimal(String fieldName) {
        Object obj = getObject(fieldName);
        return obj != null ? new BigDecimal(obj.toString()) : BigDecimal.ZERO;
    }

    public int getInt(String fieldName) {
        return getDecimal(fieldName).intValue();
    }

    public String getString(String fieldName) {
        Object obj = getObject(fieldName);
        return obj != null ? obj.toString() : "";
    }

    public Date getDate(String fieldName) {
        Object obj = getObject(fieldName);
        return obj instanceof Date ? (Date) obj : new Date();
    }

    public CustomItem getItem(String fieldName) {
        Object obj = getObject(fieldName);
        return obj instanceof CustomItem ? (CustomItem) obj : null;
    }

    public void setObject(String fieldName, Object value) {
        int col = findField(fieldName);
        if (col != -1)
            get(0)[col] = value;
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

    public void concat(DataSet d1) {
        for (int i = 0; i < d1.names.size(); i++) {
            addField(d1.names.get(i), d1.get(0)[i]);
        }
    }

    public BigDecimal sum(String fieldName) {
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

    public DataSet copy() {
        DataSet newDataSet = new DataSet(new ArrayList<>(names));
        newDataSet.clear();

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

    @Override
    public boolean equals(Object o) {
        DataSet set = (DataSet) o;

        if (set == null || size() != set.size() || names.size() != set.names.size())
            return false;

        for (int i = 0; i < this.size(); i++) {
            Object[] fRow = get(i);
            Object[] aRow = set.get(i);

            int cnt = (fRow.length + aRow.length) / 2;
            for (int y = 0; y < cnt; y++) {
                if (aRow[y] != null || fRow[y] != null) {
                    if (aRow[y] == null || fRow[y] == null) {
                        return false;
                    } else if (!fRow[y].equals(aRow[y])) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void updateFirst(DataSet set) {
        for (int i = 0; i < set.size(); i++) {
            for (String s : set.getNames()) {
                setObject(s, set.getObject(s));
            }
        }
    }

    public List<String> getNames() {
        return names;
    }
}
