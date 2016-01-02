package com.dao;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.Map;

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
            int index = dataSet.names.indexOf(names[i].toUpperCase());
            if (index != -1)
                columnMap.put(i, index);
        }
        this.dataSet = dataSet;
        count = dataSet.list.size();
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
            return dataSet.list.get(row)[columnMap.get(column)];
        } catch (NullPointerException ex) {
            System.out.println("Not found column: " + names[column]);
            ex.printStackTrace();
            return null;
        }
    }

    public Object getValueByFieldName(String name, int row){
        int col = dataSet.names.indexOf(name);
        if(col != -1 && row != -1 && !dataSet.list.isEmpty()){
            return dataSet.list.get(row)[col];
        }
        return null;
    }
}
