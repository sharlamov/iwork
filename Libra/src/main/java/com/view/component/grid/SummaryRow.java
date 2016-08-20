package com.view.component.grid;

import com.util.Fonts;
import com.util.Libra;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.Map;

public class SummaryRow extends JPanel {

    public SummaryRow() {
        super(new FlowLayout(FlowLayout.CENTER, 35, 5));
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        setPreferredSize(new Dimension(500, 50));
    }

    public void publishSummary(Map<String, String> map) {
        removeAll();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            addLabel(entry.getKey(), entry.getValue());
        }
        revalidate();
        repaint();
    }

    public void addLabel(String name, String value) {
        JLabel label = new JLabel(Libra.lng(name) + " " + value);
        label.setFont(Fonts.bold18);
        add(label);
    }
}
