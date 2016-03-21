package com.bin;

import com.enums.ArmType;
import com.service.LangService;
import com.util.Libra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class LibraBoard extends JPanel {

    public LibraBoard() {
        super(new BorderLayout());
        add(new ScaleOnlinePanel(), BorderLayout.NORTH);

        ImageIcon icon = Libra.createImageIcon("images/middle.gif");
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(getHtmlTitle("tabName0"), icon, new LibraPanel(ArmType.IN));
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_F1);
        tabbedPane.addTab(getHtmlTitle("tabName1"), icon, new LibraPanel(ArmType.OUT));
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_F2);
        add(tabbedPane, BorderLayout.CENTER);
    }

    public String getHtmlTitle(String title) {
        return "<html><body width='150'>" + LangService.trans(title) + "</body></html>";
    }
}
