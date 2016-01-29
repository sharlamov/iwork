package com.view.component.grid;

import com.util.Libra;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Date;

public class DataSetCellRenderer extends DefaultTableCellRenderer {

    private final boolean useBGColor;

    public DataSetCellRenderer(boolean useBGColor) {
        this.useBGColor = useBGColor;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int col) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        if(useBGColor){
            Color clr = ((DataSetTableModel)table.getModel()).getRowColor(row);
            label.setBackground(clr);

            if (table.isCellSelected(row, col)) {
                setBackground(Color.lightGray);
            } else
                setForeground(Color.black);
        }

        try {
            if (value != null) {

                if (value instanceof Date) {
                    label.setText(Libra.dateFormat.format(value));
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                } else if (value instanceof BigDecimal) {
                    label.setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return label;
    }

    // The following methods override the defaults for performance reasons
    public void validate() {
    }

    public void revalidate() {
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    }

    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    }
}
