package com.view.component.db.editors;

import com.enums.SearchType;
import com.model.CustomItem;
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

public class SearchDbEdit extends TextDbEdit {
    //?search with delay!!!

    private final GridField[] fields;
    private final LibraService service;
    private final SearchType searchType;
    private String[] targetFields;
    private PopupFactory factory;
    private Popup popup;
    private DataGrid dataGrid;
    private boolean shouldHide;
    private boolean shouldClear = true;

    public SearchDbEdit(String name, DataSet dataSet, GridField[] fields, LibraService service, SearchType searchType) {
        super(name, dataSet);

        this.fields = fields;
        this.service = service;
        this.searchType = searchType;
        factory = PopupFactory.getSharedInstance();
        initGridPanel();
        installAncestorListener();

        dataSet.addField("findquery", null);
    }

    public SearchDbEdit(String name, DataSet dataSet, LibraService service, SearchType searchType) {
        this(name, dataSet, new GridField[]{new GridField("clccodt", 300)}, service, searchType);
    }

    public SearchDbEdit(String name, DataSet dataSet, String targetFields, GridField[] fields, LibraService service, SearchType searchType) {
        this(name, dataSet, fields, service, searchType);
        this.targetFields = targetFields.split(",");
    }

    public void initGridPanel() {
        dataGrid = new DataGrid(service, searchType, fields, false);
        dataGrid.setPreferredSize(new Dimension(dataGrid.getDataGridWith() + 3, 250));
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
            Point p = getLocationOnScreen();
            popup = factory.getPopup(this, dataGrid, p.x, p.y + getHeight());
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
        getDataSet().setValueByName("findquery", 0, "%" + text.trim().toLowerCase() + "%");
        try {
            cnt = dataGrid.select(getDataSet());
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
        super.keyPressed(e);
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
        if (shouldClear && getValue() == null) {
            setText(null);
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
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof CustomItem) {
            getDataSet().setValueByName(getName(), 0, value);
            CustomItem ci = (CustomItem) value;
            setText(ci.getId() == null ? null : ci.getLabel());
        } else if (value == null || value.toString().isEmpty()) {
            getDataSet().setValueByName(getName(), 0, null);
            setText(null);
        } else if ((value instanceof String || value instanceof Number) && !shouldClear) {
            super.setValue(value);
        } else if (value instanceof DataSet) {
            DataSet selDataSet = (DataSet) value;
            int count = selDataSet.getColumnCount();
            if (count == 1) {
                Object result = selDataSet.getValue(0, 0);
                getDataSet().setValueByName(getName(), 0, result);
                setText(result.toString());
            } else {
                for (int i = 0; i < targetFields.length; i++) {
                    String fName = targetFields[i].trim();
                    if (!fName.isEmpty()) {
                        getDataSet().setValueByName(fName, 0, selDataSet.getValue(0, i));
                    }
                }
                setText(getValue().toString());
            }
        }
        setCaretPosition(0);
        fireChangeEditEvent();
    }

    private void selectValue() {
        int row = dataGrid.getSelectedRow();
        if (row != -1 && popup != null) {
            setValue(dataGrid.getDataSetByRow(row));
        } else if (row == -1 && popup != null && dataGrid.getRowCount() == 1) {
            setValue(dataGrid.getDataSetByRow(0));
        }
    }

    public void setShouldClear(boolean shouldClear) {
        this.shouldClear = shouldClear;
    }
}
