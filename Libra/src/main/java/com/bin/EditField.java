package com.bin;

import com.view.editor.DateEdit;
import com.view.editor.NumberEdit;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class EditField extends JPanel {
    private JComponent fieldValue;
    private String title;
    private Object value;

    public EditField(String title, Object value) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setPreferredSize(new Dimension(360, 28));

        this.title = title;
        this.value = value;
        JLabel labelName = new JLabel(title);

        labelName.setPreferredSize(new Dimension(150, 25));

        if (value instanceof BigDecimal) {
            fieldValue = new NumberEdit(value);
        }else if (value instanceof Timestamp) {
            fieldValue = new DateEdit((Date) value);
        } else {
            fieldValue = new JTextField(value == null ? "" : value.toString());
        }
        fieldValue.setPreferredSize(new Dimension(200, 25));


        labelName.setLabelFor(fieldValue);
        add(labelName);
        add(fieldValue);
    }

    public String getTitle() {
        return title;
    }

    public Object getValue() {
        return value;
    }


}
