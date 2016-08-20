package com.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class DataSet extends ArrayList<Object[]> {

    private LinkedHashMap<String, Integer> names;

    public DataSet(String... list) {
        this(Arrays.asList(list));
    }

    public DataSet(Collection<String> list) {
        names = new LinkedHashMap<>(list.size());
        list.forEach(s -> names.put(s.toUpperCase(), names.size()));
    }

    public DataSet(Map<String, Integer> names) {
        this.names = (LinkedHashMap<String, Integer>) names;
    }

    public DataSet(Map<String, Integer> names, List<Object[]> data) {
        this(names);
        this.addAll(data);
    }

    public static DataSet init(Object... list) {
        Map<String, Integer> cols = new LinkedHashMap<>(list.length);
        Object[] row = new Object[list.length];

        for (int i = 0; i < list.length; i++) {
            if (i % 2 == 0) {
                cols.put(list[i].toString().toUpperCase(), cols.size());
            } else {
                row[i / 2] = list[i];
            }
        }

        DataSet d = new DataSet(cols);
        d.add(row);
        return d;
    }

    public int findField(String fieldName) {
        Integer col = names.get(fieldName.toUpperCase());
        return col == null ? -1 : col;
    }

    public int getColCount() {
        return names.size();
    }

    public Object getObject(int row, int col) {
        return isEmpty() || col < 0 ? null : get(row)[col];
    }

    public Object getObject(String fieldName) {
        return getObject(0, fieldName);
    }

    public Object getObject(int row, String fieldName) {
        return getObject(row, findField(fieldName));
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
        setObject(0, fieldName, value);
    }

    public void setObject(int row, String fieldName, Object value) {
        int col = findField(fieldName);
        if (col != -1) {
            if (size() == row) {
                Object[] newRow = new Object[getColCount()];
                newRow[col] = value;
                add(newRow);
            } else if (size() > row) {
                get(row)[col] = value;
            }
        }
    }

    public void addField(String fieldName, Object value) {
        if (findField(fieldName) == -1) {
            names.put(fieldName.toUpperCase(), names.size());
            if (isEmpty()) {
                setObject(fieldName, value);
            } else {
                for (int i = 0; i < size(); i++) {
                    Object[] src = get(i);
                    Object[] dest = new Object[src.length + 1];
                    System.arraycopy(src, 0, dest, 0, src.length);
                    dest[src.length] = value;
                    set(i, dest);
                }
            }
        }
    }

    public void addRow(Object... data) {
        add(data);//?
    }

    public void concat(DataSet d1) {
        d1.names.forEach((k, v) -> addField(k, getObject(0, v)));
    }

    public Double sum(String fieldName) {
        int colNumber = findField(fieldName);
        long l = System.currentTimeMillis();
        double d = colNumber != -1 ? stream().map(row -> row[colNumber])
                .filter(val -> val != null).mapToDouble(p -> Double.parseDouble(p.toString())).sum() : 0d;
        System.out.println((System.currentTimeMillis() - l) + " sum ");
        return d;
    }

    public DataSet copy() { //? lambda
        DataSet newDataSet = new DataSet(names);
        stream().forEach(row -> {
            Object[] nRow = new Object[row.length];
            for (int i = 0; i < row.length; i++) {
                nRow[i] = cloneObject(row[i]);
            }
            newDataSet.add(nRow);
        });

        return newDataSet;
    }

    private Object cloneObject(Object obj) {
        if (obj instanceof BigDecimal) {
            return new BigDecimal(obj.toString());
        } else if (obj instanceof String) {
            return obj.toString() + "";
        } else if (obj instanceof CustomItem) {
            return ((CustomItem) obj).copy();
        } else if (obj instanceof java.sql.Timestamp) {
            return Timestamp.valueOf(obj.toString());
        } else
            return obj;
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

    public void update(DataSet set) {
        int minSize = Math.min(size(), set.size());
        for (Map.Entry<String, Integer> e : set.getCachedNames().entrySet()) {
            for (int i = 0; i < minSize; i++) {
                setObject(i, e.getKey(), e.getValue());
            }
        }
    }

    public DataSet getDataSetByRow(int row) {
        DataSet d = new DataSet(names);
        if (row != -1 && !isEmpty())
            d.add(get(row));

        return d;
    }

    public List<DataSet> groupBy(String fieldName) {
        int col = findField(fieldName.toUpperCase());
        if (col > -1) {
            Map<Object, List<Object[]>> collect = stream().collect(Collectors.groupingBy(row -> row[col], LinkedHashMap::new, Collectors.toList()));
            return collect.values().stream().map(sets -> new DataSet(names, sets)).collect(Collectors.toList());
        } else
            return Collections.emptyList();
    }

    public int location(String fieldName, Object value) {
        int n = findField(fieldName);
        if (n != -1) {
            for (int i = 0; i < size(); i++) {
                if (value.equals(get(i)[n]))
                    return i;
            }
        }
        return -1;
    }

    public <T, K> Map<T, K> toSimpleMap() {
        return isEmpty() ? Collections.emptyMap() : stream().collect(Collectors.toMap(x -> (T) x[0], x -> (K) x[1]));
    }

    public List<String> getNames() {
        return new ArrayList<>(names.keySet());
    }

    public Map<String, Integer> getCachedNames() {
        return names;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        forEach(row -> sb.append("/").append(Arrays.toString(row)));
        return sb.toString();
    }
}
