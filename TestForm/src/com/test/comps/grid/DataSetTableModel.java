package com.test.comps.grid;

import com.dao.model.DataSet;
import com.test.comps.interfaces.ITranslator;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class DataSetTableModel extends AbstractTableModel {

    private int bgColorColumn;
    private DataSet dataSet;
    private Map<Integer, Integer> columnMap;
    private GridProps cols;
    private String[] labels;
    private int count;
    private ITranslator translator;
    private Map<Number, Color> colors;

    public void initSettings(GridProps cols, ITranslator translator) {
        this.cols = cols;
        this.translator = translator;
        columnMap = new HashMap<>(cols.size());
        labels = new String[cols.size()];
        for (int i = 0; i < cols.size(); i++) {
            labels[i] = translator != null ? translator.lng(cols.getColName(i)) : cols.getColName(i);
        }
        initFonts();
        fireTableStructureChanged();
    }

    private void initFonts() {
        colors = new HashMap<>(5);
        colors.put(6711039, Color.decode("#FF2020"));
        colors.put(13421823, Color.decode("#FF9999"));
        colors.put(13434828, Color.decode("#CCFFCC"));
        colors.put(5635925, Color.decode("#55FF55"));
        colors.put(0, Color.decode("#FFFF66"));
    }

    public void publish(DataSet dataSet) {
        columnMap.clear();
        labels = new String[cols.size()];
        for (int i = 0; i < cols.size(); i++) {
            labels[i] = translator.lng(cols.getColName(i));
            int index = dataSet.findField(cols.getColName(i));
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
        return cols == null ? 0 : cols.size();
    }

    public String getColumnName(int column) {
        return labels[column];
    }

    public String getFieldName(int i) {
        return cols.getColName(i);
    }

    public Object getValueAt(int row, int col) {
        try {
            return dataSet.get(row)[columnMap.get(col)];
        } catch (NullPointerException ex) {
            System.out.println("Not found column: " + cols.getColName(col));
            return null;
        }
    }

    public Object getValueByFieldName(String name, int row) {
        return dataSet.getObject(row, name);
    }

    public DataSet getDataSetByRow(int row) {
        return dataSet.getDataSetByRow(row);
    }

    public BigDecimal getSumByColumn(String fieldName) {
        return dataSet.sum(fieldName);
    }

    public Color getRowColor(int row) {
        Color clr = Color.white;
        if (bgColorColumn != -1) {
            Object bd = dataSet.getObject(row, bgColorColumn);
            if (bd instanceof Number) {
                if (colors.containsKey(bd))
                    clr = colors.get(bd);
            }
        }
        return clr;
    }

    public int defineLocation(String fieldName, Object value) {
        return dataSet.location(fieldName, value);
    }
}
