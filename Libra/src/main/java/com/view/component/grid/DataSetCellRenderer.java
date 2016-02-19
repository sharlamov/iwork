package com.view.component.grid;

import com.util.Libra;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Map;

public class DataSetCellRenderer extends DefaultTableCellRenderer {

    private final boolean useBGColor;
    private final Map<Integer, Font> columnsFont;
    private DecimalFormat numberFormat = new DecimalFormat("#,###.##");

    public DataSetCellRenderer(boolean useBGColor, Map<Integer, Font> columnsFont) {
        this.useBGColor = useBGColor;
        this.columnsFont = columnsFont;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int col) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        if (useBGColor) {
            Color clr = ((DataSetTableModel) table.getModel()).getRowColor(row);
            label.setBackground(clr);

            if (table.isCellSelected(row, col)) {
                setBackground(Color.lightGray);
            } else
                setForeground(Color.black);
        }

        if (columnsFont != null && !columnsFont.isEmpty()) {
            if (columnsFont.containsKey(col)) {
                label.setFont(columnsFont.get(col));
            }
        }

        try {
            if (value != null) {

                if (value instanceof Date) {
                    label.setText(Libra.dateFormat.format(value));
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                } else if (value instanceof BigDecimal) {
                    label.setHorizontalAlignment(SwingConstants.RIGHT);
                    label.setText(numberFormat.format(value));
                } else {
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return label;
    }
}
