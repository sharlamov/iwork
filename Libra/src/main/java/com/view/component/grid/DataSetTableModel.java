package com.view.component.grid;

import com.model.DataSet;
import com.util.Fonts;
import com.util.Libra;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DataSetTableModel extends AbstractTableModel {

    private int bgColorColumn;
    private DataSet dataSet;
    private Map<Integer, Integer> columnMap;
    private GridField[] names;
    private String[] labels;
    private int count;

    public DataSetTableModel(GridField[] names) {
        columnMap = new HashMap<>(names.length);
        this.names = names;
        labels = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            labels[i] = Libra.lng(names[i].getName());
        }
    }

    public void publish(DataSet dataSet) {
        columnMap.clear();
        labels = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            labels[i] = Libra.lng(names[i].getName());
            int index = dataSet.findField(names[i].getName());
            if (index != -1)
                columnMap.put(i, index);
        }
        this.dataSet = dataSet;
        count = dataSet.size();
        bgColorColumn = dataSet.findField("bgcolor");
        fireTableDataChanged();
    }

    public int findColumn(String columnName) {
        for (int i = 0; i < getColumnCount(); i++) {
            if (columnName.equals(getFieldName(i))) {
                return i;
            }
        }
        return -1;
    }

    public int getRowCount() {
        return count;
    }

    public int getColumnCount() {
        return names.length;
    }

    public String getColumnName(int column) {
        return labels[column];
    }

    public String getFieldName(int column) {
        return names[column].getName();
    }

    public Object getValueAt(int row, int column) {
        try {
            return dataSet.get(row)[columnMap.get(column)];
        } catch (NullPointerException ex) {
            System.out.println("Not found column: " + names[column]);
            return null;
        }
    }

    public Object getValueByFieldName(String name, int row) {
        return dataSet.getObject(row, name);
    }

    public DataSet getDataSetByRow(int row) {
        return dataSet.getDataSetByRow(row);
    }

    public Double getSumByColumn(String fieldName) {
        return dataSet.sum(fieldName);
    }

    public Color getRowColor(int row) {
        if (bgColorColumn == -1)
            return Color.white;

        Object bd = dataSet.getObject(row, bgColorColumn);
        int val = bd != null ? Integer.parseInt(bd.toString()) : 0;

        switch (val) {
            case 6711039:
                return Fonts.color1;
            case 13421823:
                return Fonts.color2;
            case 13434828:
                return Fonts.color3;
            case 5635925:
                return Fonts.color4;
            case 0:
                return Fonts.color5;
            default:
                return Color.white;
        }
    }

    public int defineLocation(String fieldName, Object value) {
        return dataSet.location(fieldName, value);
    }
}
