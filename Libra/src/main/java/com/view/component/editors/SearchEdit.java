package com.view.component.editors;

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
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class SearchEdit extends CommonEdit {

    private final String sourceName;
    private final GridField[] fields;
    private final LibraService service;
    private final SearchType searchType;
    private IEdit[] iEdits;
    private DataSet selectedDataSet;
    private PopupFactory factory;
    private Popup popup;
    private DataGrid dataGrid;
    private boolean shouldHide;
    private boolean shouldClear = true;

    public SearchEdit(String name, String sourceName, GridField[] fields, LibraService service, SearchType searchType) {
        super(name);
        setFormatter(null);
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

    public SearchEdit(String name, String sourceName, GridField[] fields, LibraService service, SearchType searchType, IEdit[] iEdits) {
        this(name, sourceName, fields, service, searchType);
        this.iEdits = iEdits;
    }

    public void initGridPanel() {
        dataGrid = new DataGrid(service, searchType, fields, false);
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
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(":findquery", "%" + text.trim().toLowerCase() + "%");

        if (iEdits != null) {
            for (IEdit iEdit : iEdits) {
                Object val = iEdit.getValue() instanceof CustomItem ? ((CustomItem) iEdit.getValue()).getId() : iEdit.getValue();
                params.put(":" + iEdit.getName().toLowerCase(), val);
            }
        }

        try {
            //DataSet dataSet = service.selectDataSet(searchType, params);
            cnt = dataGrid.select(params);
            //publish(dataSet);
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
        selectedDataSet = null;
        setText(null);
    }

    private void selectValue() {
        int row = dataGrid.getSelectedRow();
        if (row != -1 && popup != null) {
            selectedDataSet = dataGrid.getDataSetByRow(row);
            setValue(selectedDataSet.getValueByName(sourceName, 0));
        } else if (row == -1 && popup != null && dataGrid.getRowCount() == 1) {
            selectedDataSet = dataGrid.getDataSetByRow(0);
            setValue(selectedDataSet.getValueByName(sourceName, 0));
        }/* else if (shouldClear && (getValue() == null || getValue() == "")) {
            removeValue();
        }*/

    }

    @Override
    public void commitEdit() throws ParseException {
        if (!shouldClear) {
            super.commitEdit();
        }
    }

    public DataSet getSelectedDataSet() {
        return selectedDataSet;
    }

    public void setShouldClear(boolean shouldClear) {
        this.shouldClear = shouldClear;
    }
}
