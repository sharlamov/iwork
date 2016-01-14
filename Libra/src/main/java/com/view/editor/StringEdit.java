package com.view.editor;

import com.model.DataSet;

import javax.swing.*;
import java.awt.*;

/**
 * Created by sharlamov on 09.01.2016.
 */
public class StringEdit extends AbstractEdit {

    private JTextField field;
    private DataSet dataSet;

    public StringEdit(String title, DataSet dataSet) {
        super(title);
        this.dataSet = dataSet;
        field = new JTextField();
        setValue();
        add(field, BorderLayout.CENTER);
    }

    public StringEdit(String title, DataSet dataSet, boolean isEdit) {
        this(title, dataSet);
        field.setEditable(isEdit);
    }

    @Override
    public void setValue() {
        if(dataSet != null && !dataSet.isEmpty()){
            Object obj = dataSet.getValueByName(getTitle(), 0);
            field.setText(obj == null ? "" : obj.toString());
        }
    }

    @Override
    public Object getValue() {
        return field.getText();
    }

    public void putValue(String edit){
        field.setText(edit);
    }
}
