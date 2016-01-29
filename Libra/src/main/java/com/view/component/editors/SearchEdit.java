package com.view.component.editors;

import com.enums.SearchType;
import com.model.DataSet;
import com.service.LibraService;
import com.view.component.grid.DataGrid;
import com.view.component.grid.GridField;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SearchEdit extends CommonEdit {

    private final String sourceName;
    private final GridField[] fields;
    private final LibraService service;
    private final SearchType searchType;
    private DataSet selectedDataSet;
    private PopupFactory factory;
    private Popup popup;
    private DataGrid dataGrid;
    private boolean shouldHide;

    public SearchEdit(String name, String sourceName, GridField[] fields, LibraService service, SearchType searchType) {
        super(name);
        this.sourceName = sourceName;
        this.fields = fields;
        this.service = service;
        this.searchType = searchType;
        factory = PopupFactory.getSharedInstance();
        initGridPanel();


        installAncestorListener();
    }

    public SearchEdit(String name, LibraService service, SearchType searchType) {
        this(name, "clccodt", new GridField[]{new GridField("clccodt", 250)}, service, searchType);
    }

    public void initGridPanel() {
        dataGrid = new DataGrid(fields, false);
        dataGrid.setPreferredSize(new Dimension(dataGrid.getDataGridWith() + 3, 200));
        dataGrid.setFocusable(false);
        dataGrid.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (me.getClickCount() == 2) {
                    selectValue();
                    hidePopup();
                }
            }
        });
    }

    private Point findLocation() {
        Point p = getLocationOnScreen();
        return new Point(p.x, p.y + getHeight());
    }

    protected void installAncestorListener() {
        addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {
                hidePopup();
            }

            public void ancestorRemoved(AncestorEvent event) {
                hidePopup();
            }

            public void ancestorMoved(AncestorEvent event) {
                hidePopup();
            }
        });
    }

    public void showPopup() {
        if (popup == null) {
            Point location = findLocation();
            popup = factory.getPopup(this, dataGrid, location.x, location.y);
            popup.show();
        }
    }

    protected void hidePopup() {
        if (popup != null) {
            popup.hide();
            popup = null;
        }
    }

    public int search(String text) {
        int cnt = 0;
        try {
            DataSet dataSet = service.searchDataSet(text, searchType);
            cnt = dataSet.size();
            dataGrid.publish(dataSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cnt;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                String text = getText();
                if (text.isEmpty() || shouldHide) {
                    hidePopup();
                } else {
                    if (search(text) == 0) {
                        hidePopup();
                    } else {
                        showPopup();
                    }
                }
            }
        });
    }

    @Override
    public void keyPressed(KeyEvent e) {
        shouldHide = false;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                shouldHide = true;
                break;
            case KeyEvent.VK_ENTER:
                selectValue();
                shouldHide = true;
                break;
            case KeyEvent.VK_ESCAPE:
                shouldHide = true;
                break;
            case KeyEvent.VK_DOWN:
                moveDown();
                break;
            case KeyEvent.VK_UP:
                moveUp();
                break;
            case KeyEvent.VK_DELETE:
                removeValue();
                shouldHide = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        super.focusLost(e);
        hidePopup();
        if (getValue() == null) {
            setText("");
        }
    }

    private void moveDown() {
        if (dataGrid != null && popup != null) {
            int pos = dataGrid.getSelectedRow() + 1;
            int cnt = dataGrid.getRowCount();
            if (cnt > 0) {
                dataGrid.setSelectedRow(cnt == pos ? 0 : pos);
            }
        } else {
            search(getText());
            showPopup();
        }
    }

    private void moveUp() {
        if (dataGrid != null && popup != null) {
            int cnt = dataGrid.getRowCount();
            if (cnt > 0) {
                int pos = dataGrid.getSelectedRow() - 1;
                dataGrid.setSelectedRow(pos < 0 ? cnt - 1 : pos);
            }
        }
    }

    private void removeValue() {
        setValue(null);
        selectedDataSet = null;
        setText("");
    }

    private void selectValue() {
        int row = dataGrid.getSelectedRow();
        if (row != -1 && popup != null) {
            DataSet dataSet = dataGrid.getDataSetByRow(row);
            selectedDataSet = dataSet;
            setValue(dataSet.getValueByName(sourceName, 0));
        }

    }

    public DataSet getSelectedDataSet() {
        return selectedDataSet;
    }
}
