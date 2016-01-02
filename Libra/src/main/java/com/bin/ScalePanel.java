package com.bin;

import com.service.LibraService;
import com.util.Libra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class ScalePanel extends JPanel {

    ImageIcon icon = Libra.createImageIcon("images/middle.gif");

    public ScalePanel(LibraService service) {
        super(new BorderLayout());

        add(new ScaleLine(), BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Приход", icon, new LibraPanel(service, 1), "Весовая вьезд");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_F1);
        tabbedPane.addTab("Расход", icon, new LibraPanel(service, 2), "Весовая выезд");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_F2);

        add(tabbedPane, BorderLayout.CENTER);
    }
}
