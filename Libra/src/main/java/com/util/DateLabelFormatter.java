package com.util;

import javax.swing.*;
import java.text.ParseException;
import java.util.Calendar;

public class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {

    @Override
    public Object stringToValue(String text) throws ParseException {
        return Libra.dateFormat.parseObject(text);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value != null) {
            Calendar cal = (Calendar) value;
            return Libra.dateFormat.format(cal.getTime());
        }
        return "";
    }

}