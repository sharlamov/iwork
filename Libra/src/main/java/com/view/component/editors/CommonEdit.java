package com.view.component.editors;

import com.view.component.editors.validators.AbstractValidator;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.EdgedBalloonStyle;
import net.java.balloontip.utils.TimingUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.InternationalFormatter;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.Format;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CommonEdit extends JFormattedTextField implements KeyListener, PropertyChangeListener, IEdit {


    private Border oldBorder;
    private List<ChangeEditListener> listeners = new ArrayList<ChangeEditListener>();
    private List<AbstractValidator> validators = new ArrayList<AbstractValidator>();

    public CommonEdit(String name) {
        DefaultFormatter format = new DefaultFormatter();
        format.setOverwriteMode(false);
        setFormatterFactory(new DefaultFormatterFactory(format));

        setName(name);
        addFocusListener(this);
        addKeyListener(this);
        addPropertyChangeListener(this);
        oldBorder = getBorder();
    }

    public CommonEdit(String name, Format format) {
        this(name);
        setFormatterFactory(new DefaultFormatterFactory(new InternationalFormatter(format)));
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

    public void setChangeable(boolean isChangable) {
        setEnabled(isChangable);
        setDisabledTextColor(Color.black);
        setFocusable(isChangable);
    }

    public void focusGained(FocusEvent e) {
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

    public boolean isEmpty() {
        return getValue() == null;
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