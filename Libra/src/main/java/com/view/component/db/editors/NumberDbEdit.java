package com.view.component.db.editors;

import com.model.DataSet;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;

public class NumberDbEdit extends TextDbEdit {

    public NumberDbEdit(String name, DataSet dataSet) {
        super(name, dataSet);
        setHorizontalAlignment(JTextField.RIGHT);
    }

    public BigDecimal getNumberValue() {
        Object obj = getValue();
        return obj != null ? new BigDecimal(obj.toString()) : BigDecimal.ZERO;
    }

    @Override
    public void setValue(Object value) {
        if (value == null || value.toString().isEmpty()) {
            getDataSet().setValueByName(getName(), 0, null);
            setText("");
        } else {
            String v = value.toString().replaceAll("\\s|\\xA0", "").replaceAll(",", ".");
            BigDecimal bd = new BigDecimal(v);
            getDataSet().setValueByName(getName(), 0, bd);
            setText(getFormat().format(bd));
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
