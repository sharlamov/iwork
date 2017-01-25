package com.test.comps;


import com.test.comps.interfaces.IComp;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class NumberDbEdit extends TextDbEdit {

    public NumberDbEdit() {
        super();
        setHorizontalAlignment(JTextField.RIGHT);
    }

    public BigDecimal getNumberValue() {
        Object obj = getValue(getName());
        return obj != null ? new BigDecimal(obj.toString()) : BigDecimal.ZERO;
    }

    @Override
    public void setValue(String name, Object value) {
        if (value == null || value.toString().isEmpty()) {
            ((IComp) getParent()).setValue(name, null);
            setText("");
        } else {
            String v = value.toString().replaceAll("\\s|\\xA0", "").replaceAll(",", ".");
            BigDecimal bd = new BigDecimal(v);
            ((IComp) getParent()).setValue(name, bd);
            setText(new DecimalFormat(getFormat()).format(bd));
        }
        fireChangeEditEvent();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char ch = e.getKeyChar();
        if (ch == '.') {
            e.setKeyChar(',');
            ch = ',';
        }
        if (Character.isDigit(ch) || ch == ',' || ch == '-') {
            if (ch == ',' && (getText().contains(",") || getCaretPosition() == 0)) {
                e.consume();
            }
            if (ch == '-' && getCaretPosition() != 0) {
                e.consume();
            }
        } else
            e.consume();
    }
}
