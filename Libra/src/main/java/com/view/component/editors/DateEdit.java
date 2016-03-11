package com.view.component.editors;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import com.view.component.editors.validators.AbstractValidator;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.EdgedBalloonStyle;
import net.java.balloontip.utils.TimingUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class DateEdit extends JDateChooser implements IEdit {

    private Border oldBorder;
    private List<ChangeEditListener> listeners = new ArrayList<ChangeEditListener>();
    private List<AbstractValidator> validators = new ArrayList<AbstractValidator>();

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
        oldBorder = getBorder();
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
        ((JTextFieldDateEditor) getDateEditor())
                .setDisabledTextColor(isChangeable ? Color.black : Color.darkGray);

        if (isChangeable)
            getCalendarButton().setPreferredSize(getCalendarButton().getMinimumSize());
        else
            getCalendarButton().setPreferredSize(new Dimension(1, 1));
    }

    public void focusGained(FocusEvent e) {
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

    public boolean isEmpty() {
        return getValue() == null;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        if ("date".equals(evt.getPropertyName())) {
            fireChangeEditEvent();
        }
    }
}