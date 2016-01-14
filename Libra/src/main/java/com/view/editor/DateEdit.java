package com.view.editor;

import com.model.DataSet;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

/**
 * Created by sharlamov on 09.01.2016.
 */
public class DateEdit extends AbstractEdit {

    private JDateChooser field;
    private final DataSet dataSet;

    public DateEdit(String title, DataSet dataSet) {
        super(title);
        this.dataSet = dataSet;

        field = new JDateChooser();
        setValue();
        add(field, BorderLayout.CENTER);
    }

    @Override
    public void setValue() {
        if(dataSet != null && !dataSet.isEmpty()){
            Object obj = dataSet.getValueByName(getTitle(), 0);
            field.setDate((Date) obj);
        }
    }

    @Override
    public Object getValue() {
        return field.getDate();
    }
}
