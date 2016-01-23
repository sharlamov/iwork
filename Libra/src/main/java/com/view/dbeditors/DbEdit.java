package com.view.dbeditors;

import com.model.DataSet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;

public abstract class DbEdit extends JPanel implements FocusListener {

    private final String fieldName;
    private final JComponent field;
    private final DataSet dataSet;
    private List<ChangeEditListener> listeners = new ArrayList<ChangeEditListener>();

    public DbEdit(String fieldName, JComponent field, DataSet dataSet) {
        this.fieldName = fieldName;
        this.field = field;
        this.dataSet = dataSet;

        field.addFocusListener(this);
        setLayout(new BorderLayout());

        add(field, BorderLayout.CENTER);

        setFieldValue(getDbValue());
    }

    public void addChangeEditListener(ChangeEditListener listener) {
        listeners.add(listener);
    }

    protected void fireChangeEditEvent(Object source) {
        for (ChangeEditListener hl : listeners)
            hl.changeEdit(source);
    }

    public Object getDbValue() {
        return (dataSet == null || dataSet.isEmpty()) ? null : dataSet.getValueByName(fieldName, 0);
    }

    public void focusGained(FocusEvent e){
        setBorder(BorderFactory.createLineBorder(Color.GREEN));
    }

    public void focusLost(FocusEvent e){
        setBorder(null);
    }

    public abstract Object getFieldValue();

    public abstract void setFieldValue(Object object);

    public String getFieldName() {
        return fieldName;
    }

    public JComponent getField() {
        return field;
    }

    public DataSet getDataSet() {
        return dataSet;
    }
}
