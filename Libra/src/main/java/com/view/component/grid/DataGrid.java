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
import java.math.BigDecimal;
import java.util.*;

public class DataGrid extends JPanel {

    private LibraService libraService;
    private String sql;
    private JTable tbl;
    private DataSetTableModel dtm;
    private int dataGridWith;
    private Map<String, Object> params;
    private Map<Integer, Font> columnFonts = new HashMap<Integer, Font>();
    private SummaryRow summaryRow;
    private Map<String, String> summaryMap;

    public DataGrid(LibraService libraService, SearchType searchType, GridField[] names, boolean useBgColor) {
        super(new BorderLayout());
        this.libraService = libraService;
        this.sql = searchType.getSql();
        dtm = new DataSetTableModel(names);
        tbl = new JTable(dtm);
        tbl.setRowSelectionAllowed(true);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbl.setDefaultRenderer(Object.class, new DataSetCellRenderer(useBgColor, columnFonts));
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

    public DataGrid(DataGridSetting lSetting, LibraService libraService) {
        super(new BorderLayout());

        this.libraService = libraService;
        this.sql = lSetting.getQuery();
        dtm = new DataSetTableModel(lSetting.getNames());
        tbl = new JTable(dtm);
        tbl.setRowSelectionAllowed(true);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbl.setDefaultRenderer(Object.class, new DataSetCellRenderer(lSetting.isUseBgColor(), columnFonts));
        tbl.setFillsViewportHeight(true);
        tbl.setAutoCreateRowSorter(lSetting.isUseSorting());

        for (int i = 0; i < lSetting.getNames().length; i++) {
            TableColumn column = tbl.getColumnModel().getColumn(i);
            int width = lSetting.getNames()[i].getSize();
            column.setPreferredWidth(width);
            dataGridWith += width;
        }

        JScrollPane scrollPane = new JScrollPane(tbl);
        add(scrollPane, BorderLayout.CENTER);

        if (lSetting.isUseSummary()) {
            summaryMap = new LinkedHashMap<String, String>();
            summaryRow = new SummaryRow();
            add(summaryRow, BorderLayout.SOUTH);
        }
    }


    public void refreshSummary() {
        if (summaryRow != null) {
            summaryMap.clear();
            BigDecimal b = dtm.getSumByColumn("masa_brutto");
            BigDecimal t = dtm.getSumByColumn("masa_tara");
            BigDecimal n = dtm.getSumByColumn("masa_netto");

            summaryMap.put("summary.count", Libra.decimalFormat.format(getRowCount()));
            summaryMap.put("summary.brutto", Libra.decimalFormat.format(b));
            summaryMap.put("summary.tara", Libra.decimalFormat.format(t));
            summaryMap.put("summary.netto", Libra.decimalFormat.format(n));

            summaryRow.publishSummary(summaryMap);
        }
    }

    public void increaseRowHeight(float koef) {
        tbl.setRowHeight((int) (tbl.getRowHeight() * koef));
    }

    public void setGridFont(Font font) {
        tbl.setFont(font);
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
        tbl.setRowSorter(null);
        tbl.getTableHeader().repaint();
        tbl.getTableHeader().revalidate();
        tbl.setAutoCreateRowSorter(true);
        DataSet d = libraService.selectDataSet(sql, params);
        dtm.publish(d);

        refreshSummary();
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

            String s = Libra.fMsg("find", "find.what", textVal, getFrame());
            System.out.println(s);

            if (s != null && !s.isEmpty()) {
                DataSet d = libraService.filterDataSet(sql, params, Collections.singletonMap(columnName, "%" + s.toLowerCase() + "%"));
                dtm.publish(d);
                refreshSummary();

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

    public void setColumnFont(String fieldName, Font font) {
        int n = dtm.findColumn(fieldName);
        if (n != -1)
            columnFonts.put(n, font);
    }

    public int defineLocation(String fieldName, Object value) {
        return dtm.defineLocation(fieldName, value);
    }
}
