package com.view.component.editors;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateEdit extends JDateChooser implements FocusListener {

    private Border oldBorder;
    private List<ChangeEditListener> listeners = new ArrayList<ChangeEditListener>();

    public DateEdit(String name, SimpleDateFormat format) {
        setDateFormatString(format.toPattern());
        setName(name);
        getDateEditor().getUiComponent().addFocusListener(this);
    }

    public void addChangeEditListener(ChangeEditListener listener) {
        listeners.add(listener);
    }

    protected void fireChangeEditEvent() {
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