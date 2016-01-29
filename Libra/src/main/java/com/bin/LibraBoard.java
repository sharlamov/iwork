package com.bin;

import com.enums.ArmType;
import com.util.Libra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class LibraBoard extends JPanel {

    public LibraBoard() {
        super(new BorderLayout());
        ScaleOnlinePanel board = new ScaleOnlinePanel();
        add(board, BorderLayout.NORTH);
        ImageIcon icon = Libra.createImageIcon("images/middle.gif");
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("<html><body width='150'>Приход</body></html>", icon, new LibraPanel(ArmType.IN), "Весовая вьезд");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_F1);
        tabbedPane.addTab("<html><body width='150'>Расход</body></html>", icon, new LibraPanel(ArmType.OUT), "Весовая выезд");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_F2);
        add(tabbedPane, BorderLayout.CENTER);
    }

}
