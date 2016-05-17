package com.view.component.db.editors;

import com.model.DataSet;
import com.view.component.db.editors.validators.AbstractValidator;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.EdgedBalloonStyle;
import net.java.balloontip.utils.TimingUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

public class TextDbEdit extends JTextField implements KeyListener, IEdit {

    private final DataSet dataSet;
    private final Color oldBackground;
    private final Color editBackground;
    private boolean isAlphaNum = false;
    private Format format = new DecimalFormat("#,###.##");
    private Border oldBorder;
    private Border newBorder;
    private List<ChangeEditListener> listeners = new ArrayList<ChangeEditListener>();
    private List<AbstractValidator> validators = new ArrayList<AbstractValidator>();

    public TextDbEdit(String name, DataSet dataSet) {
        this.dataSet = dataSet;
        setName(name);
        oldBorder = getBorder();
        newBorder = new LineBorder(Color.decode("#006600"), 2);
        oldBackground = getBackground();
        editBackground = Color.decode("#FCFCEB");

        addFocusListener(this);
        addKeyListener(this);

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
        for (ChangeEditListener hl : listeners)
            hl.changeEdit(this);
    }

    public void setChangeable(boolean isChangeable) {
        setEnabled(isChangeable);
        setDisabledTextColor(Color.black);
        setFocusable(isChangeable);
    }

    public Object getValue() {
        return dataSet.getValueByName(getName(), 0);
    }

    public void setValue(Object value) {
        if (value == null || value.toString().isEmpty()) {
            dataSet.setValueByName(getName(), 0, null);
            setText("");
        } else if (value instanceof String && isAlphaNum){
            String text = value.toString().replaceAll("[^A-Za-z0-9]|\\s", "").toUpperCase();
            dataSet.setValueByName(getName(), 0, text);
            setText(text);
        } else {
            dataSet.setValueByName(getName(), 0, value);
            setText(value.toString());
        }
        fireChangeEditEvent();
    }

    public void focusGained(FocusEvent e) {
        setBorder(newBorder);
        setBackground(editBackground);
    }

    public void focusLost(FocusEvent e) {
        setValue(getText());
        setBorder(oldBorder);
        setBackground(oldBackground);
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
            transferFocus();
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            setValue(getText());
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public boolean isEmpty() {
        return getValue() == null;
    }

    public void refresh() {
        setValue(getValue());
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public boolean isAlphaNum() {
        return isAlphaNum;
    }

    public void setAlphaNum(boolean isAlphaNum) {
        this.isAlphaNum = isAlphaNum;
    }
}