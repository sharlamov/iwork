package md.sh.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class DataSet extends ArrayList<List<Object>> implements IDataSet {

    private LinkedHashMap<String, Integer> names;

    public DataSet(String... list) {
        this(new ArrayList<>(Arrays.asList(list)));
    }

    public DataSet(List<String> list) {
        names = new LinkedHashMap<>(list.size());
        int i = 0;
        for (String str : list) {
            names.put(str.toUpperCase(), i++);
        }
    }

    public DataSet(Map<String, Integer> names) {
        this.names = (LinkedHashMap<String, Integer>) names;
    }

    public DataSet(Object... list) {
        int n = list.length / 2;
        names = new LinkedHashMap<>(n);
        add(new ArrayList<>(n));

        for (int i = 0; i < list.length; i += 2) {
            addField(list[i].toString(), list[i + 1]);
        }
    }


    public List<Object> getFirst() {
        return isEmpty() ? null : get(0);
    }

    public int findField(String fieldName) {
        Integer col = names.get(fieldName.toUpperCase());
        return col == null ? -1 : col;
    }

    public String getColName(int col) {//map.values().toArray()[0]
        for (Map.Entry<String, Integer> entry : names.entrySet()) {
            if (entry.getValue() == col)
                return entry.getKey();
        }
        return null;
    }

    public int getColCount() {
        return names.size();
    }

    public Object getObject(int row, int col) {
        return isEmpty() || col < 0 ? null : get(row).get(col);
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
        if (!isEmpty()) {
            int col = findField(fieldName);
            if (col != -1)
                get(row).set(col, value);
        }
    }

    public void addField(String fieldName, Object value) {
        if (findField(fieldName) == -1) {
            names.put(fieldName.toUpperCase(), names.size());
            forEach(row -> row.add(value));
        }
    }

    public void addRow(Object... data) {
        add(new ArrayList<>(Arrays.asList(data)));
    }

    public void concat(DataSet d1) {
        d1.names.forEach((k, v) -> addField(k, d1.get(0).get(v)));
    }

    public Double sum(String fieldName) {
        int colNumber = findField(fieldName);
        return colNumber != -1 ? stream().mapToDouble(p -> Double.parseDouble(p.get(colNumber).toString())).sum() : 0d;
    }

    public DataSet copy() {
        DataSet newDataSet = new DataSet(names);
        for (List<Object> row : this) {
            List<Object> newRow = row.stream()
                    .map(cell -> {
                        if (cell instanceof BigDecimal) {
                            return new BigDecimal(cell.toString());
                        } else if (cell instanceof String) {
                            return cell.toString() + "";
                        } else if (cell instanceof CustomItem) {
                            return ((CustomItem) cell).copy();
                        } else if (cell instanceof java.sql.Timestamp) {
                            return Timestamp.valueOf(cell.toString());
                        } else
                            return cell;
                    }).collect(Collectors.toList());
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

    public void update(DataSet set) {
        int minSize = Math.min(size(), set.size());
        for (Map.Entry<String, Integer> e : set.getCachedNames().entrySet()) {
            for (int i = 0; i < minSize; i++) {
                setObject(i, e.getKey(), e.getValue());
            }
        }
    }

    public List<DataSet> groupBy(String fieldName) {
        int col = findField(fieldName.toUpperCase());
        if (col > -1) {
            Map<Object, List<List<Object>>> collect = stream().collect(Collectors.groupingBy(row -> row.get(col), LinkedHashMap::new, Collectors.toList()));
            List<DataSet> result = new ArrayList<>(collect.size());
            for (List<List<Object>> sets : collect.values()) {
                DataSet data = new DataSet(getCachedNames());
                data.addAll(sets);
                result.add(data);
            }
            return result;
        } else
            return Collections.emptyList();
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
        for (List<Object> objects : this) {
            sb.append("/").append(objects);
        }
        return sb.toString();
    }
}
