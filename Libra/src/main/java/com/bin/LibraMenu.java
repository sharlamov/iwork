package com.bin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LibraMenu extends JMenuBar implements ActionListener {

    JMenu menuFile;

    JMenuItem menuExit;

    public LibraMenu() {
        menuFile = new JMenu("Файл");

        menuExit = new JMenuItem("Выход");
        menuExit.addActionListener(this);
        
        menuFile.addSeparator();
        menuFile.add(menuExit);

        add(menuFile);

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(menuExit)) {
            MainFrame.exit();
        }
    }
}
