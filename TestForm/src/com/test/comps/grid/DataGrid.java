package com.test.comps.grid;

import com.dao.model.DataSet;
import com.test.comps.TProp;
import com.test.comps.interfaces.IDataSource;
import com.test.comps.interfaces.ISettings;
import com.test.comps.interfaces.ITranslator;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataGrid extends JPanel implements ISettings {

    private IDataSource service;
    private ITranslator translator;

    private JTable tbl;
    private DataSetTableModel dtm;
    private int dataGridWith;
    private DataSet params;
    private SummaryRow summaryRow;


    private Map<String, String> summaryMap;
    private GridProps gridProps;

    public DataGrid() {
        super(new BorderLayout());
        dtm = new DataSetTableModel();
        tbl = new JTable(dtm);
        tbl.setRowSelectionAllowed(true);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbl.setFillsViewportHeight(true);

        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gridProps = new GridProps();

        JScrollPane scrollPane = new JScrollPane(tbl);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void init(IDataSource service, ITranslator translator) {
        this.service = service;
        this.translator = translator;

        dtm.initSettings(gridProps, translator);
        tbl.setDefaultRenderer(Object.class, new DataSetCellRenderer(gridProps));
        tbl.setAutoCreateRowSorter(gridProps.useSorting);

        for (int i = 0; i < gridProps.size(); i++) {
            TableColumn column = tbl.getColumnModel().getColumn(i);
            int width = gridProps.getColSize(i);
            column.setPreferredWidth(width);
            dataGridWith += width;
        }

        if (gridProps.useSummary) {
            summaryMap = new LinkedHashMap<>();
            summaryRow = new SummaryRow(translator);
            add(summaryRow, BorderLayout.SOUTH);
        }
    }

    public void addMouseAction() {
        tbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                int currentRow = tbl.rowAtPoint(point);
                setSelectedRow(currentRow);
            }
        });
    }

    private void refreshSummary() {
        if (summaryRow != null) {
            summaryMap.clear();

            BigDecimal b = dtm.getSumByColumn("masa_brutto");
            BigDecimal t = dtm.getSumByColumn("masa_tara");
            BigDecimal n = dtm.getSumByColumn("masa_netto");

            summaryMap.put("summary.count", gridProps.toStdFormat(getRowCount()));
            summaryMap.put("summary.brutto", gridProps.toStdFormat(b));
            summaryMap.put("summary.tara", gridProps.toStdFormat(t));
            summaryMap.put("summary.netto", gridProps.toStdFormat(n));

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

    public int select(DataSet params) throws Exception {
        this.params = params;
        createRowSorter(tbl.getAutoCreateRowSorter());
        DataSet d = service.exec(gridProps.query, params);
        dtm.publish(d);

        refreshSummary();
        return d.size();
    }

    public int select(Object... params) throws Exception {
        createRowSorter(tbl.getAutoCreateRowSorter());
        DataSet d = service.exec(gridProps.query, params);
        dtm.publish(d);
        refreshSummary();
        return d.size();
    }

    private void createRowSorter(boolean bCreate) {
        if (bCreate) {
            tbl.setRowSorter(null);
            tbl.getTableHeader().repaint();
            tbl.getTableHeader().revalidate();
            tbl.setAutoCreateRowSorter(true);
        }
    }

    public InputMap getIMap(int condition) {
        return tbl.getInputMap(condition);
    }

    public ActionMap getAMap() {
        return tbl.getActionMap();
    }

    public int getSelectedRow() {
        return getCurrentRow(tbl.getSelectedRow());
    }

    public void setSelectedRow(int row) {
        if (row != -1) {
            tbl.setRowSelectionInterval(row, row);
            scrollToVisible(row);
        }
    }

    public void scrollToVisible(int rowIndex) {
        tbl.scrollRectToVisible(tbl.getCellRect(rowIndex, 0, true));
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

    public void filter() throws Exception {
        int r = tbl.getSelectedRow();
        int c = tbl.getSelectedColumn();
        if (r != -1 && c != -1) {
            String columnName = dtm.getFieldName(c);
            String textVal = gridProps.toStdFormat(dtm.getValueAt(r, c));

            String s = (String) JOptionPane.showInputDialog(getFrame(), translator.lng("find.what"), translator.lng("find"), JOptionPane.PLAIN_MESSAGE, null, null, textVal);

            if (s != null && !s.isEmpty()) {
                DataSet d = service.filterDataSet(gridProps.query, params, Collections.singletonMap(columnName, "%" + s.toLowerCase() + "%"));
                dtm.publish(d);
                refreshSummary();

                if (!d.isEmpty()) {
                    setSelectedRow(0);
                    tbl.setColumnSelectionInterval(c, c);
                }
            }
        }
    }

    private Component getFrame() {
        Component cmp = getParent();
        while (!(cmp instanceof JFrame)) {
            cmp = cmp.getParent();
        }
        return cmp;
    }

   /* public void setColumnFont(String fieldName, Font font) {
        int n = dtm.findColumn(fieldName);
        if (n != -1)
            cFonts[n] = font;
    }*/

    public int defineLocation(String fieldName, Object value) {
        return dtm.defineLocation(fieldName, value);
    }

    private int getCurrentRow(int row) {
        return row == -1 || !tbl.getAutoCreateRowSorter() || tbl.getRowSorter() == null ? row : tbl.getRowSorter().convertRowIndexToModel(row);
    }

    public IDataSource getService() {
        return service;
    }

    public void setService(IDataSource service) {
        this.service = service;
    }

    public ITranslator getTranslator() {
        return translator;
    }

    public void setTranslator(ITranslator translator) {
        this.translator = translator;
    }

    /*public String[] getcNames() {
        return cNames;
    }

    public void setcNames(String[] cNames) {
        this.cNames = cNames;
    }

    public int[] getcSizes() {
        return cSizes;
    }

    public void setcSizes(int[] cSizes) {
        this.cSizes = cSizes;
    }

    public Font[] getcFonts() {
        return cFonts;
    }

    public void setcFonts(Font[] cFonts) {
        this.cFonts = cFonts;
    }*/

    @Override
    public void prepareProperties(TProp prop) {
        prop.put("gridProps", gridProps);
       /* prop.put("useBgColor", useBgColor);
        prop.put("useSummary", useSummary);
        prop.put("useSorting", useSorting);
        prop.put("query", query);
        prop.put("cNames", cNames);
        prop.put("cSizes", cSizes);
        prop.put("cFonts", cFonts);*/
    }

    @Override
    public void prepareComponent(TProp prop) {
        gridProps = prop.fetch("gridProps");
        /*useBgColor = prop.fetch("useBgColor");
        useSummary = prop.fetch("useSummary");
        useSorting = prop.fetch("useSorting");
        query = prop.fetch("query");
        cNames = prop.fetch("cNames");
        cSizes = prop.fetch("cSizes");
        cFonts = prop.fetch("cFonts");*/
    }
}
