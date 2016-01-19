package com.view.editor;

import com.model.DataSet;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEdit extends JPanel{

    private final String title;
    private JComponent field;
    private DataSet dataSet;
    private List<ChangeEditListener> listeners = new ArrayList<ChangeEditListener>();

    public AbstractEdit(String title, JComponent field, DataSet dataSet) {
        this.title = title;
        this.field = field;
        this.dataSet = dataSet;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(270, 27));
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        if(title != null && !title.isEmpty()){
            JLabel labelName = new JLabel(title);
            labelName.setPreferredSize(new Dimension(95, 25));
            add(labelName, BorderLayout.WEST);
        }

        setValue(getDbValue());
        add(field, BorderLayout.CENTER);
    }

    public void addChangeEditListener(ChangeEditListener listener) {
        listeners.add(listener);
    }

    protected void fireChangeEditEvent(Object source) {
        for (ChangeEditListener hl : listeners)
            hl.changeEdit(source);
    }

    protected Object getDbValue(){
        return (dataSet == null || dataSet.isEmpty()) ? null : dataSet.getValueByName(title, 0);
    }

    public abstract void setValue(Object obj);

    public abstract Object getValue();

    public String getTitle() {
        return title;
    }

    public Component getField() {
        return field;
    }

    public void setField(JComponent field) {
        this.field = field;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }
}
