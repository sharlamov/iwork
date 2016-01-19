package com.view.editor;

import com.model.DataSet;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

public class NumberEdit extends AbstractEdit{

    public NumberEdit(String title, DataSet dataSet) {
        super(title, new JTextField(), dataSet);
        JTextField fld = (JTextField)getField();
        PlainDocument doc = (PlainDocument) fld.getDocument();
        doc.setDocumentFilter(new NumberDocFilter());
        fld.setHorizontalAlignment(JTextField.RIGHT);
    }

    @Override
    public void setValue(Object obj) {
        ((JTextField)getField()).setText(obj == null ? "" : obj.toString());
    }

    @Override
    public Object getValue() {
        return new BigDecimal(((JTextField)getField()).getText());
    }

    class NumberDocFilter extends DocumentFilter {
        private static final String REMOVE_REGEX = "[^0-9]+";
        private boolean filter = true;

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws
                BadLocationException {
            super.remove(fb, offset, length);
            fireChangeEditEvent(this);
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String text,
                                 AttributeSet attr) throws BadLocationException {
            if (filter) {
                text = text.replaceAll(REMOVE_REGEX, "");
            }
            super.insertString(fb, offset, text, attr);
            fireChangeEditEvent(text);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text,
                            AttributeSet attrs) throws BadLocationException {
            if (filter) {
                text = text.replaceAll(REMOVE_REGEX, "");
            }
            super.replace(fb, offset, length, text, attrs);
            fireChangeEditEvent(text);
        }
    }

}
