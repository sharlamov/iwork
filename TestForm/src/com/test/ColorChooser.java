package com.test;


import com.test.comps.TProp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

/**
 * Created by sharlamov on 09.11.2016.
 */
public class ColorChooser extends JPanel {

    private Color color;
    private JButton b;
    private Consumer<TProp> action;


    public ColorChooser(String fName, Color color) {
        setLayout(new GridLayout(1, 2));
        this.color = color;

        setName(fName);
        add(new JLabel(fName));
        b = new JButton("?");
        b.setBackground(color);
        b.addActionListener(this::act);
        add(b);
    }

    void act(ActionEvent event) {
        color = JColorChooser.showDialog(null, "Color Chooser", color);
        if (color != null) {
            b.setBackground(color);
            action.accept(null);
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        b.setBackground(color);
        this.color = color;
    }

    public void setAction(Consumer<TProp> action) {
        this.action = action;
    }
}
