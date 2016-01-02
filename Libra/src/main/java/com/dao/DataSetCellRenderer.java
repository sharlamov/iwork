package com.dao;

import com.util.Libra;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by sharlamov on 27.12.2015.
 */
public class DataSetCellRenderer extends DefaultTableCellRenderer {
    // This method is called each time a cell in a column
    // using this renderer needs to be rendered.
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowIndex, vColIndex);
        try {
            if (value != null) {

                if (value instanceof Date) {
                    label.setText(Libra.dateFormat.format(value));
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                }else if (value instanceof BigDecimal) {
                    label.setHorizontalAlignment(SwingConstants.RIGHT);
                }else {
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
