package com.controllers;

import com.model.DataSet;
import com.model.settings.GridField;
import com.service.LangService;

import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbTable {

    private DataSet dataSet;
    private Map<Integer, Integer> columnMap;
    private GridField[] names;
    private String[] labels;
    private int count;

    public DbTable(String query, GridField[] names) {
        columnMap = new HashMap<Integer, Integer>(names.length);
        this.names = names;
        labels = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            labels[i] = LangService.trans(names[i].getName());
        }
    }

    public void publish(DataSet dataSet) {
        columnMap.clear();
        labels = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            labels[i] = LangService.trans(names[i].getName());
            int index = dataSet.getNames().indexOf(names[i].getName().toUpperCase());
            if (index != -1)
                columnMap.put(i, index);
        }
        this.dataSet = dataSet;
        count = dataSet.size();
        //fireTableDataChanged();
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
            return dataSet.getValue(row, columnMap.get(column));
        } catch (NullPointerException ex) {
            System.out.println("Not found column: " + names[column]);
            return null;
        }
    }

    public Object getValueByFieldName(String name, int row) {
        return dataSet.getValueByName(name, row);
    }

    public DataSet getDataSetByRow(int row) {
        List<Object[]> lst = new ArrayList<Object[]>();
        if (row == -1 || dataSet.isEmpty()) {
            lst.add(new Object[dataSet.getNames().size()]);
        } else {
            lst.add(dataSet.get(row));
        }
        return new DataSet(dataSet.getNames(), lst);
    }

    public BigDecimal getSumByColumn(String fieldName) {
        return dataSet.getSumByColumn(fieldName);
    }

    public Color getRowColor(int row) {
        BigDecimal bd = dataSet.getNumberValue("bgcolor", row);
        switch (bd.intValue()) {
            case 6711039:
                return Color.decode("#FF2020");
            case 13421823:
                return Color.decode("#FF9999");
            case 13434828:
                return Color.decode("#CCFFCC");
            case 5635925:
                return Color.decode("#55FF55");
            case 0:
                return Color.decode("#FFFF66");
            default:
                return Color.white;
        }
    }

    public int defineLocation(String fieldName, Object value) {
        int n = dataSet.findField(fieldName);
        if (n != -1) {
            for (int i = 0; i < dataSet.size(); i++) {
                if (value.equals(dataSet.get(i)[n])) {
                    return i;
                }
            }
        }
        return -1;
    }
}
