package com.test.comps;

import javax.swing.*;

public class TLabel extends JLabel implements IDesign {

    @Override
    public void prepareProperties(TProp prop) {
        prop.put("text", getText());
        prop.put("isOpaque", getText());
    }

    @Override
    public void prepareComponent(TProp prop) {
        setText(prop.fetch("text"));
        if(prop.fetch("isOpaque") != null)
            setOpaque(prop.fetch("isOpaque"));
    }
}
