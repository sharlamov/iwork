package com.view.component.grid;

import com.model.DataSet;
import com.util.Libra;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSetTableModel extends AbstractTableModel {

    private DataSet dataSet;
    private Map<Integer, Integer> columnMap;
    private GridField[] names;
    private String[] labels;
    private int count;

    public DataSetTableModel(GridField[] names) {
        columnMap = new HashMap<Integer, Integer>(names.length);
        this.names = names;
        labels = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            labels[i] = Libra.translate(names[i].getName());
        }
    }

    public void publish(DataSet dataSet) {
        columnMap.clear();
        labels = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            labels[i] = Libra.translate(names[i].getName());
            int index = dataSet.getNames().indexOf(names[i].getName().toUpperCase());
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
        return labels[column];
    }

    public String getFieldName(int column) {
        return names[column].getName();
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
        if (row == -1 || dataSet.isEmpty()) {
            lst.add(new Object[dataSet.getNames().size()]);
        } else {
            lst.add(dataSet.get(row));
        }
        return new DataSet(dataSet.getNames(), lst);
    }

    public Color getRowColor(int row) {
        Object bd = dataSet.getValueByName("bgcolor", row);
        if (bd != null) {
            switch (((BigDecimal) bd).intValue()) {
                case 6711039:
                    return Color.decode("#CC3333");
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
        } else return Color.white;
    }
}
