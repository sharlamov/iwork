package com.view.component.editors;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;

public class NumberEdit extends CommonEdit {


    public NumberEdit(String name, NumberFormat format) {
        super(name, format);
        setHorizontalAlignment(JTextField.RIGHT);
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
