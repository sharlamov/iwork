package com.view.editor;

import com.model.DataSet;

import javax.swing.*;
import java.awt.*;

public class StringEdit extends AbstractEdit {

    public StringEdit(String title, DataSet dataSet) {
        super(title, new JTextField(), dataSet);
    }

    @Override
    public void setValue(Object obj) {
        ((JTextField)getField()).setText(obj == null ? "" : obj.toString());
    }

    @Override
    public Object getValue() {
        return ((JTextField)getField()).getText();
    }
}
