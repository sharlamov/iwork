package com.reporting.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*it's class necessary to refactor!!!*/
public class PivotTable {
    private List<Object[]> resultSet;
    private List<List<Object>> pivotHeaders = new ArrayList<>();
    private List<Object> pivotSummary;

    public PivotTable(int[] fkey, int[] pkey, int[] skey, List<Object[]> data) {
        long t = System.currentTimeMillis();
        for (int ignored : pkey) pivotHeaders.add(new ArrayList<>());

        List<Object[]> keyHeaders = new ArrayList<>();
        for (Object[] row : data) {
            /*prepare pivot fields*/
            for (int i = 0; i < pkey.length; i++) {
                if (!pivotHeaders.get(i).contains(row[pkey[i]]))
                    pivotHeaders.get(i).add(row[pkey[i]]);
            }
            /*prepare pivot fields*/

            /*prepare key fields*/
            Object keys[] = getSubArray(row, fkey);

            if (!conainsGroupList(keyHeaders, keys)) {
                keyHeaders.add(keys);
            }
            /*work with sum fields*/
        }

        /*count*/
        int countResultSet = fkey.length;
        int tempCount = 0;
        for (List<Object> lst : pivotHeaders) {
            if (tempCount != 0)
                tempCount *= lst.size();
            else {
                tempCount = lst.size();
            }
        }
        countResultSet += tempCount * skey.length;
        /*count*/

        /*copy keys*/
        resultSet = new ArrayList<>();
        for (Object[] keyHeader : keyHeaders) {
            Object[] row = new Object[countResultSet];
            System.arraycopy(keyHeader, 0, row, 0, keyHeader.length);
            resultSet.add(row);
        }
        /*copy keys*/

        for (Object[] row : data) {
            for (int i = 0; i < skey.length; i++) {
                BigDecimal bd = (BigDecimal) row[skey[i]];
                Object keys[] = getSubArray(row, fkey);
                Object pkeys[] = getSubArray(row, pkey);
                int x = getX(keyHeaders, keys);
                int y = getY(pivotHeaders, pkeys, fkey.length, i, skey.length);
                if (resultSet.get(x)[y] != null) {
                    BigDecimal newVal = ((BigDecimal) resultSet.get(x)[y]).add(bd);
                    resultSet.get(x)[y] = newVal;
                } else {
                    resultSet.get(x)[y] = bd;
                }
            }
        }

        prepareHeader();
        prepareSummary(fkey.length, countResultSet);

        System.out.println(System.currentTimeMillis() - t);
    }

    private void prepareSummary(int key, int count) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        pivotSummary = new ArrayList<>();
        Object[] row = new Object[count - key];

        for (Object[] objects : resultSet) {
            for (int i = key, j = 0; i < objects.length; i++, j++) {
                if (row[j] == null) {
                    row[j] = objects[i];
                } else {
                    if(objects[i] != null) {
                        BigDecimal oldValue = (BigDecimal) row[j];
                        BigDecimal newValue = (BigDecimal) objects[i];
                        row[j] = oldValue.add(newValue);
                    }
                }
            }
        }

        for (int i = 0; i < row.length; i++) {
            if(row[i] != null)
                row[i] = df.format(row[i]);
        }
        pivotSummary = Arrays.asList(row);
    }

    int getX(List<Object[]> list, Object[] key) {
        for (int i = 0; i < list.size(); i++) {
            if (Arrays.equals(list.get(i), key))
                return i;
        }
        return -1;
    }

    public void prepareHeader() {
        int n = pivotHeaders.size();
        if (n > 1) {
            for (int i = 0; i < n - 1; i++) {
                int count = pivotHeaders.get(i).size();
                List<Object> lst = pivotHeaders.remove(i + 1);
                List<Object> newList = new ArrayList<>();
                for (int j = 0; j < count; j++) {
                    newList.addAll(lst);
                }
                pivotHeaders.add(i + 1, newList);
            }
        }
    }

    int getY(List<List<Object>> list, Object[] pkey, int prefix, int postfix, int sCount) {
        int res = 0;
        for (int i = 0; i < list.size(); i++) {
            int ind = list.get(i).indexOf(pkey[i]);
            if (i + 1 < list.size()) {
                int koef = list.get(i + 1).size();
                res += koef * ind * sCount;
            } else {
                res += ind * sCount;
            }
        }
        return prefix + res + postfix;
    }

    Object[] getSubArray(Object[] row, int[] indexes) {
        Object keys[] = new Object[indexes.length];
        for (int i = 0; i < indexes.length; i++) {
            keys[i] = row[indexes[i]];
        }
        return keys;
    }

    public boolean conainsGroupList(List<Object[]> list, Object[] obj) {
        for (Object[] row : list) {
            if (Arrays.equals(row, obj))
                return true;
        }
        return false;
    }

    public List<Object[]> getResultSet() {
        return resultSet;
    }

    public void setResultSet(List<Object[]> resultSet) {
        this.resultSet = resultSet;
    }

    public void print() {
        System.out.println(pivotHeaders);

        for (Object[] objects : resultSet)
            System.out.println(Arrays.deepToString(objects));

        System.out.println(pivotSummary);
    }

    public List<List<Object>> getPivotHeaders() {
        return pivotHeaders;
    }

    public List<Object> getPivotSummary() {
        return pivotSummary;
    }
}
