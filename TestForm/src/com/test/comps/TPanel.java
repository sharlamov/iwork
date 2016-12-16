package com.test.comps;


import javax.swing.*;

public class TPanel extends JPanel implements IDesign {

    public TPanel() {
        super(null);
    }

    public TPanel(TProp prop) throws Exception {
        this();
        load(prop);
    }

    @Override
    public void prepareProperties(TProp prop) {

    }

    @Override
    public void prepareComponent(TProp prop) {

    }

}
