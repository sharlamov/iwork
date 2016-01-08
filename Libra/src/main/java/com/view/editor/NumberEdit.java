package com.view.editor;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

/**
 * Created by sharlamov on 07.01.2016.
 */
public class NumberEdit extends JTextField{

    public NumberEdit() {
        PlainDocument doc = (PlainDocument) getDocument();
        doc.setDocumentFilter(new MyDocFilter());
    }

    public NumberEdit(Object value) {
        this();
        setText(value.toString());
    }

    class MyDocFilter extends DocumentFilter {
        private static final String REMOVE_REGEX = "[^0-9]+";
        private boolean filter = true;

        @Override
        public void insertString(FilterBypass fb, int offset, String text,
                                 AttributeSet attr) throws BadLocationException {
            if (filter) {
                text = text.replaceAll(REMOVE_REGEX, "");
            }
            super.insertString(fb, offset, text, attr);

        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text,
                            AttributeSet attrs) throws BadLocationException {
            if (filter) {
                text = text.replaceAll(REMOVE_REGEX, "");
            }
            super.replace(fb, offset, length, text, attrs);

        }
    }

}
