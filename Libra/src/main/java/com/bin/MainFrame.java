package com.bin;

import com.util.Libra;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(String title) {
        super(title);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                exit();
            }
        });

        add(new LibraMenu(), BorderLayout.NORTH);

        add(new LibraBoard());
        setSize(800, 500);
        setLocationRelativeTo(null);
        if (Libra.autoLogin != 1)
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    public static void exit() {
        Libra.libraService.close();
        System.exit(0);
    }
}
