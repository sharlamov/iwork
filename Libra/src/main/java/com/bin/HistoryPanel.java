package com.bin;

import com.enums.SearchType;
import com.model.CustomItem;
import com.model.DataSet;
import com.util.Fonts;
import com.util.Libra;
import com.util.Pictures;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Date;

import static com.util.Libra.eMsg;

public class HistoryPanel extends JPanel {


    private Dimension dimension = new Dimension(Integer.MAX_VALUE, 90);

    public HistoryPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        Dimension size = new Dimension(200, 300);
        setMaximumSize(size);
        setPreferredSize(size);
    }

    public void refreshData(BigDecimal id) {
        removeAll();
        try {
            DataSet dataSet = Libra.libraService.executeQuery(SearchType.HISTORY.getSql(), new DataSet("id", id));
            for (Object[] objects : dataSet) {
                addInfo((BigDecimal) objects[0], (Date) objects[1], (CustomItem) objects[2], (BigDecimal) objects[3]);
            }
        } catch (Exception ex) {
            eMsg(ex.getMessage());
        }
        revalidate();
        repaint();
    }


    public void addInfo(BigDecimal direction, Date date, CustomItem user, BigDecimal weight) {
        JPanel p = new JPanel(new BorderLayout());
        p.setMaximumSize(dimension);
        p.setPreferredSize(dimension);
        p.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        JLabel l = new JLabel(direction.intValue() == 1 ? Pictures.loadedIcon : Pictures.unloadedIcon);
        p.add(l, BorderLayout.WEST);
        JLabel userLabel = new JLabel(user.toString(), SwingConstants.CENTER);
        p.add(userLabel, BorderLayout.NORTH);
        JLabel dateLabel = new JLabel(Libra.dateTimeFormat.format(date), SwingConstants.CENTER);
        p.add(dateLabel, BorderLayout.CENTER);
        JLabel weightLabel = new JLabel(String.valueOf(weight), SwingConstants.CENTER);
        p.add(weightLabel, BorderLayout.EAST);
        weightLabel.setForeground(Color.green);
        weightLabel.setFont(Fonts.bold24);
        add(p);
    }

}
