package com.test.comps;

import com.dao.model.CustomItem;
import com.dao.model.DataSet;
import com.test.comps.grid.DataGrid;

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

    private DataGrid dataGrid;
    private PopupFactory factory;
    private Popup popup;

    private String[] targetFields;
    private boolean shouldHide;
    private boolean shouldClear = true;

    public SearchDbEdit() {
        factory = PopupFactory.getSharedInstance();
        installAncestorListener();
        initGridPanel();
    }

    private void initGridPanel() {
        dataGrid = new DataGrid();
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

    private void installAncestorListener() {
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

    private void showPopup() {
        if (popup == null) {
            Point p = getLocationOnScreen();
            popup = factory.getPopup(this, dataGrid, p.x, p.y + getHeight());
            popup.show();
        }
    }

    private void hidePopup() {
        if (popup != null) {
            popup.hide();
            popup = null;
        }
    }

    private int search(String text) {
        int cnt = 0;
        try {
            cnt = dataGrid.select("%" + text.trim().toLowerCase() + "%");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cnt;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        EventQueue.invokeLater(() -> {
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
        if (shouldClear && getValue(getName()) == null) {
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
        setValue(getName(), null);
    }

    @Override
    public void setValue(String sName, Object value) {
        if (value instanceof CustomItem) {
            getDataSet().setObject(sName, value);
            CustomItem ci = (CustomItem) value;
            setText(ci.getId() == null ? null : ci.getLabel());
        } else if (value == null || value.toString().isEmpty()) {
            getDataSet().setObject(sName, null);
            setText(null);
        } else if ((value instanceof String || value instanceof Number) && !shouldClear) {
            super.setValue(sName, value);
        } else if (value instanceof DataSet) {
            DataSet selDataSet = (DataSet) value;
            int count = selDataSet.getColCount();
            if (count == 1) {
                Object result = selDataSet.getObject(0, 0);
                getDataSet().setObject(sName, result);
                setText(result.toString());
            } else {
                for (int i = 0; i < targetFields.length; i++) {
                    String fName = targetFields[i].trim();
                    if (!fName.isEmpty()) {
                        getDataSet().setObject(fName, selDataSet.getObject(0, i));
                    }
                }
                setText(getValue(sName).toString());
            }
        }
        setCaretPosition(0);
        fireChangeEditEvent();
    }

    private void selectValue() {
        int row = dataGrid.getSelectedRow();
        if (row != -1 && popup != null) {
            setValue(getName(), dataGrid.getDataSetByRow(row));
        } else if (row == -1 && popup != null && dataGrid.getRowCount() == 1) {
            setValue(getName(), dataGrid.getDataSetByRow(0));
        }
    }

    public void setShouldClear(boolean shouldClear) {
        this.shouldClear = shouldClear;
    }

    @Override
    public void prepareProperties(TProp prop) {
        super.prepareProperties(prop);
        prop.put("shouldHide", shouldHide);
        prop.put("shouldClear", shouldClear);
        prop.put("targetFields", targetFields);
        dataGrid.prepareProperties(prop);
    }

    @Override
    public void prepareComponent(TProp prop) {
        shouldHide = prop.fetch("shouldHide");
        shouldClear = prop.fetch("shouldClear");
        targetFields = prop.fetch("targetFields");
        //initGridPanel();
        dataGrid.prepareComponent(prop);
        dataGrid.init(getDataSource(), getTranslator());

        super.prepareComponent(prop);
    }
}
