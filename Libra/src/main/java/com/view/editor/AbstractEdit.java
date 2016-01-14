package com.view.editor;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEdit extends JPanel{

    private final String title;
    private List<ChangeEditListener> listeners = new ArrayList<ChangeEditListener>();

    public AbstractEdit(String title) {
        this.title = title;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(270, 27));
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        if(title != null && !title.isEmpty()){
            JLabel labelName = new JLabel(title);
            labelName.setPreferredSize(new Dimension(95, 25));
            add(labelName, BorderLayout.WEST);
        }
    }

    public void addChangeEditListener(ChangeEditListener listener) {
        listeners.add(listener);
    }

    public void removeChangeEditListener(ChangeEditListener listener) {
        listeners.remove(listener);
    }

    protected void fireChangeEditEvent(Object source) {
        System.out.println("Hello!!");
        for (ChangeEditListener hl : listeners)
            hl.changeEdit(source);
    }

    public abstract void setValue();

    public abstract Object getValue();

    public String getTitle() {
        return title;
    }
}
