package com.bin;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JField extends JPanel {
    private JTextField fieldValue;
    private String title;
    private Object value;

    public JField(String title, Object value) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setPreferredSize(new Dimension(360, 28));

        this.title = title;
        this.value = value;
        JLabel labelName = new JLabel(title);

        labelName.setPreferredSize(new Dimension(150, 25));
        fieldValue = new JTextField(value == null ? "" : value.toString());
        fieldValue.setPreferredSize(new Dimension(200, 25));

        fieldValue.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }


        });

        labelName.setLabelFor(fieldValue);
        add(labelName);
        add(fieldValue);
    }

    public String getTitle() {
        return title;
    }

    public Object getValue(){
        return value;
    }

    public void warn() {
        value = fieldValue.getText();
    }
}
