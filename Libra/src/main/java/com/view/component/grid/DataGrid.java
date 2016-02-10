package com.view.component.grid;

import com.enums.SearchType;
import com.model.DataSet;
import com.service.LibraService;
import com.util.Libra;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class DataGrid extends JPanel {

    private LibraService libraService;
    private SearchType searchType;
    private JTable tbl;
    private DataSetTableModel dtm;
    private int dataGridWith;
    private Map<String, Object> params;

    public DataGrid(LibraService libraService, SearchType searchType, GridField[] names, boolean useBgColor) {
        super(new BorderLayout());
        this.libraService = libraService;
        this.searchType = searchType;
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

    public void setHeaderFont(Font font) {
        tbl.getTableHeader().setFont(font);
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

    public int select(Map<String, Object> params) throws Exception {
        this.params = params;
        DataSet d = libraService.selectDataSet(searchType, params);
        dtm.publish(d);
        return d.size();
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

    public int filter(String filterString) throws Exception {
        DataSet d = libraService.filterDataSet(searchType, params, filterString);
        dtm.publish(d);
        return d.size();
    }

    public int filter() throws Exception {
        int r = tbl.getSelectedRow();
        int c = tbl.getSelectedColumn();
        if (r != -1 && c != -1) {
            String columnName = dtm.getFieldName(c);
            Object val = dtm.getValueAt(r, c);
            String textVal = "";

            if (val instanceof Date) {
                textVal = Libra.dateFormat.format((Date) val);
            } else if (val != null) {
                textVal = val.toString();
            }

            String s = (String) JOptionPane.showInputDialog(
                    getFrame(), "Что искать?", "Поиск",
                    JOptionPane.PLAIN_MESSAGE, null, null, textVal);

            System.out.println(s);

            if (s != null && !s.isEmpty()) {
                DataSet d = libraService.filterDataSet(searchType, params, Collections.singletonMap(columnName, "%" + s.toLowerCase() + "%"));
                dtm.publish(d);
                if (!d.isEmpty()) {
                    setSelectedRow(0);
                    tbl.setColumnSelectionInterval(c, c);
                    return d.size();
                }
            }
        }
        return 0;
    }

    private Component getFrame() {
        Component cmp = getParent();
        while (!(cmp instanceof JFrame)) {
            cmp = cmp.getParent();
        }
        return cmp;
    }
}
