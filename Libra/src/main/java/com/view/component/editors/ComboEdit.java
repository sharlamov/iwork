package com.view.component.editors;

import com.model.CustomItem;
import com.model.DataSet;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ComboEdit extends JComboBox<CustomItem> implements KeyListener, FocusListener, IEdit, ItemListener {

    private Border oldBorder;
    private List<ChangeEditListener> listeners = new ArrayList<ChangeEditListener>();

    public ComboEdit(String name, List<CustomItem> list) {
        setName(name);
        addItemListener(this);
        addKeyListener(this);
        addFocusListener(this);
        for (CustomItem o : list) {
            addItem(o);
        }
    }

    public void changeData(DataSet dataSet) {
        removeAllItems();
        for (Object[] o : dataSet) {
            addItem((CustomItem) o[0]);
        }
    }

    public void addChangeEditListener(ChangeEditListener listener) {
        listeners.add(listener);
    }

    public void fireChangeEditEvent() {
        if (listeners != null) {
            for (ChangeEditListener hl : listeners)
                hl.changeEdit(this);
        }
    }

    public void setChangeable(boolean isChangeable) {
        setEnabled(isChangeable);
    }

    public Object getValue() {
        return getSelectedItem();
    }

    public void setValue(Object value) {
        boolean exists = false;
        int n = getModel().getSize();
        for (int i = 0; i < n; i++) {
            Object obj = getModel().getElementAt(i);
            if (obj.equals(value)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            addItem((CustomItem) value);
        }
        setSelectedItem(value);
    }

    public boolean isEmpty() {
        return getValue() == null;
    }

    public void focusGained(FocusEvent e) {
        oldBorder = getBorder();
        setBorder(BorderFactory.createLineBorder(Color.GREEN));
    }

    public void focusLost(FocusEvent e) {
        setBorder(oldBorder);
    }

    public void actionPerformed(ActionEvent e) {
        fireChangeEditEvent();
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            fireChangeEditEvent();
        }
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
            transferFocus();
        }
    }

    public void keyReleased(KeyEvent e) {

    }
}
