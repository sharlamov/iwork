package com.test.comps;


import com.test.comps.interfaces.ChangeEditListener;
import com.test.comps.interfaces.IComp;
import com.test.comps.interfaces.ISettings;
import com.test.comps.validator.AbstractValidator;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class TextDbEdit extends JTextField implements KeyListener, IComp, ISettings {

    private final Color oldBackground;
    private final Color editBackground;
    private boolean changeable;
    private String format;//("#,###.##");
    private Border oldBorder;
    private Border newBorder;
    private boolean alphaNum;
    //private int tabOrder;
    private List<ChangeEditListener> listeners;
    private List<AbstractValidator> validators;

    public TextDbEdit() {
        oldBorder = getBorder();
        newBorder = new LineBorder(Color.decode("#006600"), 2);
        oldBackground = getBackground();
        editBackground = Color.decode("#FCFCEB");

        listeners = new ArrayList<>();
        validators = new ArrayList<>();

        addFocusListener(this);
        addKeyListener(this);
    }

    public void addChangeEditListener(ChangeEditListener listener) {
        listeners.add(listener);
    }

    protected void fireChangeEditEvent() {
        listeners.forEach(a -> a.changeEdit(this));
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

    public boolean isChangeable() {
        return changeable;
    }

    public void setChangeable(boolean changeable) {
        this.changeable = changeable;
        setEnabled(changeable);
        setDisabledTextColor(Color.black);
        setFocusable(changeable);
    }

    @Override
    public void setValue(String name, Object value) {
        if (value == null || value.toString().isEmpty()) {
            ((IComp) getParent()).setValue(name, null);
            setText("");
        } else if (value instanceof String && alphaNum) {
            String text = value.toString().toUpperCase();
            text = text.replaceAll(text.startsWith("INV") ? "[^A-Za-z0-9*/-]|\\s" : "[^A-Za-z0-9]|\\s", "");
            ((IComp) getParent()).setValue(name, text);
            setText(text);
        } else {
            ((IComp) getParent()).setValue(name, value);
            setText(value.toString());
        }
        fireChangeEditEvent();
    }

    public void focusGained(FocusEvent e) {
        setBorder(newBorder);
        setBackground(editBackground);
    }

    public void focusLost(FocusEvent e) {
        setValue(getName(), getText());
        setBorder(oldBorder);
        setBackground(oldBackground);
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
            transferFocus();
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            setValue(getName(), getText());
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void refresh() {
        setValue(getName(), getValue(getName()));
    }

    @Override
    public Object getValue(String name) {
        return ((IComp) getParent()).getValue(name);
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isAlphaNum() {
        return alphaNum;
    }

    public void setAlphaNum(boolean alphaNum) {
        this.alphaNum = alphaNum;
    }

    @Override
    public void prepareProperties(TProp prop) {
        prop.put("changeable", changeable);
        prop.put("format", getFormat());
        prop.put("alphaNum", alphaNum);
    }

    @Override
    public void prepareComponent(TProp prop) {
        setChangeable(prop.fetch("changeable"));
        setFormat(prop.fetch("format"));
        if (prop.fetch("alphaNum") != null)
            setAlphaNum(prop.fetch("alphaNum"));
    }
}