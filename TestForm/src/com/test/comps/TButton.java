package com.test.comps;

import javax.swing.*;

public class TButton extends JButton implements IDesign {
    @Override
    public void prepareProperties(TProp prop) {
        prop.put("text", getText());
    }

    @Override
    public void prepareComponent(TProp prop) {
        setText(prop.fetch("text"));
    }
}
