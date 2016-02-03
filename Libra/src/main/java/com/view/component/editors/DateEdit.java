package com.view.component.editors;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class DateEdit extends JDateChooser implements FocusListener, IEdit {

    private Border oldBorder;
    private List<ChangeEditListener> listeners = new ArrayList<ChangeEditListener>();

    public DateEdit(String name, SimpleDateFormat format) {
        setDateFormatString(format.toPattern());
        initDateEdit(name);
    }

    public DateEdit(String name) {
        super("dd.MM.yyyy", "##.##.####", '_');
        initDateEdit(name);
    }

    private void initDateEdit(String name) {
        setName(name);
        getDateEditor().getUiComponent().addFocusListener(this);

        Set<AWTKeyStroke> set = new HashSet<AWTKeyStroke>(getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        set.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, set);
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

    public void setChangable(boolean isChangable) {
        setEnabled(isChangable);
        ((JTextFieldDateEditor) getDateEditor())
                .setDisabledTextColor(isChangable ? Color.black : Color.darkGray);

        if (isChangable)
            getCalendarButton().setPreferredSize(getCalendarButton().getMinimumSize());
        else
            getCalendarButton().setPreferredSize(new Dimension(1, 1));
    }

    public void focusGained(FocusEvent e) {
        oldBorder = getBorder();
        setBorder(BorderFactory.createLineBorder(Color.GREEN));
    }

    public void focusLost(FocusEvent e) {
        setBorder(oldBorder);
    }

    public Object getValue() {
        return getDate();
    }

    public void setValue(Object value) {
        setDate((Date) value);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        if ("date".equals(evt.getPropertyName())) {
            fireChangeEditEvent();
        }
    }
}