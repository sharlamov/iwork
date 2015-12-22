package com.bin;

import javax.swing.*;
import java.awt.*;

/**
 * Created by sharlamov on 21.12.2015.
 */
public class LibraToolBar extends JToolBar {

    JButton b1 = new JButton("Добавить");

    public LibraToolBar() {
        super(HORIZONTAL);
        setFloatable(false);
        add(b1);
    }
}
