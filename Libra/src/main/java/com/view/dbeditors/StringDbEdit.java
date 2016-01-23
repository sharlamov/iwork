package com.view.dbeditors;

import com.model.DataSet;

import javax.swing.*;
import java.awt.*;

public class StringDbEdit extends DbEdit {

    public StringDbEdit(String fieldName, DataSet dataSet) {
        super(fieldName, new JFormattedTextField(), dataSet);
    }

    public void setEditable(boolean isEditable) {
        getField().setEnabled(isEditable);
        ((JFormattedTextField) getField()).setDisabledTextColor(Color.black);
    }

    @Override
    public Object getFieldValue() {
        return ((JFormattedTextField) getField()).getValue();
    }

    @Override
    public void setFieldValue(Object object) {
        ((JFormattedTextField) getField()).setValue(object);
    }
}
