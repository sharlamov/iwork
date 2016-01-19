package com.bin;

import com.driver.ScaleEventListener;
import com.driver.ScalesDriver;
import com.service.SettingsService;
import com.util.Libra;
import com.view.component.weightboard.WeightBoard;
import jssc.SerialPortException;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScaleOnlinePanel extends JPanel implements ActionListener {

    private JButton find = new JButton("Поиск весов", Libra.createImageIcon("images/find.png"));

    public ScaleOnlinePanel() {
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        find.addActionListener(this);

        Libra.manager.initByName(SettingsService.getGroupValues("scales"));
        try {
            initScales();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public void initScales() throws SerialPortException {
        removeAll();
        if (Libra.manager.getScales().isEmpty()) {
            add(find);
        } else {
            for (ScalesDriver driver : Libra.manager.getScales()) {
                driver.openPort();
                add(new WeightBoard(driver, true));
            }
        }
        revalidate();
    }

    public void actionPerformed(ActionEvent e) {
        try {
            Libra.manager.defineScales();
            for (ScalesDriver driver : Libra.manager.getScales()) {
                SettingsService.set("scales." + driver.getComPort(), driver.toString());
            }
            SettingsService.save();
            initScales();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}