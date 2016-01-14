package com.bin;

import com.util.Libra;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MainFrame extends JFrame {

    public MainFrame(String title) {
        super(title);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                Libra.libraService.close();
                System.exit(0);
            }
        });

        add(new LibraMenu(), BorderLayout.NORTH);

        add(new ScalePanel());
        setSize(800, 500);
        setLocationRelativeTo(null);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }
}
