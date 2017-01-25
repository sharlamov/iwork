package com.test.comps.grid;


import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Date;

class DataSetCellRenderer extends DefaultTableCellRenderer {

    private GridProps props;
    /* private final boolean useBGColor;
     private final java.util.List<GridProps.Col> cols;
     private final DecimalFormat numberFormat;
     private final SimpleDateFormat dateFormat;*/
    private int lastRow = -1;
    private Color clr;

    /*public DataSetCellRenderer(boolean useBGColor, Font[] columnsFont) {
        this.useBGColor = useBGColor;
        this.columnsFont = columnsFont;
        numberFormat = new DecimalFormat("#,###.##");
        dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    }*/

    DataSetCellRenderer(GridProps props) {
        this.props = props;
        /*this.useBGColor = props.useBgColor;
        this.cols = props.columns;
        numberFormat = new DecimalFormat("#,###.##");
        dateFormat = new SimpleDateFormat("dd.MM.yyyy");*/
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int col) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        int sorterRow = !table.getAutoCreateRowSorter() || table.getRowSorter() == null ? row
                : table.getRowSorter().convertRowIndexToModel(row);

        if (props.useBgColor) {
            if (lastRow != row) {
                clr = ((DataSetTableModel) table.getModel()).getRowColor(sorterRow);
                lastRow = row;
            }
            label.setBackground(clr);

            if (table.isCellSelected(sorterRow, col)) {
                setBackground(clr.darker());
            } else
                setForeground(Color.black);
        }

        if (col < props.size()) {
            label.setFont(props.getColFont(col));
        }

        try {
            if (value != null) {
                if (value instanceof Date) {
                    label.setText(props.toStdFormat(value));
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                } else if (value instanceof Number) {
                    label.setHorizontalAlignment(SwingConstants.RIGHT);
                    label.setText(props.toStdFormat(value));
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
