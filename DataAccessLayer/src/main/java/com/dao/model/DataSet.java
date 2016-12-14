package com.dao.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class DataSet extends ArrayList<Object[]> {

    private final LinkedHashMap<String, Integer> names;

    public DataSet(String... list) {
        this(Arrays.asList(list));
    }

    public DataSet(Collection<String> list) {
        names = new LinkedHashMap<>(list.size());
        int i = 0;
        for (String s : list)
            names.put(s.toUpperCase(), i++);
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
        if (obj instanceof BigDecimal)
            return (BigDecimal) obj;
        else if (obj == null)
            return BigDecimal.ZERO;
        else
            return new BigDecimal(obj.toString());
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
        add(data);
    }

    public void concat(DataSet d1) {
        int cnt = Math.min(size(), d1.size());
        if (cnt == 0)
            return;

        List<Integer> ints = new ArrayList<>(d1.names.size());
        d1.names.forEach((k, v) -> {
            int n = findField(k);
            if (n == -1) {
                names.put(k, names.size());
                ints.add(v);
            }
        });

        for (int i = 0; i < cnt; i++) {
            Object[] src = get(i);
            Object[] dest = new Object[src.length + ints.size()];
            System.arraycopy(src, 0, dest, 0, src.length);
            for (int i1 = 0; i1 < ints.size(); i1++) {
                dest[src.length + i1] = d1.getObject(i, ints.get(i1));
            }
            set(i, dest);
        }
    }

    public BigDecimal sum(String fieldName) {
        BigDecimal res = BigDecimal.ZERO;
        if (!isEmpty()) {
            int col = findField(fieldName);
            if (col > -1) {
                for (Object[] row : this) {
                    if (row[col] instanceof BigDecimal) {
                        res = res.add((BigDecimal) row[col]);
                    } else if (row[col] instanceof Double) {
                        res = res.add(BigDecimal.valueOf((Double) row[col]));
                    } else if (row[col] instanceof Integer) {
                        res = res.add(BigDecimal.valueOf((Integer) row[col]));
                    }
                }
            }
        }
        return res;
    }

    public DataSet copy() {
        DataSet newDataSet = new DataSet(names);
        for (int i = 0; i < size(); i++) {
            Object[] rOld = get(i);
            Object[] rNew = new Object[rOld.length];
            for (int y = 0; y < rOld.length; y++) {
                rNew[y] = cloneObject(rOld[y]);
            }
            newDataSet.add(rNew);
        }

        return newDataSet;
    }

    private Object cloneObject(Object obj) {
        if (obj instanceof BigDecimal) {
            return new BigDecimal(obj.toString());
        } else if (obj instanceof String) {
            return obj.toString() + "";
        } else if (obj instanceof CustomItem) {
            return ((CustomItem) obj).copy();
        } else if (obj instanceof Timestamp) {
            return Timestamp.valueOf(obj.toString());
        } else
            return obj;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DataSet))
            return false;
        DataSet set = (DataSet) o;

        if (size() != set.size() || names.size() != set.names.size())
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
        for (String fName : set.getCachedNames().keySet()) {
            for (int i = 0; i < minSize; i++) {
                setObject(i, fName, set.getObject(i, fName));
            }
        }
    }

    public DataSet getDataSetByRow(int row) {
        DataSet d = new DataSet(names);
        if (row > -1 && row < size())
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
