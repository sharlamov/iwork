package com.bin;

import com.model.CustomItem;
import com.model.DataSet;
import com.util.Libra;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Date;

import static com.util.Libra.eMsg;

public class HistoryPanel extends JPanel {

    private ImageIcon loaded = Libra.createImageIcon("images/loaded.png", 100, 80);
    private ImageIcon unloaded = Libra.createImageIcon("images/unloaded.png", 100, 80);
    private Font sumaFont = new Font("Courier", Font.BOLD, 24);

    private Dimension dimension = new Dimension(Integer.MAX_VALUE, 90);

    public HistoryPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setMaximumSize(new Dimension(200, 300));
        setPreferredSize(new Dimension(200, 300));
    }

    public void refreshData(BigDecimal id) {
        removeAll();
        try {
            DataSet dataSet = Libra.libraService.getHistory(id);
            for (Object[] objects : dataSet) {
                addInfo((BigDecimal) objects[0], (Date) objects[1], (CustomItem) objects[2], (BigDecimal) objects[3]);
            }
        } catch (Exception ex) {
            eMsg(ex.getMessage());
        }
        revalidate();
    }


    public void addInfo(BigDecimal direction, Date date, CustomItem user, BigDecimal weight) {
        JPanel p = new JPanel(new BorderLayout());
        p.setMaximumSize(dimension);
        p.setPreferredSize(dimension);
        p.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        JLabel l = new JLabel(direction.intValue() == 1 ? loaded : unloaded);
       // l.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        p.add(l, BorderLayout.WEST);
        JLabel userLabel = new JLabel(user.toString(), SwingConstants.CENTER);
        p.add(userLabel, BorderLayout.NORTH);
        JLabel dateLabel = new JLabel(Libra.dateTimeFormat.format(date), SwingConstants.CENTER);
        p.add(dateLabel, BorderLayout.CENTER);
        JLabel weightLabel = new JLabel(String.valueOf(weight), SwingConstants.CENTER);
        p.add(weightLabel, BorderLayout.EAST);
        weightLabel.setForeground(Color.green);
        weightLabel.setFont(sumaFont);
        add(p);
    }

}
