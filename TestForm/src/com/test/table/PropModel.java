package com.test.table;

import com.test.comps.IDesign;
import com.test.comps.TProp;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PropModel extends AbstractTableModel {

    private List<Object[]> prop;
    private String[] names = {"Name", "Value"};

    public PropModel() {
        prop = new ArrayList<>();
    }

    @Override
    public String getColumnName(int column) {
        return names[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        prop.get(rowIndex)[columnIndex] = aValue;
    }

    @Override
    public int getRowCount() {
        return prop.size();
    }

    @Override
    public int getColumnCount() {
        return names.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return prop.get(rowIndex)[columnIndex];
    }

    public void publish(Component comp) {
        TProp info = new TProp();
        ((IDesign) comp).initProperties(info);
        prop.clear();

        for (Map.Entry<String, Object> entry : info.entrySet())
            prop.add(new Object[]{entry.getKey(), entry.getValue()});

        fireTableDataChanged();
    }
}
