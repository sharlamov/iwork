package com.test.comps;

import com.test.comps.interfaces.IComp;
import com.test.comps.interfaces.ISettings;

import javax.swing.*;
import java.awt.event.FocusEvent;

public class TLabel extends JLabel implements IComp, ISettings {


    public void prepareProperties(TProp prop) {
        prop.put("text", getText());
        prop.put("isOpaque", getText());
    }


    public void prepareComponent(TProp prop) {
        setText(prop.fetch("text"));
        if (prop.fetch("isOpaque") != null)
            setOpaque(prop.fetch("isOpaque"));
    }

    @Override
    public void setChangeable(boolean changeable) {

    }

    @Override
    public void refresh() {

    }

    @Override
    public Object getValue(String name) {
        return ((IComp) getParent()).getValue(name);
    }

    @Override
    public void setValue(String name, Object o) {

    }

    @Override
    public void focusGained(FocusEvent e) {

    }

    @Override
    public void focusLost(FocusEvent e) {

    }
}
