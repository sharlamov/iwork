package com.test.comps.grid;

import com.test.comps.interfaces.ITranslator;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.Map;

public class SummaryRow extends JPanel {

    private final Font fnt;
    private final ITranslator translator;

    public SummaryRow(ITranslator translator) {
        super(new FlowLayout(FlowLayout.CENTER, 35, 5));
        this.translator = translator;
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        setPreferredSize(new Dimension(500, 50));
        fnt = new Font("Courier", Font.BOLD, 18);
    }

    public void publishSummary(Map<String, String> map) {
        removeAll();
        map.entrySet().forEach(this::addLabel);
        revalidate();
        repaint();
    }

    private void addLabel(Map.Entry<String, String> entry) {
        JLabel label = new JLabel(translator.lng(entry.getValue()) + " " + entry.getValue());
        label.setFont(fnt);
        add(label);
    }
}
