package com.view.component.db.editors;

import com.model.CustomItem;
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
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ComboDbEdit<T> extends JComboBox<T> implements KeyListener, IEdit, ItemListener {
    //? refactor setValue()

    private final DataSet dataSet;
    private final Border newBorder;
    private final Color oldBackground;
    private final Color editBackground;
    private final Border oldBorder;
    private final List<ChangeEditListener> listeners = new ArrayList<ChangeEditListener>();
    private final List<AbstractValidator> validators = new ArrayList<AbstractValidator>();

    public ComboDbEdit(String name, Collection<T> list, DataSet dataSet) {
        this.dataSet = dataSet;

        oldBorder = getBorder();
        newBorder = new LineBorder(Color.decode("#006600"), 2);
        oldBackground = getBackground();
        editBackground = Color.decode("#FCFCEB");

        setName(name);
        addItemListener(this);
        addKeyListener(this);
        addFocusListener(this);

        Object value = dataSet.getValueByName(getName(), 0);

        for (T o : list) {
            addItem(o);
        }

        if (value != null)
            setValue(value);
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

   /* public void changeData(DataSet set) {
        removeAllItems();
        dataSet.setValueByName(getName(), 0, null);
        for (Object[] o : set) {
            addItem((T) o[0]);
        }
    }*/

    public void changeData(Collection<T> set) {
        removeAllItems();
        dataSet.setValueByName(getName(), 0, null);
        if (set != null)
            for (T o : set) {
                addItem(o);
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
            if (value instanceof BigDecimal && obj instanceof CustomItem) {
                if (((CustomItem) obj).getId().equals(value)) {
                    value = obj;
                    exists = true;
                    break;
                }
            } else if (obj.equals(value)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            addItem((T) value);
        }
        setSelectedItem(value);
    }

    public boolean isEmpty() {
        return getValue() == null;
    }

    public void refresh() {
        Object obj = dataSet.getValueByName(getName(), 0);
        if (obj != null)
            setValue(obj);
    }

    public void focusGained(FocusEvent e) {
        setBorder(newBorder);
        setBackground(editBackground);
    }

    public void focusLost(FocusEvent e) {
        setValue(getSelectedItem());
        setBorder(oldBorder);
        setBackground(oldBackground);
    }

    public void actionPerformed(ActionEvent e) {
        fireChangeEditEvent();
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            dataSet.setValueByName(getName(), 0, getValue());
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
