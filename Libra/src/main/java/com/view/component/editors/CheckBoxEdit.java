package com.view.component.editors;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class CheckBoxEdit extends JCheckBox implements KeyListener, FocusListener, IEdit, ChangeListener {

    private Border oldBorder;
    private List<ChangeEditListener> listeners = new ArrayList<ChangeEditListener>();

    public CheckBoxEdit(String name, String text) {
        super(text);
        setName(name);

        addChangeListener(this);
        addKeyListener(this);
        addFocusListener(this);
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
        return isSelected() ? 1 : 0;
    }

    public void setValue(Object value) {
        if (value instanceof Integer) {
            setSelected(((Integer) value) != 0);
        } else
            setSelected(false);
    }

    public boolean isEmpty() {
        return !isSelected();
    }

    public void focusGained(FocusEvent e) {
        oldBorder = getBorder();
        setBorder(BorderFactory.createLineBorder(Color.GREEN));
    }

    public void focusLost(FocusEvent e) {
        setBorder(oldBorder);
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

    public void stateChanged(ChangeEvent e) {
        fireChangeEditEvent();
    }
}
