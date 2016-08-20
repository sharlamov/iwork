package com.view.component.db.editors;

import com.model.DataSet;
import com.view.component.db.editors.validators.AbstractValidator;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.EdgedBalloonStyle;
import net.java.balloontip.utils.TimingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CheckDbEdit extends JCheckBox implements KeyListener, IEdit, ItemListener {

    private final DataSet dataSet;
    private final List<ChangeEditListener> listeners = new ArrayList<ChangeEditListener>();
    private final List<AbstractValidator> validators = new ArrayList<AbstractValidator>();

    public CheckDbEdit(String name, String text, DataSet dataSet) {
        super(text);
        this.dataSet = dataSet;
        setName(name);

        addItemListener(this);
        addKeyListener(this);
        addFocusListener(this);

        refresh();
    }

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

    public void showError(String message) {
        setBorder(BorderFactory.createLineBorder(Color.red));
        BalloonTipStyle edgedLook = new EdgedBalloonStyle(Color.decode("#FFFFCC"), Color.red);
        BalloonTip myBalloonTip = new BalloonTip(this, message, edgedLook, false);
        TimingUtils.showTimedBalloon(myBalloonTip, 3000);
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
        return isSelected() ? BigDecimal.ONE : BigDecimal.ZERO;
    }

    public void setValue(Object value) {
        setSelected(value instanceof Number && ((Number) value).intValue() > 0);
    }

    public boolean isEmpty() {
        return !isSelected();
    }

    public void refresh() {
        setSelected(dataSet.getInt(getName()) > 0);
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
        dataSet.setObject(getName(), getValue());
        fireChangeEditEvent();
    }
}
