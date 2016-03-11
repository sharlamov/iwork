package com.view.component.editors;

import com.model.CustomItem;
import com.model.DataSet;
import com.view.component.editors.validators.AbstractValidator;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.EdgedBalloonStyle;
import net.java.balloontip.utils.TimingUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ComboEdit extends JComboBox<CustomItem> implements KeyListener, IEdit, ItemListener {

    private Border oldBorder;
    private List<ChangeEditListener> listeners = new ArrayList<ChangeEditListener>();
    private List<AbstractValidator> validators = new ArrayList<AbstractValidator>();

    public ComboEdit(String name, List<CustomItem> list) {
        oldBorder = getBorder();
        setName(name);
        addItemListener(this);
        addKeyListener(this);
        addFocusListener(this);
        for (CustomItem o : list) {
            addItem(o);
        }
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

    public void changeData(DataSet dataSet) {
        removeAllItems();
        for (Object[] o : dataSet) {
            addItem((CustomItem) o[0]);
        }
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
        return getSelectedItem();
    }

    public void setValue(Object value) {
        boolean exists = false;
        int n = getModel().getSize();
        for (int i = 0; i < n; i++) {
            Object obj = getModel().getElementAt(i);
            if(value instanceof BigDecimal && obj instanceof  CustomItem){
                if (((CustomItem) obj).getId().equals(value)) {
                    value = obj;
                    exists = true;
                    break;
                }
            }else if (obj.equals(value)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            addItem((CustomItem) value);
        }
        setSelectedItem(value);
    }

    public boolean isEmpty() {
        return getValue() == null;
    }

    public void focusGained(FocusEvent e) {
        setBorder(BorderFactory.createLineBorder(Color.GREEN));
    }

    public void focusLost(FocusEvent e) {
        setBorder(oldBorder);
    }

    public void actionPerformed(ActionEvent e) {
        fireChangeEditEvent();
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            fireChangeEditEvent();
        }
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
}
