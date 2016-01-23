package com.dao;

import com.model.DataSet;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

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
        if (dataSet.isEmpty()) {
            lst.add(new Object[dataSet.getNames().size()]);
        } else {
            lst.add(dataSet.get(row));
        }
        return new DataSet(dataSet.getNames(), lst);
    }

    public Color getRowColor(int row) {
        Object bd = dataSet.getValueByName("bgcolor", row);
        Color clr = Color.white;
        if (bd != null) {
            switch (((BigDecimal) bd).intValue()) {
                case 6711039:
                    return Color.decode("#CC0000");
                case 13421823:
                    return Color.decode("#FF6699");
                case 13434828:
                    return Color.decode("#CCFFCC");
                case 5635925:
                    return Color.decode("#55FF55");
                default:
                    return Color.white;
            }
        } else return Color.white;
    }
}
