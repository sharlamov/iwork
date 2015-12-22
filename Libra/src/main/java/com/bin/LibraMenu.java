package com.bin;

import javax.swing.*;

public class LibraMenu extends JMenuBar{

    JMenu menuForms;
    JMenu menuProps;
    JMenuItem menuItem0;
    JMenuItem menuItem1;

    public LibraMenu() {
        menuForms = new JMenu("Формы");
        menuProps = new JMenu("Настройки");

        menuItem0 = new JMenuItem("Вьезд");
        menuForms.add(menuItem0);

        menuItem1 = new JMenuItem("Выезд");
        menuForms.add(menuItem1);

        add(menuForms);
        add(menuProps);
    }
}
