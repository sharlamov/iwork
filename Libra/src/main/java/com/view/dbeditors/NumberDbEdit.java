package com.view.dbeditors;

import com.model.DataSet;
import com.toedter.calendar.JTextFieldDateEditor;
import com.util.Libra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class NumberDbEdit extends DbEdit {

    JFormattedTextField fld;

    public NumberDbEdit(String fieldName, DataSet dataSet) {
        super(fieldName, new JFormattedTextField(Libra.decimalFormat), dataSet);

        fld = (JFormattedTextField) getField();
        fld.setHorizontalAlignment(JTextField.RIGHT);

        fld.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char ch = e.getKeyChar();
                if (ch == '.') {
                    e.setKeyChar(',');
                    ch = ',';
                }
                if (Character.isDigit(ch) || ch == ',' || ch == '-') {
                    if (ch == ',' && (fld.getText().contains(",") || fld.getCaretPosition() == 0)) {
                        e.consume();
                    }
                    if (ch == '-' && fld.getCaretPosition() != 0) {
                        e.consume();
                    }
                } else
                    e.consume();
            }
        });

    }

    public void setEditable(boolean isEditable) {
        fld.setEnabled(isEditable);
        fld.setDisabledTextColor(Color.black);
    }

    @Override
    public Object getFieldValue() {
        return fld.getValue();
    }

    @Override
    public void setFieldValue(Object object) {
        ((JFormattedTextField) getField()).setValue(object);
    }
}
