package com.dao;

import com.model.DataSet;

import javax.swing.table.AbstractTableModel;
import java.util.*;

public class DataSetTableModel extends AbstractTableModel {

    DataSet dataSet;
    Map<Integer, Integer> columnMap;
    String[] names;
    int count;

    public DataSetTableModel(String[] names) {
        columnMap = new HashMap<Integer, Integer>(names.length);
        this.names = names;
    }

    public void setData(DataSet dataSet) {
        columnMap.clear();
        for (int i = 0; i < names.length; i++) {
            int index = dataSet.getNames().indexOf(names[i].toUpperCase());
            if (index != -1)
                columnMap.put(i, index);
        }
        this.dataSet = dataSet;
        count = dataSet.size();
        fireTableDataChanged();
    }

    public int getRowCount() {
        return count;
    }

    public int getColumnCount() {
        return names.length;
    }

    public String getColumnName(int column) {
        return names[column];
    }

    public Object getValueAt(int row, int column) {
        try {
            if (dataSet == null)
                return null;
            return dataSet.getValue(row, columnMap.get(column));
        } catch (NullPointerException ex) {
            System.out.println("Not found column: " + names[column]);
            ex.printStackTrace();
            return null;
        }
    }

    public Object getValueByFieldName(String name, int row) {
        return dataSet.getValueByName(name, row);
    }

    public DataSet getDataSetByRow(int row) {
        List<Object[]> lst = new ArrayList<Object[]>();
        Object[] line = new Object[names.length];
        for (int i = 0; i < names.length; i++) {
            line[i] = dataSet.isEmpty() || row == -1 ? null : getValueAt(row, i);
        }
        lst.add(line);
        return new DataSet(Arrays.asList(names), lst);
    }

}
