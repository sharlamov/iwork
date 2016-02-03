package com.view.component.editors;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.InternationalFormatter;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.Format;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CommonEdit extends JFormattedTextField implements KeyListener, FocusListener, PropertyChangeListener, IEdit {

    private Border oldBorder;
    private List<ChangeEditListener> listeners = new ArrayList<ChangeEditListener>();

    public CommonEdit(String name) {
        super();
        setName(name);
        addFocusListener(this);
        addKeyListener(this);
        addPropertyChangeListener(this);
    }

    public CommonEdit(String name, Format format) {
        this(name);
        setFormatterFactory(new DefaultFormatterFactory(new InternationalFormatter(format)));
    }

    public void addChangeEditListener(ChangeEditListener listener) {
        listeners.add(listener);
    }

    public void fireChangeEditEvent() {
        for (ChangeEditListener hl : listeners)
            hl.changeEdit(this);
    }

    public void setChangable(boolean isChangable) {
        setEnabled(isChangable);
        setDisabledTextColor(Color.black);
        setFocusable(isChangable);
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

    public void propertyChange(PropertyChangeEvent evt) {
        if ("value".equals(evt.getPropertyName())) {
            if (evt.getOldValue() != null || evt.getNewValue() != null) {
                fireChangeEditEvent();
            }
        }
    }

    @Override
    public void commitEdit() throws ParseException {
        super.commitEdit();
        if (getFormatter() == null) {
            String str = getText();
            if (!str.isEmpty())
                setValue(str);
        }
    }
}