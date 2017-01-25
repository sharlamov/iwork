package com.test.table;

import com.test.comps.TProp;
import com.test.comps.interfaces.ISettings;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropModel extends AbstractTableModel {

    private List<Object[]> prop;
    private String[] names = {"Name", "Value"};
    private Map<String, Integer> map;

    public PropModel() {
        prop = new ArrayList<>();
        map = new HashMap<>();
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
        ((ISettings) comp).initProperties(info);
        prop.clear();
        map.clear();
        int i = 0;

        for (Map.Entry<String, Object> entry : info.entrySet()){
            prop.add(new Object[]{entry.getKey(), entry.getValue()});
            map.put(entry.getKey().toLowerCase(), i++);
        }

        fireTableDataChanged();
    }

    public void clear(){
        prop.clear();
        map.clear();
        fireTableDataChanged();
    }

    public void apply(Component comp) {
        TProp info = new TProp();
        prop.forEach(a -> info.put(String.valueOf(a[0]), a[1]));
        ((ISettings) comp).initComponent(info);
    }

    public void updateProp(String name, Object value) {
        int row = map.get(name.toLowerCase());
        setValueAt(value, row, 1);
        fireTableCellUpdated(row, 1);
    }
}
