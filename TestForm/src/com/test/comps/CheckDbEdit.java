package com.test.comps;

import com.test.comps.interfaces.ChangeEditListener;
import com.test.comps.interfaces.IComp;
import com.test.comps.interfaces.ISettings;
import com.test.comps.validator.AbstractValidator;

import javax.swing.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CheckDbEdit extends JCheckBox implements KeyListener, IComp, ISettings, ItemListener {

    private List<ChangeEditListener> listeners;
    private List<AbstractValidator> validators;

    public CheckDbEdit() {
        super();

        listeners = new ArrayList<>();
        validators = new ArrayList<>();

        addItemListener(this);
        addKeyListener(this);
        addFocusListener(this);
    }

    public void addValidator(AbstractValidator validator) {
        validators.add(validator);
    }

    public boolean verify() {
        for (AbstractValidator validator : validators) {
            if (!validator.verify(getValue(getName()))) {
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
        if (listeners != null) {
            for (ChangeEditListener hl : listeners)
                hl.changeEdit(this);
        }
    }

    @Override
    public Object getValue(String name) {
        return ((IComp) getParent()).getValue(name);
    }

    @Override
    public void setValue(String name, Object o) {
        setSelected(o instanceof Number && ((Number) o).intValue() > 0);
        ((IComp) getParent()).setValue(name, o);
    }

    @Override
    public void prepareProperties(TProp prop) {
        prop.put("changeable", isEnabled());
        prop.put("text", getText());
    }

    @Override
    public void prepareComponent(TProp prop) {
        setChangeable(prop.fetch("changeable"));
        setText(prop.fetch("text"));
    }

    public void setChangeable(boolean isChangeable) {
        setEnabled(isChangeable);
    }

    public void setValue(Object value) {

    }

    public boolean isEmpty() {
        return !isSelected();
    }

    public void refresh() {
        Object o = getValue(getName());
        setSelected(o instanceof Number && ((Number) o).intValue() > 0);
    }

    public void focusGained(FocusEvent e) {
    }

    public void focusLost(FocusEvent e) {
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

    public void itemStateChanged(ItemEvent e) {
        setValue(getName(), isSelected() ? BigDecimal.ONE : BigDecimal.ZERO);
        fireChangeEditEvent();
    }
}
