package com.bin;

import com.util.Libra;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LibraMenu extends JMenuBar implements ActionListener {

    JMenu menuFile;

    JMenuItem menuExit;

    public LibraMenu() {
        menuFile = new JMenu(Libra.translate("file"));

        menuExit = new JMenuItem(Libra.translate("exit"));
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
