package com.view.dbeditors;

import com.model.DataSet;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import java.awt.*;
import java.util.Date;

public class DateDbEdit extends DbEdit {

    private JDateChooser fld;

    public DateDbEdit(String fieldName, DataSet dataSet) {
        super(fieldName, new JDateChooser(), dataSet);
        fld = (JDateChooser) getField();
        fld.getDateEditor().getUiComponent().addFocusListener(this);
    }

    public DateDbEdit(String fieldName, DataSet dataSet, String format) {
        this(fieldName, dataSet);
        fld.setDateFormatString(format);
    }

    public void setEditable(boolean isEditable) {
        fld.setEnabled(isEditable);
        ((JTextFieldDateEditor) fld.getDateEditor())
                .setDisabledTextColor(isEditable ? Color.black : Color.darkGray);

        if (isEditable)
            fld.getCalendarButton().setPreferredSize(fld.getCalendarButton().getMinimumSize());
        else
            fld.getCalendarButton().setPreferredSize(new Dimension(1, 1));
    }

    @Override
    public Object getFieldValue() {
        return fld.getDate();
    }

    @Override
    public void setFieldValue(Object object) {
        ((JDateChooser) getField()).setDate((Date) object);
    }
}