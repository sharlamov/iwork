package com.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class DataSet2 extends ArrayList<RowSet> implements Copyable<DataSet2> {

    private List<String> names;

    public DataSet2(List<String> names, List<RowSet> list) {
        this.names = names;
        this.addAll(list);
    }

    public DataSet2(String names) {
        this.names = Arrays.asList(names.split(","));
    }

    public DataSet2(List<String> names) {
        this.names = names;
        add(new RowSet());
    }

    public DataSet2() {
        this.names = new ArrayList<>();
        add(new RowSet());
    }

    public DataSet2(DataSet2 dataSet) {
        this.addAll(dataSet);
    }

    public DataSet2(String fieldName, Object value) {
        names = Collections.singletonList(fieldName);
        add(new RowSet(value));
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
        return isEmpty() ? null : get(row).get(col);
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

    public Date getDateValue(String fieldName, int row) {
        Object obj = getValueByName(fieldName, row);
        return obj instanceof Date ? (Date) obj : new Date();
    }

    public void setValueByName(String fieldName, int row, Object value) {
        int col = findField(fieldName);
        if (col != -1) {
            if (isEmpty()) {
                add(new RowSet(names.size()));
            }
            get(row).set(col, value);
        }
    }

    public void addField(String fieldName, Object value) {
        if (findField(fieldName) == -1) {
            names.add(fieldName);
            for (List<Object> row : this) {
                row.add(value);
            }
        }
    }

    public void addDataSet(DataSet2 d1) {
        for (int i = 0; i < d1.names.size(); i++) {
            addField(d1.names.get(i), d1.get(0).get(i));
        }
    }

    public BigDecimal getSumByColumn(String fieldName) {
        int colNumber = findField(fieldName);
        BigDecimal res = BigDecimal.ZERO;

        if (colNumber != -1) {
            for (List<Object> row : this) {
                Object value = row.get(colNumber);
                if (value instanceof BigDecimal) {
                    res = res.add((BigDecimal) value);
                }
            }
        }
        return res;
    }

    @Override
    public String toString() {
        return "DataSet{" +
                "names=" + names +
                '}';
    }

    public DataSet2 copy() {
        DataSet2 newDataSet = new DataSet2(new ArrayList<>(names));
        newDataSet.clear();

        for (List<Object> row : this) {
            int rSize = row.size();
            RowSet newRow = new RowSet(rSize);

            for (int i = 0; i < rSize; i++) {
                if (row.get(i) instanceof BigDecimal) {
                    newRow.set(i, new BigDecimal(row.get(i).toString()));
                } else if (row.get(i) instanceof String) {
                    newRow.set(i, row.get(i).toString() + "");
                } else if (row.get(i) instanceof CustomItem) {
                    newRow.set(i, ((CustomItem) row.get(i)).copy());
                } else if (row.get(i) instanceof Timestamp) {
                    newRow.set(i, Timestamp.valueOf(row.get(i).toString()));
                } else
                    newRow.set(i, row.get(i));
            }
            newDataSet.add(newRow);
        }

        return newDataSet;
    }

    public boolean isEqual(DataSet2 set) {
        if (set == null || size() != set.size() || names.size() != set.names.size())
            return false;

        for (int i = 0; i < this.size(); i++) {
            List<Object> fRow = get(i);
            List<Object> aRow = set.get(i);

            int cnt = (fRow.size() + aRow.size()) / 2;
            for (int y = 0; y < cnt; y++) {
                if (aRow.get(y) != null || fRow.get(y) != null) {
                    if (aRow.get(y) == null || fRow.get(y) == null) {
                        return false;
                    } else if (!fRow.get(y).equals(aRow.get(y))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void update(DataSet2 set) {
        for (int i = 0; i < set.size(); i++) {
            for (String s : set.getNames()) {
                setValueByName(s, i, set.getValueByName(s, i));
            }
        }
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }
}
