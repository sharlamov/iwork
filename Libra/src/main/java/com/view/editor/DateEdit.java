package com.view.editor;

import com.model.DataSet;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class DateEdit extends AbstractEdit {

    public DateEdit(String title, DataSet dataSet) {
        super(title, new JDateChooser(), dataSet);
    }

    @Override
    public void setValue(Object obj) {
        ((JDateChooser)getField()).setDate((Date) obj);
    }

    @Override
    public Object getValue() {
        return ((JDateChooser)getField()).getDate();
    }
}
