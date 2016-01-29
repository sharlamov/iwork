package com.view.component.grid;

import com.model.DataSet;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseListener;

public class DataGrid extends JPanel {

    private JTable tbl;
    private DataSetTableModel dtm;
    private int dataGridWith;


    public DataGrid(GridField[] names, boolean useBgColor) {
        super(new BorderLayout());
        dtm = new DataSetTableModel(names);
        tbl = new JTable(dtm);
        tbl.setRowSelectionAllowed(true);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbl.setDefaultRenderer(Object.class, new DataSetCellRenderer(useBgColor));
        tbl.setFillsViewportHeight(true);

        for (int i = 0; i < names.length; i++) {
            TableColumn column = tbl.getColumnModel().getColumn(i);
            int width = names[i].getSize();
            column.setPreferredWidth(width);
            dataGridWith += width;
        }

        JScrollPane scrollPane = new JScrollPane(tbl);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addListSelectionListener(ListSelectionListener listener) {
        tbl.getSelectionModel().addListSelectionListener(listener);
    }

    public void addMouseListener(MouseListener listener) {
        tbl.addMouseListener(listener);
    }

    public DataSet getDataSetByRow(int row) {
        return dtm.getDataSetByRow(row);
    }

    public void addToolBar(JToolBar tb) {
        add(tb, BorderLayout.NORTH);
    }

    public void publish(DataSet dataSet) {
        dtm.publish(dataSet);
    }


    public InputMap getIMap(int condition) {
        return tbl.getInputMap(condition);
    }

    public ActionMap getAMap() {
        return tbl.getActionMap();
    }

    public int getSelectedRow() {
        return tbl.getSelectedRow();
    }

    public void setSelectedRow(int row) {
        tbl.setRowSelectionInterval(row, row);
    }

    public Object getValueByFieldName(String id, int row) {
        return dtm.getValueByFieldName(id, row);
    }

    public int getRowCount() {
        return dtm.getRowCount();
    }

    public int getDataGridWith() {
        return dataGridWith;
    }

    public void setFocusable(boolean isFocusable) {
        tbl.setFocusable(isFocusable);
    }
}
