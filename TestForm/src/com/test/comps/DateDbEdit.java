package com.test.comps;

import com.test.comps.interfaces.ChangeEditListener;
import com.test.comps.interfaces.IComp;
import com.test.comps.interfaces.ISettings;
import com.test.comps.validator.AbstractValidator;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.util.*;
import java.util.List;

public class DateDbEdit extends JDateChooser implements IComp, ISettings {

    private Color oldBackground;
    private Color editBackground;
    private Border oldBorder;
    private List<ChangeEditListener> listeners;
    private List<AbstractValidator> validators;
    private Border newBorder;


    public DateDbEdit() {
        getDateEditor().getUiComponent().addFocusListener(this);
        Set<AWTKeyStroke> set = new HashSet<>(getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        set.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, set);
        oldBorder = getBorder();
        newBorder = new LineBorder(Color.decode("#006600"), 2);
        oldBackground = getBackground();
        editBackground = Color.decode("#FCFCEB");

        listeners = new ArrayList<>();
        validators = new ArrayList<>();
    }

/*    private void initDateEdit(String name) {
        //setLocale(Libra.SETTINGS.getLang().getLoc());
        setName(name);


        refresh();
    }*/

    public void addValidator(AbstractValidator validator) {
        validators.add(validator);
    }

    public boolean verify() {
        for (AbstractValidator validator : validators) {
            if (!validator.verify(getValue())) {
                showError(validator.getErrorMessage());
                return false;
            }
        }
        return true;
    }

    public void addChangeEditListener(ChangeEditListener listener) {
        listeners.add(listener);
    }

    public void fireChangeEditEvent() {
        if (listeners != null)
            listeners.forEach(h1 -> h1.changeEdit(this));
    }

    @Override
    public Object getValue(String name) {
        return ((IComp) getParent()).getValue(name);
    }

    @Override
    public void setValue(String name, Object o) {
        setDate((Date) o);

    }

    @Override
    public void prepareProperties(TProp prop) {
        prop.put("changeable", isEnabled());
        prop.put("format", getDateFormatString());
        //prop.put("text", getText());
    }

    @Override
    public void prepareComponent(TProp prop) {
        setChangeable(prop.fetch("changeable"));
        setDateFormatString(prop.fetch("format"));
        //setText(prop.fetch("text"));
    }

    public void setChangeable(boolean isChangeable) {
        setEnabled(isChangeable);
        ((JTextFieldDateEditor) getDateEditor())
                .setDisabledTextColor(isChangeable ? Color.black : Color.darkGray);

        if (isChangeable)
            getCalendarButton().setPreferredSize(getCalendarButton().getMinimumSize());
        else
            getCalendarButton().setPreferredSize(new Dimension(1, 1));
    }

    public void focusGained(FocusEvent e) {
        setBorder(newBorder);
        ((JTextFieldDateEditor) getDateEditor()).setBackground(editBackground);
    }

    public void focusLost(FocusEvent e) {
        setValue(getDate());
        setBorder(oldBorder);
        ((JTextFieldDateEditor) getDateEditor()).setBackground(oldBackground);
    }

    public Object getValue() {
        return getDate();
    }

    public void setValue(Object value) {
        setDate((Date) value);
    }

    public boolean isEmpty() {
        return getValue() == null;
    }

    public void refresh() {
        setDate((Date) getValue(getName()));
    }

    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        if ("date".equals(evt.getPropertyName())) {
            IComp comp = ((IComp) getParent());
            if (comp != null)
                comp.setValue(getName(), evt.getNewValue());
            fireChangeEditEvent();
        }
    }
}